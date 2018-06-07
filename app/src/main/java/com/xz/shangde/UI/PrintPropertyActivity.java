package com.xz.shangde.UI;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.shangde.Area;
import com.xz.shangde.CropType;
import com.xz.shangde.Farm;
import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxq on 2018/5/21.
 */

public class PrintPropertyActivity extends AppCompatActivity {

    private Context context;

    private Toolbar tb_print_property;
    private MapView mv_preview_property;
    private EditText ev_name_print_property;
    private TextView tv_area_print_property;
    private Spinner spinner_choose_crop_type;
    private TextView et_date_print_property;
    private Button btn_print_property_commit;
    private ImageView iv_edit_date_print_property;

    private Area area;
    private ArrayList<GeoPoint> points;
    private int postion = -1;
    private GeoPoint center_point;

    private String FromActivity;

    private Field field_hasknow;

    private ArrayList<CropType> cropTypes = new ArrayList<>();
    private LinkedHashMap<Integer, String> croptype_map;

    public static final OnlineTileSourceBase GoogleHybrid = new XYTileSource("Google-Hybrid", 0,
            19, 256, ".png", new String[]{"http://mt0.google.cn", "http://mt1.google.cn",
            "http://mt2.google.cn", "http://mt3.google.cn",

    }) {
        @Override
        public String getTileURLString(long pTileIndex) {
            return getBaseUrl() + "/vt/lyrs=y&x=" + MapTileIndex.getX(pTileIndex) + "&y=" +
                    MapTileIndex.getY(pTileIndex) + "&z=" + MapTileIndex.getZoom(pTileIndex);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_property);

        context = this;
        initView();
        initData();
        initListener();
    }

