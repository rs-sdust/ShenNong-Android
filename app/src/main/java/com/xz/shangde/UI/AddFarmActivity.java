package com.xz.shangde.UI;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.shangde.Area;
import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 添加地块类，用于绘制地块
 */

public class AddFarmActivity extends AppCompatActivity {

    private Context context;

    private TextView tv_perimeter;
    private TextView tv_area_1;
    private TextView tv_area_2;
    private TextView tv_altitude;

    private MapView mMapView = null;
    private Polygon polygon;
    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private GeoPoint center_point;
    private Area area=new Area();

    private LinearLayout btn_back_add;
    private Button btn_finish_add;
    private LinearLayout btn_repeat_add;
    private TextView tv_tips_add_farm;

    private ImageView iv_location;

    //状态判断
    private boolean FINISH_STATE = false;

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

    private ArrayList<Field> fields_DB=new ArrayList<>();
    private static final int MSE_DOWNLOAD_FIELDS_SUCCESS=0;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSE_DOWNLOAD_FIELDS_SUCCESS:
                    for (int i = 0; i < fields_DB.size(); i++) {
                        Field field = fields_DB.get(i);
                        Polygon polygon = new Polygon();
                        polygon.setPoints(field.getField_Boundary());
                        mMapView.getOverlayManager().add(polygon);
                    }
                    ArrayList<GeoPoint> points = new ArrayList<>();
                    for (int i = 0; i < fields_DB.size(); i++) {
                        points.addAll(fields_DB.get(i).getField_Boundary());
                    }
                    BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
                    mMapView.zoomToBoundingBox(boundingBox, false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_addfarm);

        initView();

        initDownload();

