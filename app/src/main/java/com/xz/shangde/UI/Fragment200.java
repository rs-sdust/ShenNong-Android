package com.xz.shangde.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.shangde.Field;
import com.xz.shangde.Field_live;
import com.xz.shangde.R;
import com.xz.shangde.RSInfo;
import com.xz.shangde.RSInfo_Grade;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxq on 2018/4/26.
 */

public class Fragment200 extends Fragment {

    private Context context;
    private MapView mapView;
    private ImageView iv_photo_200;
    private ImageView iv_location_200;
    private ImageView iv_zoom_200;
    private Spinner spinner_image_layers_200;

    private ArrayList<Field> fields;
    //存储绘制的所有地块和marker
    private ArrayList<Polygon> polygons = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    //可以为空
    private GeoPoint Location_now;
    private ArrayList<Field_live> field_lives = new ArrayList<>();
    private ArrayList<RSInfo> rsInformation = new ArrayList<>();
    private ArrayList<RSInfo_Grade> rsInfo_grades = new ArrayList<>();

    private RelativeLayout rl_gradeview;

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

    private final int[] colors = {0xCCC00000, 0xCCFB7305, 0xCCFFF12E, 0xCCA0FF00,0xCC38A800};

    private static final int MSE_DOWNLOAD_ALL_SUCCESS = 0;
    private static final int MSE_FAILED = 1;
    private Handler myhandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSE_DOWNLOAD_ALL_SUCCESS:
                    for (int i = 0; i < fields.size(); i++) {
                        Field field = fields.get(i);
                        Polygon polygon = new Polygon();
                        polygon.setPoints(field.getField_Boundary());
                        polygons.add(polygon);
                        mapView.getOverlayManager().add(polygon);
                    }
                    mapView.invalidate();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_200, container, false);

        context = getContext();

        DownloadAllData();

        initView(view);

        initMapView(view);

        initSpinnerListener();

        return view;
    }

    public void initView(View view) {
        spinner_image_layers_200 = view.findViewById(R.id.spinner_image_layers_200);
        iv_photo_200 = view.findViewById(R.id.iv_photo_200);
        iv_location_200 = view.findViewById(R.id.iv_location_200);
        iv_zoom_200 = view.findViewById(R.id.iv_zoom_200);
        rl_gradeview = view.findViewById(R.id.rl_gradeview);

        iv_photo_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
                Log.i("Tag", " Activity.DEFAULT_KEYS_DIALER: " + Activity.DEFAULT_KEYS_DIALER);
            }
        });

        iv_zoom_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<GeoPoint> points = new ArrayList<>();
                for (int i = 0; i < fields.size(); i++) {
                    points.addAll(fields.get(i).getField_Boundary());
                }
                BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
                mapView.zoomToBoundingBox(boundingBox, true);
            }
        });

        iv_location_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否开启权限！
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission
                        .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission
                                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "权限不够", Toast.LENGTH_LONG).show();
                    return;
                }

                //获取一个地址管理者，获取的方法比较特殊，不是直接new出来的
                LocationManager locationManager = (LocationManager) context.getSystemService
                        (context.LOCATION_SERVICE);

                //使用GPS获取上一次的地址，这样获取到的信息需要多次，才能够显示出来，所以后面有动态的判断
                Location location = locationManager.getLastKnownLocation(locationManager
                        .GPS_PROVIDER);
                if (location != null) {
                    Location_now = new GeoPoint(Location2Geopoint(location));
                    Toast.makeText(context, "Longitude:" + location.getLongitude() + ";Latitude"
                            + location.getLatitude(), Toast.LENGTH_SHORT).show();
                    IMapController mapController = mapView.getController();
                    mapController.setZoom(16);
                    mapController.setCenter(Location_now);
                    Marker marker=new Marker(mapView);
                    marker.setPosition(Location_now);
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_place_black_24dp));
                    mapView.getOverlays().add(marker);
                    mapView.invalidate();
                } else {
                    Toast.makeText(context, "位置为空", Toast.LENGTH_SHORT).show();
                }

                //判断是否用户打开了GPS开关，这个和获取权限没关系
                GPSisopen(locationManager);

                //获取时时更新，第一个是Provider,第二个参数是更新时间1000ms，第三个参数是更新半径，第四个是监听器
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 8, new
                        LocationListener() {

                    @Override
                    //当地理位置发生改变的时候调用
                    public void onLocationChanged(Location location) {
//                        IMapController mapController = mapView.getController();
//                        mapController.setZoom(12);
//                        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location
//                                .getLongitude());
//                        mapController.setCenter(startPoint);
//                        mapView.invalidate();
//
//                        Location_now = new
//
//                                GeoPoint(Location2Geopoint(location));
                    }

                    /* 当状态发生改变的时候调用*/
                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                        Log.d("GPS_SERVICES", "状态信息发生改变");

                    }

                    /*当定位者启用的时候调用*/
                    @Override
                    public void onProviderEnabled(String s) {
                        Log.d("TAG", "onProviderEnabled: ");

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        Log.d("TAG", "onProviderDisabled: ");
                    }
                });
            }


        });
    }

    public void initMapView(View view) {
        mapView = view.findViewById(R.id.map_200);
        mapView.setTileSource(GoogleHybrid);

        //add default zoom buttons, and ability to zoom with 2 fingers (multi-touch)
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(39.9035849157, 116.3977047882);
        mapController.setCenter(startPoint);

        //        ShangdeApplication application = (ShangdeApplication) getActivity()
        // .getApplication();
        //        if (application.isHasDownload()) {
        //            fields = application.getFields();
        //
        //
        //            for (int i = 0; i < fields.size(); i++) {
        //                Field field = fields.get(i);
        //                Polygon polygon = new Polygon();
        //                polygon.setPoints(field.getField_Boundary());
        //                polygons.add(polygon);
        //                mapView.getOverlayManager().add(polygon);
        //            }
        //            mapView.invalidate();
        //        } else {
        //            fields = null;
        //            Toast.makeText(context, "数据下载错误，请重试", Toast.LENGTH_SHORT).show();
        //        }

        //        Marker startMarker = new Marker(mapView);
        //        startMarker.setPosition(startPoint);
        //        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //        mapView.getOverlays().add(startMarker);

        //同步的get方法获取http数据
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
        //                    Request request = new Request.Builder()
        //                            .url("https://raw.github.com/square/okhttp/master/README
        // .md")//请求接口。如果需要传参拼接到接口后面。
        //                            .build();//创建Request 对象
        //                    Response response = null;
        //                    response = client.newCall(request).execute();//得到Response 对象
        //                    if (response.isSuccessful()) {
        //                        Log.i("kwwl","response.code()=="+response.code());
        //                        Log.i("kwwl","response.message()=="+response.message());
        //                        Log.i("kwwl","res=="+response.body().string());
        //                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
        //                    }
        //                } catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }).start();

        //异步get和post的方法来获取http数据
        //        OkHttpClient client = new OkHttpClient();
        //        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        //        formBody.add("121131131","121131131");//传递键值对参数
        //        Request request = new Request.Builder()//创建Request 对象。
        //                .url("http://apicloud.mob.com/v1/weather/query?key=2560586b2f675&city=通州")
        ////                .post(formBody.build())//传递请求体
        //                .build();
        //        client.newCall(request).enqueue(new Callback() {
        //            @Override
        //            public void onFailure(Call call, IOException e) {
        //                Log.i("kwwl","获取数据失败");
        //            }
        //            @Override
        //            public void onResponse(Call call, Response response) throws IOException {
        //                if(response.isSuccessful()){//回调的方法执行在子线程。
        //                    Log.i("kwwl","获取数据成功了");
        //                    Log.i("kwwl","response.code()=="+response.code());
        //                    Log.i("kwwl","response.body().string()=="+response.body().string());
        //                }
        //            }
        //        });
    }

    public void initData() {
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        field_lives = application.getField_lives();
        rsInformation = application.getRsInfosmation();
        rsInfo_grades = application.getRsInfo_grades();
    }

    public void initSpinnerListener() {
        spinner_image_layers_200.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener
                () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        RemoveAllOverlays();
                        DrawGrade(-1);
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        break;
                    case 1:
                        RemoveAllOverlays();
                        DrawGrade(1);
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        if (!SearchGradeMap(1).isEmpty()) {
                            rl_gradeview.setBackgroundColor(Color.argb(180,106,90,205));
                            GradeView gradeView0 = new GradeView(context, SearchGradeMap(1));
                            rl_gradeview.addView(gradeView0);
                        }
                        break;
                    case 2:
                        RemoveAllOverlays();
                        DrawGrade(2);
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        if (!SearchGradeMap(2).isEmpty()) {
                            rl_gradeview.setBackgroundColor(Color.argb(180,106,90,205));
                            GradeView gradeView1 = new GradeView(context, SearchGradeMap(2));
                            rl_gradeview.addView(gradeView1);
                        }
                        break;
                    case 3:
                        RemoveAllOverlays();
                        DrawGrade(3);
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        if (!SearchGradeMap(3).isEmpty()) {
                            rl_gradeview.setBackgroundColor(Color.argb(180,106,90,205));
                            GradeView gradeView2 = new GradeView(context, SearchGradeMap(3));
                            rl_gradeview.addView(gradeView2);
                        }
                        break;
                    case 4:
                        RemoveAllOverlays();
                        DrawGrade(4);
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        if (!SearchGradeMap(4).isEmpty()) {
                            rl_gradeview.setBackgroundColor(Color.argb(180,106,90,205));
                            GradeView gradeView3 = new GradeView(context, SearchGradeMap(4));
                            rl_gradeview.addView(gradeView3);
                        }
                        break;
                    case 5:
                        RemoveAllOverlays();
                        DrawGrade(5);
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        if (!SearchGradeMap(5).isEmpty()) {
                            rl_gradeview.setBackgroundColor(Color.argb(180,106,90,205));
                            GradeView gradeView4 = new GradeView(context, SearchGradeMap(5));
                            rl_gradeview.addView(gradeView4);
                        }
                        break;
                    case 6:
                        RemoveAllOverlays();
                        DrawGrade(-1);
                        DrawMarkers();
                        rl_gradeview.removeAllViews();
                        rl_gradeview.setBackground(null);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    //相机的返回结果
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (Location_now == null) {
                Toast.makeText(context, "请首先定位当前位置", Toast.LENGTH_LONG).show();
            } else {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");

                //传递bitmap，进入activity
                Bundle b = new Bundle();
                b.putParcelable("bitmap", bitmap);
                b.putParcelable("location_now", Location_now);
                Log.i("TAG", Location_now.toString());
                Intent intent = new Intent(context, UploadPhotoActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        }
    }

    //判断是否用户打开GPS开关，并作指导性操作！
    private void GPSisopen(LocationManager locationManager) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为了获取定位服务，请先打开GPS");
            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //界面跳转
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            //调用显示方法！
            dialog.show();
        }
    }

    private GeoPoint Location2Geopoint(Location location) {
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
        return point;
    }

    //移除所有的东西
    private void RemoveAllOverlays() {
        for (int i = 0; i < polygons.size(); i++) {
            mapView.getOverlays().remove(polygons.get(i));
        }
        for (int i = 0; i < markers.size(); i++) {
            mapView.getOverlays().remove(markers.get(i));
        }
        mapView.invalidate();
        polygons.clear();
        markers.clear();

        rl_gradeview.removeAllViews();
    }

    //从所有信息中找出某一农场地块，某种类型的值
    public int SearchGradeFromMesses(int farm_id, int field_id, int type) {
        int grade = -1;
        for (int i = 0; i < rsInformation.size(); i++) {
            RSInfo info = rsInformation.get(i);
            if ((farm_id == info.getFarm_ID()) && (field_id == info.getField_ID()) && (type ==
                    info.getRS_type())) {
                grade = info.getGrade();
            }
        }
        return grade;
    }

    //从所有信息中找出某一类的grade（Integer）对应关系，对应于Object[2]（color，string）
    public LinkedHashMap<Integer, Object[]> SearchGradeMap(int type) {
        LinkedHashMap<Integer, Object[]> map = new LinkedHashMap<>();

        for (int i = 0; i < rsInfo_grades.size(); i++) {
            if (type == rsInfo_grades.get(i).getType()) {
                Object[] objects = new Object[2];
                objects[0] = colors[rsInfo_grades.get(i).getGrade()];
                objects[1] = rsInfo_grades.get(i).getName();
                map.put(rsInfo_grades.get(i).getGrade(), objects);
            }
        }

        return map;
    }

    //根据农情类型绘制等级图
    public void DrawGrade(int type) {
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        fields = application.getFields();

        HashMap<Integer, Object[]> map_grade = SearchGradeMap(type);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            int grade = SearchGradeFromMesses(field.getField_Farm_Belong(), field.getField_ID(),
                    type);

            Polygon polygon = new Polygon();
            polygon.setPoints(field.getField_Boundary());
            if ((type != -1) && (grade != -1)) {
                polygon.setFillColor((Integer) map_grade.get(grade)[0]);
            }
            polygons.add(polygon);
            mapView.getOverlayManager().add(polygon);
        }
        mapView.invalidate();
    }

    //绘制marker
    public void DrawMarkers() {
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        field_lives = application.getField_lives();

        for (int i = 0; i < field_lives.size(); i++) {
            final Field_live live = field_lives.get(i);
//            String location = live.getLocation();
//            location = location.substring(5, location.length());
//            location = location.replace("(", "");
//            location = location.replace(")", "");
//            location = location.trim();
//            double lat = Double.parseDouble(location.split("[\\s]")[0]);
//            double lon = Double.parseDouble(location.split("[\\s]")[1]);
//            GeoPoint geoPoint = new GeoPoint(lat, lon);

            Marker marker = new Marker(mapView);
            marker.setPosition(live.getLocation());
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Intent intent = new Intent(context, FieldLiveActivity.class);
                    intent.putExtra("data", live);
                    startActivity(intent);
                    return true;
                }
            });
            mapView.getOverlays().add(marker);

            markers.add(marker);
        }
        mapView.invalidate();
    }

    //下载所有数据
    public void DownloadAllData() {
        new Thread() {
            @Override
            public void run() {
                //下载地块类
                DownloadFields();

                ShangdeApplication application = (ShangdeApplication) getActivity()
                        .getApplication();
                fields = application.getFields();

                //下载农情信息表，GET方法，所用接口：Farmwork/GetRsiType
                GetAgricultureinfo(1);
                GetAgricultureinfo(2);
                GetAgricultureinfo(3);
                GetAgricultureinfo(4);
                GetAgricultureinfo(5);

                Log.i("TAG","GetAgricultureinfo finished");
                application.addRsInfosmation(rsInformation);

                //获取地块的实况信息，GET方法，所用接口：Farmwork/GetFieldLive?field={field}
                Log.i("TAG", String.valueOf(fields.size()));
                for (int i = 0; i < fields.size(); i++) {
                    int fieldid = fields.get(i).getField_ID();
                    GetFieldLive(fieldid);
                }
                initData();

                myhandler1.obtainMessage(MSE_DOWNLOAD_ALL_SUCCESS).sendToTarget();
            }
        }.start();
    }

    //下载所有地块信息并存入application中，GET方法，所用接口：Fields/GetFields?farmid={farmid}
    public void DownloadFields() {
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context
                .MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client_DownloadFields = application.getClient();

        Request request_DownloadFields = new Request.Builder().header("Token", token).addHeader
                ("Connection", "close").url(url + "Fields/GetFields?farmid=" + application
                .getFarm().getFarm_ID()).build();
        try {
            Response response_DownloadFields = client_DownloadFields.newCall
                    (request_DownloadFields).execute();
            Log.i("TAG", response_DownloadFields.toString());
            if (response_DownloadFields.isSuccessful()) {
                String str = response_DownloadFields.body().string();
                Log.i("TAG", str);
                JSONObject jsonObject = new JSONObject(str);
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    //成功获取到所有地块的信息
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<Field> fields = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        int id = object.getInt("id");
                        int farm_id = object.getInt("farm");
                        String name = object.getString("name");
                        String geom = object.getString("geom");
                        float area = Float.parseFloat(object.getString("area"));
                        String date = object.getString("createdate");
                        int currentcrop = object.getInt("currentcrop");
                        String sowdate = object.getString("sowdate");
                        int phenophase = object.getInt("phenophase");
                        String thumb = object.getString("thumb");

                        ArrayList<GeoPoint> boundary = new ArrayList<>();
                        geom = geom.substring(7, geom.length());
                        geom = geom.replace("(", "");
                        geom = geom.replace(")", "");
                        geom = geom.trim();
                        Log.i("TAG", "geom:" + geom);
                        String[] strings = geom.split("[\\,]");
                        for (int j = 0; j < strings.length; j++) {
                            String s = strings[j];
                            String lat = s.split("[\\s]")[0];
                            String lon = s.split("[\\s]")[1];
                            Log.i("TAG", "lat:" + lat + "lon:" + lon);
                            GeoPoint point = new GeoPoint(Double.valueOf(lat), Double.valueOf(lon));
                            boundary.add(point);
                        }

                        Field field = new Field(id, farm_id, name, boundary, area, currentcrop,
                                date, sowdate, phenophase, thumb);
                        fields.add(field);
                    }
                    application.setFields(fields);
                }
            } else {
                Log.i("TAG", response_DownloadFields.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取某种类型的农情信息，GET方法，所用接口：Farmwork/GetAgricultureinfo?farm={farm}&type={type}
    public void GetAgricultureinfo(int type) {
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context
                .MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client_GetAgricultureinfo = application.getClient();

        Request request_GetAgricultureinfo = new Request.Builder().header("Token", token).url(url
                + "Farmwork/GetAgricultureinfo?farm=" + application.getFarm().getFarm_ID() +
                "&type=" + type).build();
        Log.i("TAG", "GetAgricultureinfo");

        try {
            Response response_GetAgricultureinfo = client_GetAgricultureinfo.newCall
                    (request_GetAgricultureinfo).execute();
            Log.i("TAG", response_GetAgricultureinfo.toString());
            String str = response_GetAgricultureinfo.body().string();
            Log.i("TAG", str);
            JSONObject jsonObject = new JSONObject(str);
            boolean status = jsonObject.getBoolean("status");
            if (status) {
                JSONArray data = jsonObject.getJSONArray("data");
                ArrayList<RSInfo> rsInfos = new ArrayList<>();
                Log.i("TAG", String.valueOf(data.length()));
                for (int i = 0; i < data.length(); i++) {
                    JSONObject rsi = data.getJSONObject(i);
                    int id = rsi.getInt("id");
                    Log.i("TAG", "id:" + id);
                    int farm = rsi.getInt("farm");
                    int field = rsi.getInt("field");
                    int rsi_type = rsi.getInt("type");
                    String date = rsi.getString("date");
                    int grade = rsi.getInt("grade");
                    double value = rsi.getDouble("value");
                    Log.i("TAG", "value:" + value);

                    RSInfo rsInfo = new RSInfo(id, farm, field, rsi_type, date, grade, value);
                    rsInfos.add(rsInfo);
                    Log.i("TAG", rsInfo.toString());
                }
                Log.i("TAG", "ended");
                //                    application.addRsInfosmation(rsInfos);
                rsInformation.addAll(rsInfos);
                Log.i("TAG", "handler");
            }
            response_GetAgricultureinfo.close();
            //                myhandler.obtainMessage(MSE_DOWNLOAD_ALL_SUCCESS).sendToTarget();
        } catch (IOException e) {
            Log.i("TAG", "IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.i("TAG", "JSONException");
            e.printStackTrace();
        }
    }

    //获取地块的实况信息，GET方法，所用接口：Farmwork/GetFieldLive?field={field}
    public void GetFieldLive(int fieldid) {
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context
                .MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client_GetFieldLive = application.getClient();
        Request request_GetFieldLive = new Request.Builder().header("Token", token).url(url +
                "Farmwork/GetFieldLive?field=" + fieldid).build();
        try {
            Response response_GetFieldLive = client_GetFieldLive.newCall(request_GetFieldLive)
                    .execute();
            Log.i("TAG", response_GetFieldLive.message());
            if (response_GetFieldLive.isSuccessful()) {
                String s = response_GetFieldLive.body().string();
                Log.i("TAG", s);
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<Field_live> lives = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject fieldlive = data.getJSONObject(i);
                        Log.i("LIVE","fieldlive:"+fieldlive.toString());
                        int id = fieldlive.getInt("id");
                        Log.i("LIVE","id:"+id);
                        int field = fieldlive.getInt("field");
                        Log.i("LIVE","field:"+field);
                        int growth = fieldlive.getInt("growth");
                        Log.i("LIVE","growth:"+growth);
                        int moisture = fieldlive.getInt("moisture");
                        Log.i("LIVE","moisture:"+moisture);
                        int disease = fieldlive.getInt("disease");
                        Log.i("LIVE","disease:"+disease);
                        int pest = fieldlive.getInt("pest");
                        Log.i("LIVE","pest:"+pest);
                        int collector = fieldlive.getInt("collector");
                        Log.i("LIVE","collector:"+collector);
                        String date = fieldlive.getString("collect_date");
                        Log.i("LIVE","date:"+date);
                        String gps = fieldlive.getString("gps");
                        Log.i("LIVE","gps:"+gps);
                        String picture = fieldlive.getString("picture");
                        Log.i("LIVE","picture:"+picture);

                        gps = gps.substring(5, gps.length());
                        gps = gps.replace("(", "");
                        gps = gps.replace(")", "");
                        gps = gps.trim();
                        Log.i("LIVE", "gps:" + gps);
                        String[] strings = gps.split("[\\s]");
                        GeoPoint point=new GeoPoint(Double.valueOf(strings[0]),Double.valueOf(strings[1]));

                        Field_live field_live = new Field_live(id, field, growth, moisture,
                                disease, pest, collector, date, point, picture);
                        lives.add(field_live);
                    }
                    application.addField_lives(lives);
                }
            }
            response_GetFieldLive.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

}
