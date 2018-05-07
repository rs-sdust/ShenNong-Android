package com.xz.shangde.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.xz.shangde.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxq on 2018/4/26.
 */

public class Fragment200 extends Fragment {

    Context context;
    private MapView mapView;
    private ImageView iv_photo_200;
    private ImageView iv_location_200;
    private Spinner spinner_image_layers_200;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_200,container,false);

        context=getContext();

        initView(view);
        mapView=view.findViewById(R.id.map_200);
        mapView.setTileSource(GoogleHybrid);

        //add default zoom buttons, and ability to zoom with 2 fingers (multi-touch)
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);

        return view;
    }

    public void initView(View view){
        spinner_image_layers_200=view.findViewById(R.id.spinner_image_layers_200);
        iv_photo_200=view.findViewById(R.id.iv_photo_200);
        iv_location_200=view.findViewById(R.id.iv_location_200);

        iv_photo_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
                Log.i("Tag"," Activity.DEFAULT_KEYS_DIALER: "+ Activity.DEFAULT_KEYS_DIALER);
            }
        });

        iv_location_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否开启权限！
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "权限不够", Toast.LENGTH_LONG).show();
                    return;
                }

                //获取一个地址管理者，获取的方法比较特殊，不是直接new出来的
                LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

                //使用GPS获取上一次的地址，这样获取到的信息需要多次，才能够显示出来，所以后面有动态的判断
                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                Toast.makeText(context,"Longitude:"+location.getLongitude()+";Latitude"+location.getLatitude(),Toast.LENGTH_SHORT).show();
                //判断是否用户打开了GPS开关，这个和获取权限没关系
                GPSisopen(locationManager);

                //获取时时更新，第一个是Provider,第二个参数是更新时间1000ms，第三个参数是更新半径，第四个是监听器
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 8, new LocationListener() {

                    @Override
            /*当地理位置发生改变的时候调用*/
                    public void onLocationChanged(Location location) {
                        Toast.makeText(context,"Longitude:"+location.getLongitude()+";Latitude"+location.getLatitude(),Toast.LENGTH_SHORT).show();
                        Log.i("Tag",location.toString());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Tag","requestCode: "+requestCode);
        Log.i("Tag","resultCode: "+resultCode);
        Log.i("Tag","Activity.RESULT_OK: "+Activity.RESULT_OK);
        if(resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            Log.i("Tag","fragment onActivityResult used");
            //todo bitmap即为拍得的照片，压缩后进行上传
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
}
