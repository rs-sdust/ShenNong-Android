package com.xz.shangde.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 显示地块信息（边界，名称，作物，物候期等等）
 */

public class FieldPropertyActivity extends AppCompatActivity {

    private Context context;

    private Toolbar tb_field_property;
    private ImageView iv_back_field_property;
    private ImageView iv_delete_field;
    private LinearLayout ll_edit_field;
    private LinearLayout ll_share_field;
//    private RelativeLayout rl_container;
    private ProgressBar progress;

    private MapView mv_properties;
    private TextView tv_field_name_properties;
    private TextView tv_crop_type_properties;
    private TextView tv_area_properties;
    private TextView tv_seed_date_till_now;
    private ImageView iv_phenophase;
    private TextView tv_phenophase;
    private TextView tv_phenophase_date;
    private TextView tv_phenophase2;
    private TextView tv_phenophase_introduction;

    private Field field;
    private LinkedHashMap<Integer,String> croptype_map;
//    private LinkedHashMap<Integer,String> phenType_map;
    private int postion = -1;

    public static final OnlineTileSourceBase GoogleHybrid = new XYTileSource("Google-Hybrid",
            0, 19, 256, ".png", new String[]{
            "http://mt0.google.cn",
            "http://mt1.google.cn",
            "http://mt2.google.cn",
            "http://mt3.google.cn",

    }) {
        @Override
        public String getTileURLString(long pTileIndex) {
            return getBaseUrl() + "/vt/lyrs=y&x=" + MapTileIndex.getX(pTileIndex) + "&y=" +
                    MapTileIndex.getY(pTileIndex) + "&z=" + MapTileIndex.getZoom(pTileIndex);
        }
    };

    private static final int MSE_SUCCESS=0;
    private static final int MSE_DATE_SUCCESS=1;
    private int date_till_now=-1;
    private int duration=-1;
    private String PhenName="";
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSE_SUCCESS:
                    tv_phenophase.setText(PhenName);
                    tv_phenophase2.setText(PhenName);

                    BoundingBox boundingBox=BoundingBox.fromGeoPoints(field.getField_Boundary());
                    mv_properties.zoomToBoundingBox(boundingBox,true);
                    String phen= (String) msg.obj;
                    Log.i("TAG",phen);
                    tv_phenophase_introduction.setText(phen);
                    break;
                case MSE_DATE_SUCCESS:
                    duration= (int) msg.obj;
                    String sowdate=field.getSowdate();
                    sowdate=sowdate.substring(0,10);
                    String s=sowdate+"—";
                    SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date begindate=dft.parse(sowdate);
                        Calendar date = Calendar.getInstance();
                        date.setTime(begindate);
                        date.set(Calendar.DATE, date.get(Calendar.DATE) + duration);
                        Date endDate = dft.parse(dft.format(date.getTime()));
                        s+=dft.format(endDate);
                        tv_phenophase_date.setText(s);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            if ((date_till_now!=-1) && (duration!=-1)){
//                DateView dateView=new DateView(getApplicationContext(),date_till_now,duration);
//                rl_container.addView(dateView);
                progress.setMax(duration);
                progress.setProgress(date_till_now);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_property);

        context=this;

        initView();
        Intent intent = getIntent();
        postion = intent.getIntExtra("position", -1);
        if (postion == -1) {
            Toast.makeText(this, "出错了", Toast.LENGTH_SHORT).show();
        } else {
            initData();
        }

        if (field.getField_Crop()!=-1){
            GetFieldPhenophase();
            GetCropGrowthDay();
        }

