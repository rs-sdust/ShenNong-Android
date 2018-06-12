package com.xz.shangde.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 我的农场，用radiobutton + fragment控制显示地块信息列表和地块在地图上的位置
 */

public class FieldsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView iv_back_fields;
    private RadioGroup rg_fields_map;
    private RadioButton rb_show_fiels;
    private RadioButton rb_show_map;
    private ImageView iv_add_farm;
    private FrameLayout fl_container;

    private FragmentManager fm;
    private FragmentFieldsList fragmentFieldsList;
    private FragmentMapview fragmentMapview;

    private static final int MSE_DOWNLOAD_SUCCESS=0;
    private static final int MSE_DOWNLOAD_FAILED=1;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSE_DOWNLOAD_SUCCESS:
                    fm=getSupportFragmentManager();
                    fragmentFieldsList=new FragmentFieldsList();
                    fragmentMapview =new FragmentMapview();

                    ShangdeApplication application= (ShangdeApplication) getApplication();
                    ArrayList<Field> fields=application.getFields();

                    Bundle bundle=new Bundle();
                    bundle.putSerializable("fields",fields);
                    fragmentFieldsList.setArguments(bundle);

                    FragmentTransaction ft=fm.beginTransaction();
                    ft.add(R.id.fl_container,fragmentFieldsList,"fragmentFieldsList");
                    ft.commit();
                    break;
                case MSE_DOWNLOAD_FAILED:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);

        initView();
        initDownload();
        initAdd();
        initRadioGroup();

    }

    public void initView(){
        toolbar=findViewById(R.id.tb_fields_map);
        iv_back_fields=findViewById(R.id.iv_back_fields);
        iv_back_fields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rg_fields_map=findViewById(R.id.rg_fields_map);
        rb_show_fiels=findViewById(R.id.rb_show_fiels);
        rb_show_map=findViewById(R.id.rb_show_map);
        iv_add_farm=findViewById(R.id.iv_add_farm);
        fl_container=findViewById(R.id.fl_container);

    }

    public void initDownload(){
        new Thread(){
            @Override
            public void run() {
                DownloadData();
            }
        }.start();
    }

    public void initAdd(){
        iv_add_farm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AddFarmActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void initRadioGroup(){
        rg_fields_map.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_show_fiels:
                        rb_show_fiels.setBackgroundResource(R.drawable.radiobutton_top_left_checked_shape);
                        rb_show_map.setBackgroundResource(R.drawable.radiobutton_top_right_uncheck_shape);
                        FragmentTransaction ft1 = fm.beginTransaction();
                        if (fragmentFieldsList.isAdded()){
                            ft1.show(fragmentFieldsList);
                            ft1.hide(fragmentMapview);
                        }else {
                            ft1.add(R.id.fl_container,fragmentFieldsList,"fragmentFieldsList");
                            ft1.hide(fragmentMapview);
                        }
                        ft1.commit();
                        break;
                    case R.id.rb_show_map:
                        rb_show_map.setBackgroundResource(R.drawable.radiobutton_top_right_checked_shape);
                        rb_show_fiels.setBackgroundResource(R.drawable.radiobutton_top_left_uncheck_shape);
                        FragmentTransaction ft2 = fm.beginTransaction();
                        if (fragmentMapview.isAdded()){
                            ft2.show(fragmentMapview);
                            ft2.hide(fragmentFieldsList);
                        }else {
                            ft2.add(R.id.fl_container, fragmentMapview,"fragmentMapview");
                            ft2.hide(fragmentFieldsList);
                        }
                        ft2.commit();
                        break;
                }
            }
        });
    }

    //下载所有地块信息并存入application中，GET方法，所用接口：Fields/GetFields?farmid={farmid}
    public void DownloadData(){
        ShangdeApplication application = (ShangdeApplication) getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client_DownloadData = new OkHttpClient();

        Request request_DownloadData = new Request.Builder()
                .header("Token", token)
                .url(url + "Fields/GetFields?farmid="+application.getFarm().getFarm_ID())
                .build();
        Log.i("TAG","GetFieldPhenophase");
        try {
            Response response_DownloadData = client_DownloadData.newCall(request_DownloadData).execute();
            if (response_DownloadData.isSuccessful()) {
                String str=response_DownloadData.body().string();
                Log.i("TAG",response_DownloadData.toString());
                Log.i("TAG",str);
                JSONObject jsonObject = new JSONObject(str);
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    //成功获取到所有地块的信息
                    JSONArray data=jsonObject.getJSONArray("data");
                    ArrayList<Field> fields=new ArrayList<>();
                    for (int i=0;i<data.length();i++){
                        JSONObject object=data.getJSONObject(i);
                        int id=object.getInt("id");
                        Log.i("TAG","id:"+id);
                        int farm_id=object.getInt("farm");
                        Log.i("TAG","farm_id:"+farm_id);
                        String name=object.getString("name");
                        Log.i("TAG","name:"+name);
                        String geom=object.getString("geom");
                        Log.i("TAG","geom:"+geom);
                        float area= Float.parseFloat(object.getString("area"));
                        Log.i("TAG","area:"+area);
                        String date=object.getString("createdate");
                        Log.i("TAG","date:"+date);
                        int currentcrop=object.getInt("currentcrop");
                        Log.i("TAG","currentcrop:"+currentcrop);
                        String sowdate=object.getString("sowdate");
                        Log.i("TAG","sowdate:"+sowdate);
                        int phenophase=object.getInt("phenophase");
                        Log.i("TAG","phenophase:"+phenophase);
                        String thumb=object.getString("thumb");
                        Log.i("TAG","thumb:"+thumb);

                        ArrayList<GeoPoint> boundary=new ArrayList<>();
                        geom=geom.substring(7,geom.length());
                        geom=geom.replace("(","");
                        geom=geom.replace(")","");
                        geom=geom.trim();
                        Log.i("TAG","geom:"+geom);
                        String[] strings=geom.split("[\\,]");
                        for (int j=0;j<strings.length;j++){
                            String s=strings[j];
                            String lat=s.split("[\\s]")[0];
                            String lon=s.split("[\\s]")[1];
                            Log.i("TAG","lat:"+lat+"lon:"+lon);
                            GeoPoint point=new GeoPoint(Double.valueOf(lat),Double.valueOf(lon));
                            Log.i("TAG","point:"+point.toString());
                            boundary.add(point);
                        }

                        Field field=new Field(id,farm_id,name,boundary,area,currentcrop,date,sowdate,phenophase,thumb);
                        fields.add(field);
                    }
                    application.setFields(fields);
                    mhandler.obtainMessage(MSE_DOWNLOAD_SUCCESS).sendToTarget();
                }
            }
            else {
                Log.i("TAG",response_DownloadData.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