    public void initView() {
        tb_print_property = findViewById(R.id.tb_print_property);
        mv_preview_property = findViewById(R.id.mv_preview_property);
        ev_name_print_property = findViewById(R.id.ev_name_print_property);
        tv_area_print_property = findViewById(R.id.tv_area_print_property);
        spinner_choose_crop_type = findViewById(R.id.spinner_choose_crop_type);
        et_date_print_property = findViewById(R.id.et_date_print_property);
        btn_print_property_commit = findViewById(R.id.btn_print_property_commit);
        iv_edit_date_print_property = findViewById(R.id.iv_edit_date_print_property);

        setSupportActionBar(tb_print_property);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_print_property.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ShangdeApplication application = (ShangdeApplication) getApplication();
        croptype_map = application.getCroptype_map();
        Iterator entries = croptype_map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Integer key = (Integer) entry.getKey();
            String value = (String) entry.getValue();
            CropType cropType = new CropType(key, value);
            cropTypes.add(cropType);
        }
        ArrayList<String> cropsname = new ArrayList<>();
        for (int i = 0; i < cropTypes.size(); i++) {
            cropsname.add(cropTypes.get(i).crop_type);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout
                .simple_spinner_item, cropsname);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_choose_crop_type.setAdapter(adapter);
    }

    public void initData() {
        Intent intent = getIntent();
        FromActivity = intent.getStringExtra("FromActivity");
        if (FromActivity.equals("AddFarmActivity")) {
            area = (Area) intent.getSerializableExtra("area");
            points = intent.getParcelableArrayListExtra("geoPoints");
            double center_point_latitude = intent.getDoubleExtra("center_point_latitude", 0);
            double center_point_longitude = intent.getDoubleExtra("center_point_longitude", 0);
            //        GeoPoint center_point=new GeoPoint(Double.valueOf(center_point_latitude),
            // Double.valueOf(center_point_longitude));
            center_point = new GeoPoint(center_point_latitude, center_point_longitude);

            mv_preview_property.setTileSource(GoogleHybrid);
            mv_preview_property.setMultiTouchControls(true);
            mv_preview_property.setBuiltInZoomControls(false);
            //设置地图中心点位置
            IMapController mapController = mv_preview_property.getController();
            mapController.setZoom(15);
            mapController.setCenter(center_point);

            Polygon polygon = new Polygon();
            polygon.setFillColor(Color.argb(75, 255, 0, 0));
            polygon.setPoints(points);
            mv_preview_property.getOverlayManager().add(polygon);
            mv_preview_property.invalidate();

            DecimalFormat Myformat=new DecimalFormat("#.00");
            String s=Myformat.format(area.area_mu);
            tv_area_print_property.setText(s+"亩");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            et_date_print_property.setText(str);
        } else if (FromActivity.equals("FieldPropertyActivity")) {
            postion = intent.getIntExtra("position", -1);
            if (postion == -1) {
                //something wrong
            } else {
                ShangdeApplication application = (ShangdeApplication) getApplication();
                field_hasknow = application.getField(postion);
                points = field_hasknow.getField_Boundary();

                center_point = field_hasknow.getCenterPoint();
                mv_preview_property.setTileSource(GoogleHybrid);
                mv_preview_property.setMultiTouchControls(true);
                mv_preview_property.setBuiltInZoomControls(false);
                //设置地图中心点位置
                IMapController mapController = mv_preview_property.getController();
                mapController.setZoom(15);
                mapController.setCenter(center_point);

                Polygon polygon = new Polygon();
                polygon.setFillColor(Color.argb(75, 255, 0, 0));
                polygon.setPoints(points);
                mv_preview_property.getOverlayManager().add(polygon);
                mv_preview_property.invalidate();

                ev_name_print_property.setText(field_hasknow.getField_Name());
                tv_area_print_property.setText(String.valueOf(field_hasknow.getField_Area()));
                et_date_print_property.setText(field_hasknow.getSowdate());
            }
        }

    }

    public void initListener() {
        iv_edit_date_print_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog
                        .OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = "";
                        date += year + "-";
                        month=month+1;
                        if (month < 10) {
                            String m = "0" + month;
                            date += m;
                        } else {
                            String m = String.valueOf(month);
                            date += m;
                        }
                        if (dayOfMonth < 10) {
                            String day = "0" + dayOfMonth;
                            date = date + "-" + day;
                        } else {
                            String day = String.valueOf(dayOfMonth);
                            date = date + "-" + day;
                        }
                        et_date_print_property.setText(date);
                    }
                };
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);       //获取年月日时分秒
                int month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(context, 0, listener, year, month,
                        day);
                DatePicker dp=dialog.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                dialog.show();
            }
        });


        btn_print_property_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "commit");
                if (et_date_print_property.getText().toString().equals("请选择播种时间")) {
                    Toast.makeText(context, "请选择播种时间", Toast.LENGTH_SHORT).show();
                } else {
                    ShangdeApplication application = (ShangdeApplication) getApplication();
                    Farm farm = application.getFarm();

                    //                    Field field = new Field(farm.getFarm_ID(),
                    // ev_name_print_property.getText().toString(), points,
                    //                            (float) area.area_m2, spinner_choose_crop_type
                    // .getSelectedItemPosition(),"","", -1,
                    //                            et_date_print_property.getText().toString());
                    //                    field.setCenterPoint(center_point);
                    if (FromActivity.equals("FieldPropertyActivity")) {
                        //todo 通过application中的editField方法更改数据,之后上传到数据库
                        Log.i("TAG", "FieldPropertyActivity");
                        new Thread() {
                            @Override
                            public void run() {
                                Log.i("TAG", "in thread");
                                int id = field_hasknow.getField_ID();
                                Log.i("TAG", "id:" + id);
                                String name = ev_name_print_property.getText().toString();
                                Log.i("TAG", "name:" + name);
                                String sowdate = et_date_print_property.getText().toString();
                                Log.i("TAG", "sowdate:" + sowdate);
                                int crop = spinner_choose_crop_type.getSelectedItemPosition();
                                Log.i("TAG", "crop:" + crop);
                                float a = Float.parseFloat(tv_area_print_property.getText()
                                        .toString());
                                Log.i("TAG", "area:" + a);
                                UpdateField(id, name, a, sowdate, crop, "null");
                            }
                        }.start();
                    } else if (FromActivity.equals("AddFarmActivity")) {
                        //todo 转变为Field对象，上传数据，通过addField方法将field更新至application中
                        Log.i("TAG", "add field");
                        new Thread() {
                            @Override
                            public void run() {
                                Log.i("TAG", "in thread");
                                ShangdeApplication application = (ShangdeApplication)
                                        getApplication();
                                Farm farm = application.getFarm();
                                Log.i("TAG", "before AddField");
                                //                                spinner_choose_crop_type
                                // .getSelectedItemPosition()
                                //                                AddField(farm.getFarm_ID(),
                                // ev_name_print_property.getText().toString(), points, (float)
                                //                                        area.area_m2, 0,
                                // "2018-10-1", -1, "2018-10-5");

                                Log.i("TAG", "AddField");
                                String url = application.getURL();
                                SharedPreferences sharedPreferences = getSharedPreferences
                                        ("User", Context.MODE_PRIVATE);
                                String token = sharedPreferences.getString("Token", "");

                                String geom = "POLYGON((";
                                for (GeoPoint geoPoint : points) {
                                    geom += geoPoint.getLatitude();
                                    geom += " ";
                                    geom += geoPoint.getLongitude();
                                    geom += ",";
                                }
                                geom = geom.substring(0, geom.length() - 1);
                                geom += "))";

                                Log.i("TAG", geom);
                                OkHttpClient client_AddField = application.getClient();
                                Log.i("TAG", "client is fine");
                                String farmid = String.valueOf(farm.getFarm_ID());
                                Log.i("TAG", "farmid is fine");
                                String str = ev_name_print_property.getText().toString();
                                Log.i("TAG", "str is fine");
                                FormBody formBody_AddField = new FormBody.Builder().add("farm",
                                        farmid).add("name", str).add("geom", geom).add("area",
                                        String.valueOf(area.area_m2)).add("currentcrop", String
                                        .valueOf(cropTypes.get(spinner_choose_crop_type
                                                .getSelectedItemPosition()).id)).add("sowdate",
                                        et_date_print_property.getText().toString()).add
                                        ("phenophase", String.valueOf(-1)).add("thumb",
                                        "http://img.taopic" + "" +
                                                ".com/uploads/allimg/140327/235088-14032GP44387.jpg").build();

                                Log.i("TAG", "formbody is correct");
                                Request request_AddField = new Request.Builder().header("Token",
                                        token).url(url + "Fields/AddField").post
                                        (formBody_AddField).build();
                                Log.i("TAG", "url:" + url + "Fields/AddField");
                                Log.i("TAG", "token:" + token);
                                try {
                                    Response response_AddField = client_AddField.newCall
                                            (request_AddField).execute();
                                    if (response_AddField.isSuccessful()) {
                                        String s = response_AddField.body().string();
                                        Log.i("TAG", s);
                                        JSONObject jsonObject = new JSONObject(s);
                                        boolean status = jsonObject.getBoolean("status");
                                        if (status) {
                                            //成功添加地块
                                        }
                                    } else {
                                        Log.i("TAG", response_AddField.message());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                    finish();
                }
            }
        });
    }

    //新增地块，POST方法，所用接口：Fields/AddField
    public void AddField(int Field_Farm_Belong, String Field_Name, ArrayList<GeoPoint>
            Field_Boundary, float Field_Area, int Field_Crop, String sowdate, int phenophase,
                         String Field_Thumb) {
        Log.i("TAG", "AddField");
        ShangdeApplication application = (ShangdeApplication) getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        String geom = "POLYGON((";
        for (GeoPoint geoPoint : Field_Boundary) {
            geom += geoPoint.getLatitude();
            geom += " ";
            geom += geoPoint.getLongitude();
            geom += ",";
        }
        geom = geom.substring(0, geom.length() - 1);
        geom += "))";

        Log.i("TAG", geom);
        OkHttpClient client_AddField = new OkHttpClient();
        FormBody formBody_AddField = new FormBody.Builder().add("farm", String.valueOf
                (Field_Farm_Belong)).add("name", Field_Name).add("geom", geom).add("area", String
                .valueOf(Field_Area)).add("currentcrop", String.valueOf(Field_Crop)).add
                ("sowdate", sowdate).add("phenophase", String.valueOf(phenophase)).add("thumb",
                Field_Thumb).build();

        Request request_AddField = new Request.Builder().header("Token", token).url(url +
                "Fields/AddField").post(formBody_AddField).build();
        Log.i("TAG", "url:" + url + "Fields/AddField");
        Log.i("TAG", "token:" + token);
        try {
            Response response_AddField = client_AddField.newCall(request_AddField).execute();
            if (response_AddField.isSuccessful()) {
                String s = response_AddField.body().string();
                Log.i("TAG", s);
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    //成功添加地块
                    Toast.makeText(context,"您已成功添加地块",Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i("TAG", response_AddField.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //修改地块，POST方法，所用接口:Fields/UpdateField
    public void UpdateField(int field_id, String Field_Name, float Field_Area, String sowdate,
                            int Field_Crop, String Field_Thumb) {
        Log.i("TAG", "in method");
        ShangdeApplication application = (ShangdeApplication) getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client_UpdateField = new OkHttpClient();
        FormBody formBody_UpdateField = new FormBody.Builder().add("id", String.valueOf(field_id)
        ).add("name", Field_Name).add("area", String.valueOf(Field_Area)).add("sowdate", sowdate)
                .add("currentcrop", String.valueOf(Field_Crop)).add("thumb", Field_Thumb).build();
        Request request_UpdateField = new Request.Builder().header("Token", token).url(url +
                "Fields/UpdateField").post(formBody_UpdateField).build();
        Log.i("TAG", "update field");
        try {
            Response response_UpdateField = client_UpdateField.newCall(request_UpdateField)
                    .execute();
            Log.i("TAG", response_UpdateField.toString());
            if (response_UpdateField.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response_UpdateField.body().string());
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    //成功更新地块
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