        initListener();
    }

    public void initView() {
        tb_field_property = findViewById(R.id.tb_field_property);

        iv_back_field_property =findViewById(R.id.iv_back_field_property);
        iv_back_field_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_delete_field = findViewById(R.id.iv_delete_field);
        ll_edit_field = findViewById(R.id.ll_edit_field);
        ll_share_field = findViewById(R.id.ll_share_field);
//        rl_container=findViewById(R.id.rl_container);
        progress=findViewById(R.id.progress);

        mv_properties = findViewById(R.id.mv_properties);
        tv_field_name_properties = findViewById(R.id.tv_field_name_properties);
        tv_crop_type_properties = findViewById(R.id.tv_crop_type_properties);
        tv_area_properties = findViewById(R.id.tv_area_properties);
        tv_seed_date_till_now = findViewById(R.id.tv_seed_date_till_now);
        iv_phenophase = findViewById(R.id.iv_phenophase);
        tv_phenophase = findViewById(R.id.tv_phenophase);
        tv_phenophase_date = findViewById(R.id.tv_phenophase_date);
        tv_phenophase2 = findViewById(R.id.tv_phenophase2);
        tv_phenophase_introduction = findViewById(R.id.tv_phenophase_introduction);

    }

    public void initData() {
        ShangdeApplication application = (ShangdeApplication) this.getApplication();
        field= application.getField(postion);
        croptype_map=application.getCroptype_map();
//        phenType_map=application.getPhenType_map();

        //设置mapview控件
        mv_properties.setTileSource(GoogleHybrid);
        mv_properties.setMultiTouchControls(true);
        mv_properties.setBuiltInZoomControls(false);
        IMapController mapController = mv_properties.getController();
        mapController.setZoom(17);
        GeoPoint centerPoint = field.getField_Boundary().get(0);
        mapController.setCenter(centerPoint);
        Polygon polygon = new Polygon();
        polygon.setFillColor(Color.argb(75, 81, 173, 88));
        polygon.setPoints(field.getField_Boundary());
        mv_properties.getOverlayManager().add(polygon);
        mv_properties.invalidate();

        //设置其他控件
        tv_field_name_properties.setText(String.valueOf(field.getField_Name()));
        int mu=(int) (field.getField_Area()*9/6000);
        int fen=(int) ((field.getField_Area()*9/6000-mu)*10);
        tv_area_properties.setText(mu+"亩"+fen+"分");
        tv_crop_type_properties.setText(croptype_map.get(field.getField_Crop()));

        if (field.getField_Crop()!=-1){
            String sowdate=field.getSowdate();
            DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d=df.parse(sowdate);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String gap=String.valueOf(((curDate.getTime()-d.getTime())/(60*60*1000*24)));
                date_till_now= Integer.parseInt(gap);
                tv_seed_date_till_now.setText("第"+gap+"天");
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            String PhenName=phenType_map.get(field.getField_Phenophase());
//            Log.i("TAG",PhenName);
//            tv_phenophase.setText(PhenName);
//            tv_phenophase2.setText(PhenName);


            String url = field.getField_Thumb();
            Glide.with(this)
                    .load(url)
                    .into(iv_phenophase);
        }
        else {
            progress.setVisibility(View.INVISIBLE);
        }

    }

    public void initListener(){
        iv_delete_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("是否要删除该地块");//提示内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ShangdeApplication application = (ShangdeApplication) getApplication();
                        application.removeField(postion);
                        new Thread(){
                            @Override
                            public void run() {
                                Delete_field(field.getField_ID());
                            }
                        }.start();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ll_edit_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PrintPropertyActivity.class);
                intent.putExtra("FromActivity","FieldPropertyActivity");
                intent.putExtra("position",postion);
                startActivity(intent);
            }
        });
    }

    //删除地块，DELETE方法，所用接口：Fields/DeleteField?id={id}
    public void Delete_field(int id){
        ShangdeApplication application = (ShangdeApplication) getApplicationContext();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client_Delete_field=new OkHttpClient();
        Request request_Delete_field=new Request.Builder()
                .header("Token",token)
                .url(url+"Fields/DeleteField?id="+id)
                .delete()
                .build();
        Log.i("TAG",url+"Fields/DeleteField?id="+id);
        try {
            Response response_Delete_field=client_Delete_field.newCall(request_Delete_field).execute();
            Log.i("TAG","response:"+response_Delete_field.toString());
            Log.i("TAG","response:"+response_Delete_field.message());
            Log.i("TAG","response:"+response_Delete_field.body().string());
            if (response_Delete_field.isSuccessful()){
                JSONObject jsonObject=new JSONObject(response_Delete_field.body().string());
                boolean status=jsonObject.getBoolean("status");
                if (status){
                    //删除成功
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //下载物候期有关的信息，GET方法，所用接口：Phenophase/GetFieldPhenophase?crop_type=
    // {crop_type}&phen_type={phen_type}
    public void GetFieldPhenophase(){
        new Thread(){
            @Override
            public void run() {
                ShangdeApplication application= (ShangdeApplication) getApplication();
                String url=application.getURL();
                SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
                String token=sp.getString("Token","");

                OkHttpClient client=application.getClient();
                Request request=new Request.Builder()
                        .header("Token",token)
                        .url(url+"phenophase/GetFieldPhenophase?crop_type=" + field.getField_Crop()+ "&phen_type="+field.getField_Phenophase())
                        .build();
                Log.i("TAG","crop_type=" + field.getField_Crop());
                Log.i("TAG", "&phen_type="+field.getField_Phenophase());
                try {
                    Response response=client.newCall(request).execute();
                    Log.i("TAG",response.toString());
                    if (response.isSuccessful()){
                        String s=response.body().string();
                        JSONObject jsonObject=new JSONObject(s);
                        Log.i("TAG",s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONObject data=jsonObject.getJSONObject("data");
                            String phen_detail=data.getString("phen_detail");
                            PhenName=data.getString("phen_type");
                            mhandler.obtainMessage(MSE_SUCCESS,phen_detail).sendToTarget();
                            response.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //获取指定作物的生长周期,GET方法，所用接口：Phenophase/GetCropGrowthDay?croptype={croptype}
    public void GetCropGrowthDay(){
        new Thread(){
            @Override
            public void run() {
                ShangdeApplication application= (ShangdeApplication) getApplication();
                String url=application.getURL();
                SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
                String token=sp.getString("Token","");

                OkHttpClient client=application.getClient();
                Request request=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Phenophase/GetCropGrowthDay?croptype="+field.getField_Crop())
                        .build();
                try {
                    Response response=client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String s=response.body().string();
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONObject data=jsonObject.getJSONObject("data");
                            int growth_days=data.getInt("growth_days");
                            mhandler.obtainMessage(MSE_DATE_SUCCESS,growth_days).sendToTarget();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