        initBtnListener();
    }

    public void initView() {
        context = this;

        btn_back_add = findViewById(R.id.btn_back_add);
        btn_finish_add = findViewById(R.id.btn_finish_add);
        btn_repeat_add = findViewById(R.id.btn_repeat_add);
        tv_tips_add_farm = findViewById(R.id.tv_tips_add_farm);
        iv_location = findViewById(R.id.iv_location);
        initLocation();

        tv_perimeter = findViewById(R.id.tv_perimeter);
        tv_area_1 = findViewById(R.id.tv_area_1);
        tv_area_2 = findViewById(R.id.tv_area_2);
        tv_altitude = findViewById(R.id.tv_altitude);

        mMapView = (MapView) findViewById(R.id.add_farm_map);

        //设置瓦片的来源
        mMapView.setTileSource(GoogleHybrid);

        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(false);

        //设置地图中心点位置
        IMapController mapController = mMapView.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(39.9035849157, 116.3977047882);
        mapController.setCenter(startPoint);
        mMapView.invalidate();

        //设置地图点击事件
        mMapView.getOverlayManager().add(new Overlay() {
            @Override
            public void draw(Canvas c, MapView osmv, boolean shadow) {

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

                if (!FINISH_STATE) {
                    final IGeoPoint geo = mapView.getProjection().fromPixels((int) e.getX(),
                            (int) e.getY());

                    GeoPoint point = new GeoPoint(geo.getLatitude(), geo.getLongitude());

                    //                geoPoints.add(point);

                    //绘制点，并设置图案和气泡窗（空）
                    Marker marker = new Marker(mMapView);
                    marker.setPosition(point);
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_place_black_24dp));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    //设置marker可拖拽，距离，无弹窗
                    marker.setDraggable(true);
                    marker.setDragOffset(10);
                    marker.setInfoWindow(null);

                    markers.add(marker);
                    mMapView.getOverlays().add(marker);

                    mMapView.invalidate();
                    tv_tips_add_farm.setText("边界点是可以微调的哦，长按后移动试试");
                    //                Polygon polygon = new Polygon();    //see note below
                    //                polygon.setFillColor(Color.argb(75, 255,0,0));
                    //                polygon.setPoints(geoPoints);
                    //                mMapView.getOverlays().add(polygon);
                }
                return true;
            }
        });
    }

    public void initDownload(){
        new Thread(){
            @Override
            public void run() {
                DownloadFields();
            }
        }.start();
    }

    public void initBtnListener() {

        btn_back_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_finish_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FINISH_STATE) {
                    if (markers.size() == 0) {
                        Toast.makeText(getApplicationContext(), "绘制完成后才可以点击哦", Toast
                                .LENGTH_SHORT).show();
                    } else {
                        FINISH_STATE = true;
                        geoPoints.clear();
                        for (int i = 0; i < markers.size(); i++) {
                            GeoPoint point = markers.get(i).getPosition();
                            geoPoints.add(point);
                        }

                        polygon = new Polygon();
                        polygon.setFillColor(Color.argb(75, 81, 173, 88));
                        polygon.setStrokeWidth(3);
                        polygon.setStrokeColor(Color.argb(255, 255, 255, 255));
                        geoPoints.add(geoPoints.get(0));    //forces the loop to close
                        polygon.setPoints(geoPoints);
                        mMapView.getOverlayManager().add(polygon);
                        mMapView.invalidate();

                        //area面积，distance周长，center_point中心点
                        area.area_m2 = ComputeArea(geoPoints);
                        area.compute();
                        double distance = 0;
                        for (int i = 0; i < geoPoints.size() - 1; i++) {
                            GeoPoint point_this = geoPoints.get(i);
                            GeoPoint point_next = geoPoints.get(i + 1);
                            distance += Distance(point_this.getLatitude(), point_this.getLongitude()
                                    , point_next.getLatitude(), point_next.getLongitude());
                        }
                        center_point = GetCenterPointFromListOfCoordinates(geoPoints);

                        tv_perimeter.setText(String.valueOf((int) (distance*1000)) + "米");
                        tv_area_1.setText(String.valueOf((int) area.area_m2 + "平方米"));
                        tv_area_2.setText(String.valueOf("约" + (int) (area.area_mu) + "亩"));
                        tv_altitude.setText("未知");

                        btn_finish_add.setText("完成");
                    }
                } else {
                    Intent intent=new Intent(getApplicationContext(),PrintPropertyActivity.class);
                    intent.putExtra("FromActivity","AddFarmActivity");
                    intent.putExtra("geoPoints",geoPoints);
                    intent.putExtra("area",area);
                    intent.putExtra("center_point_latitude",center_point.getLatitude());
                    intent.putExtra("center_point_longitude",center_point.getLongitude());
                    startActivity(intent);

                    finish();
                }
            }
        });


        btn_repeat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.getOverlays().remove(polygon);
                for (int i = 0; i < markers.size(); i++) {
                    mMapView.getOverlays().remove(markers.get(i));
                }
                mMapView.invalidate();

                FINISH_STATE = false;
                markers.clear();

                tv_tips_add_farm.setText("请一次绘制一个地块，不要着急哦");
            }
        });
    }

    public void initLocation() {

        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否开启权限！
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                        .PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission
                        .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "权限不够", Toast.LENGTH_LONG).show();
                    return;
                }

                //获取一个地址管理者，获取的方法比较特殊，不是直接new出来的
                LocationManager locationManager = (LocationManager) context.getSystemService(context
                        .LOCATION_SERVICE);

                //使用GPS获取上一次的地址，这样获取到的信息需要多次，才能够显示出来，所以后面有动态的判断
                Location location = locationManager.getLastKnownLocation(locationManager
                        .GPS_PROVIDER);

                if (location!=null){
                    IMapController mapController = mMapView.getController();
                    mapController.setZoom(15);
                    GeoPoint centerPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                    mapController.setCenter(centerPoint);
                }

                //判断是否用户打开了GPS开关，这个和获取权限没关系
                GPSisopen(locationManager);

                //获取时时更新，第一个是Provider,第二个参数是更新时间1000ms，第三个参数是更新半径，第四个是监听器
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 8, new
                        LocationListener() {
                            @Override
            /*当地理位置发生改变的时候调用*/
                            public void onLocationChanged(Location location) {
//                                IMapController mapController = mMapView.getController();
//                                mapController.setZoom(12);
//                                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                                mapController.setCenter(startPoint);
//                                mMapView.invalidate();
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

    /**
     * 注意，该方法使用需要各点按顺时针或逆时针方向排序
     * 该方法计算面积有一定误差，初步测试在百分之一左右
     */
    public double ComputeArea(List<GeoPoint> points) {

        ArrayList<Double> Point_longitude = new ArrayList<>();
        ArrayList<Double> Point_latitude = new ArrayList<>();
        //在这里减一的目的是将最后一个点，即重复的那个点删掉，否则面积算出来为0
        for (int i = 0; i < points.size() - 1; i++) {
            Point_latitude.add(points.get(i).getLatitude());
            Point_longitude.add(points.get(i).getLongitude());
        }
        String MapUnits = "DEGREES";
        int Count = Point_longitude.size();
        if (Count > 2) {
            double mtotalArea = 0;


            if (MapUnits == "DEGREES")//经纬度坐标下的球面多边形
            {
                double LowX = 0.0;
                double LowY = 0.0;
                double MiddleX = 0.0;
                double MiddleY = 0.0;
                double HighX = 0.0;
                double HighY = 0.0;

                double AM = 0.0;
                double BM = 0.0;
                double CM = 0.0;

                double AL = 0.0;
                double BL = 0.0;
                double CL = 0.0;

                double AH = 0.0;
                double BH = 0.0;
                double CH = 0.0;

                double CoefficientL = 0.0;
                double CoefficientH = 0.0;

                double ALtangent = 0.0;
                double BLtangent = 0.0;
                double CLtangent = 0.0;

                double AHtangent = 0.0;
                double BHtangent = 0.0;
                double CHtangent = 0.0;

                double ANormalLine = 0.0;
                double BNormalLine = 0.0;
                double CNormalLine = 0.0;

                double OrientationValue = 0.0;

                double AngleCos = 0.0;

                double Sum1 = 0.0;
                double Sum2 = 0.0;
                double Count2 = 0;
                double Count1 = 0;


                double Sum = 0.0;
                double Radius = 6378000;

                for (int i = 0; i < Count; i++) {
                    if (i == 0) {
                        LowX = (double) Point_longitude.get(Count - 1) * Math.PI / 180;
                        LowY = (double) Point_latitude.get(Count - 1) * Math.PI / 180;
                        MiddleX = (double) Point_longitude.get(0) * Math.PI / 180;
                        MiddleY = (double) Point_latitude.get(0) * Math.PI / 180;
                        HighX = (double) Point_longitude.get(1) * Math.PI / 180;
                        HighY = (double) Point_latitude.get(1) * Math.PI / 180;
                    } else if (i == Count - 1) {
                        LowX = (double) Point_longitude.get(Count - 2) * Math.PI / 180;
                        LowY = (double) Point_latitude.get(Count - 2) * Math.PI / 180;
                        MiddleX = (double) Point_longitude.get(Count - 1) * Math.PI / 180;
                        MiddleY = (double) Point_latitude.get(Count - 1) * Math.PI / 180;
                        HighX = (double) Point_longitude.get(0) * Math.PI / 180;
                        HighY = (double) Point_latitude.get(0) * Math.PI / 180;
                    } else {
                        LowX = (double) Point_longitude.get(i - 1) * Math.PI / 180;
                        LowY = (double) Point_latitude.get(i - 1) * Math.PI / 180;
                        MiddleX = (double) Point_longitude.get(i) * Math.PI / 180;
                        MiddleY = (double) Point_latitude.get(i) * Math.PI / 180;
                        HighX = (double) Point_longitude.get(i + 1) * Math.PI / 180;
                        HighY = (double) Point_latitude.get(i + 1) * Math.PI / 180;
                    }

                    AM = Math.cos(MiddleY) * Math.cos(MiddleX);
                    BM = Math.cos(MiddleY) * Math.sin(MiddleX);
                    CM = Math.sin(MiddleY);
                    AL = Math.cos(LowY) * Math.cos(LowX);
                    BL = Math.cos(LowY) * Math.sin(LowX);
                    CL = Math.sin(LowY);
                    AH = Math.cos(HighY) * Math.cos(HighX);
                    BH = Math.cos(HighY) * Math.sin(HighX);
                    CH = Math.sin(HighY);


                    CoefficientL = (AM * AM + BM * BM + CM * CM) / (AM * AL + BM * BL + CM * CL);
                    CoefficientH = (AM * AM + BM * BM + CM * CM) / (AM * AH + BM * BH + CM * CH);

                    ALtangent = CoefficientL * AL - AM;
                    BLtangent = CoefficientL * BL - BM;
                    CLtangent = CoefficientL * CL - CM;
                    AHtangent = CoefficientH * AH - AM;
                    BHtangent = CoefficientH * BH - BM;
                    CHtangent = CoefficientH * CH - CM;


                    AngleCos = (AHtangent * ALtangent + BHtangent * BLtangent + CHtangent *
                            CLtangent) / (Math.sqrt(AHtangent * AHtangent + BHtangent * BHtangent
                            + CHtangent * CHtangent) * Math.sqrt(ALtangent * ALtangent +
                            BLtangent * BLtangent + CLtangent * CLtangent));

                    AngleCos = Math.acos(AngleCos);

                    ANormalLine = BHtangent * CLtangent - CHtangent * BLtangent;
                    BNormalLine = 0 - (AHtangent * CLtangent - CHtangent * ALtangent);
                    CNormalLine = AHtangent * BLtangent - BHtangent * ALtangent;

                    if (AM != 0)
                        OrientationValue = ANormalLine / AM;
                    else if (BM != 0)
                        OrientationValue = BNormalLine / BM;
                    else
                        OrientationValue = CNormalLine / CM;

                    if (OrientationValue > 0) {
                        Sum1 += AngleCos;
                        Count1++;

                    } else {
                        Sum2 += AngleCos;
                        Count2++;
                        //Sum +=2*Math.PI-AngleCos;
                    }

                }

                if (Sum1 > Sum2) {
                    Sum = Sum1 + (2 * Math.PI * Count2 - Sum2);
                } else {
                    Sum = (2 * Math.PI * Count1 - Sum1) + Sum2;
                }

                //平方米
                mtotalArea = (Sum - (Count - 2) * Math.PI) * Radius * Radius;
            } else { //非经纬度坐标下的平面多边形

                int i, j;
                //double j;
                double p1x, p1y;
                double p2x, p2y;
                for (i = Count - 1, j = 0; j < Count; i = j, j++) {

                    p1x = (double) Point_longitude.get(i);
                    p1y = (double) Point_latitude.get(i);

                    p2x = (double) Point_longitude.get(j);
                    p2y = (double) Point_latitude.get(j);

                    mtotalArea += p1x * p2y - p2x * p1y;
                }
                mtotalArea /= 2.0;
            }
            return mtotalArea;
        }
        return 0;
    }

    //计算地块中心点
    public GeoPoint GetCenterPointFromListOfCoordinates(List<GeoPoint> geoCoordinateList) {
        //以下为简化方法（400km以内）
        int total = geoCoordinateList.size();
        double lat = 0, lon = 0;
        for (GeoPoint g : geoCoordinateList) {
            lat += g.getLatitude() * Math.PI / 180;
            lon += g.getLongitude() * Math.PI / 180;
        }
        lat /= total;
        lon /= total;
        return new GeoPoint(lat * 180 / Math.PI, lon * 180 / Math.PI);
    }

    /**
     * 以下方法用以计算两个点之间的长度
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double Distance(double lat1, double lon1, double lat2, double lon2) {
        //用haversine公式计算球面两点间的距离。
        //经纬度转换成弧度
        lat1 = ConvertDegreesToRadians(lat1);
        lon1 = ConvertDegreesToRadians(lon1);
        lat2 = ConvertDegreesToRadians(lat2);
        lon2 = ConvertDegreesToRadians(lon2);

        //差值
        double vLon = Math.abs(lon1 - lon2);
        double vLat = Math.abs(lat1 - lat2);

        //h is the great circle distance in radians, great circle就是一个球体上的切面，它的圆心即是球心的一个周长最大的圆。
        double h = HaverSin(vLat) + Math.cos(lat1) * Math.cos(lat2) * HaverSin(vLon);

        double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));

        return distance;
    }

    static double EARTH_RADIUS = 6371.0;//km 地球半径 平均值，千米

    public static double HaverSin(double theta) {
        double v = Math.sin(theta / 2);

        return v * v;
    }

    //将角度单位换算为弧度
    public static double ConvertDegreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    //下载所有地块信息并存入application中，GET方法，所用接口：Fields/GetFields?farmid={farmid}
    public void DownloadFields() {
        ShangdeApplication application = (ShangdeApplication) getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
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
                    fields_DB=fields;
                }
            }
            response_DownloadFields.close();
            mhandler.obtainMessage(MSE_DOWNLOAD_FIELDS_SUCCESS).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences
        // (this));
        mMapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mMapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

}
