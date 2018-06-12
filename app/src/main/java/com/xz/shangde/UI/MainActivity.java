package com.xz.shangde.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xz.shangde.CropType;
import com.xz.shangde.Farm;
import com.xz.shangde.Field_live;
import com.xz.shangde.News;
import com.xz.shangde.R;
import com.xz.shangde.RSInfo;
import com.xz.shangde.RSInfo_Grade;
import com.xz.shangde.ShangdeApplication;
import com.xz.shangde.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 主界面，使用BottomNavigationView + fragment 完成内容切换操作
 * 下载各数据字典以备使用
 */

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;

    private Fragment100 fragment100;
    private Fragment200 fragment200;
    private Fragment300 fragment300;
    private Fragment400 fragment400;

    private final int MSG_SUCCESS=0;
    private final int MSG_FAIL=1;
    private final int MSE_AGREEED=2;
    private final int MSE_REJECTED=3;
    private final int MSE_OTHER=4;
    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_SUCCESS:
                    break;
                case MSG_FAIL:
                    break;
                case MSE_AGREEED:
//                    //申请权限
//                    RequestPower();
//                    //初始化底部导航栏
//                    initBottomNavigation();
//                    fragment100 = new Fragment100();
//                    fragment200=new Fragment200();
//                    fragment300=new Fragment300();
//                    fragment400=new Fragment400();
//                    //设置起始的fragment为Fragment100对象
//                    fm = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                    fragmentTransaction.add(R.id.container, fragment100, "fragment100");
//                    fragmentTransaction.commit();
//                    download();
                    break;
                case MSE_REJECTED:
                    //已经被拒绝了
                    break;
                case MSE_OTHER:
                    Intent intent=new Intent(getApplicationContext(),WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences=getSharedPreferences("User",Context.MODE_PRIVATE);
        int role=sharedPreferences.getInt("Role",0);
        if (role==1){
            TaskStatus();
        }

        String  sdCardDir = Environment.getExternalStorageDirectory()+ "/Shangde/Pictures/";
        File dirFile  = new File(sdCardDir);  //目录转化成文件夹
        if (!dirFile .exists()) {              //如果不存在，那就建立这个文件夹
            dirFile .mkdirs();
        }                          //文件夹有啦，就可以保存图片啦

        //申请权限
        RequestPower();
        //初始化底部导航栏
        initBottomNavigation();

        fragment100 = new Fragment100();
        fragment200=new Fragment200();
        fragment300=new Fragment300();
        fragment400=new Fragment400();

        //设置起始的fragment为Fragment100对象
        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment100, "fragment100");
        fragmentTransaction.commit();

        download();
    }

    //权限获取方法,一次性获取多个权限
    private void RequestPower() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> mPermissionList = new ArrayList<>();

        //判断哪些权限未授予
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager
                    .PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        //判断是否为空
        if (!mPermissionList.isEmpty()) {//请求权限方法
            Toast.makeText(MainActivity.this, "为了方便使用，请全部授予权限", Toast.LENGTH_LONG).show();
            String[] permission = mPermissionList.toArray(new String[mPermissionList.size()]);
            //将List转为数组
            ActivityCompat.requestPermissions(this, permission, 1);
        }
    }

    //初始化底部导航栏
    public void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        //取消底部导航栏中元素大小不一的情况（>3个items会出现）
        disableShiftMode(bottomNavigationView);
        //设置点击事件监听
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_farm:
                        FragmentTransaction ft1 = fm.beginTransaction();
                        if (fragment100.isAdded()){
                            hideAllFragment();
                            ft1.show(fragment100);
                            Log.i("testFragment","fragment100 show()");
                        }
                        else {
                            hideAllFragment();
                            ft1.add(R.id.container,fragment100,"fragment100");
                            Log.i("testFragment","fragment100 add()");
                        }
                        ft1.commit();
                        return true;
                    case R.id.navigation_farmthing:
                        FragmentTransaction ft2 = fm.beginTransaction();
                        if (fragment200.isAdded()){
                            hideAllFragment();
                            ft2.show(fragment200);
                            Log.i("testFragment","fragment200 show()");
                        }
                        else {
                            hideAllFragment();
                            ft2.add(R.id.container,fragment200,"fragment200");
                            Log.i("testFragment","fragment200 add()");
                        }
                        ft2.commit();
                        return true;
                    case R.id.navigation_find:
                        FragmentTransaction ft3 = fm.beginTransaction();
                        if (fragment300.isAdded()){
                            hideAllFragment();
                            ft3.show(fragment300);
                            Log.i("testFragment","fragment300 show()");
                        }
                        else {
                            hideAllFragment();
                            ft3.add(R.id.container,fragment300,"fragment300");
                            Log.i("testFragment","fragment300 add()");
                        }
                        ft3.commit();
                        return true;
                    case R.id.navigation_my:
                        FragmentTransaction ft4 = fm.beginTransaction();
                        if (fragment400.isAdded()){
                            hideAllFragment();
                            ft4.show(fragment400);
                            Log.i("testFragment","fragment400 show()");
                        }
                        else {
                            hideAllFragment();
                            ft4.add(R.id.container,fragment400,"fragment400");
                            Log.i("testFragment","fragment400 add()");
                        }
                        ft4.commit();
                        return true;
                }
                return false;
            }
        });
    }

    public void hideAllFragment(){
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        if (fragment100.isAdded()){
            fragmentTransaction.hide(fragment100);
        }
        if (fragment200.isAdded()){
            fragmentTransaction.hide(fragment200);
        }
        if (fragment300.isAdded()){
            fragmentTransaction.hide(fragment300);
        }
        if (fragment400.isAdded()){
            fragmentTransaction.hide(fragment400);
        }
        fragmentTransaction.commit();
    }

    @SuppressLint("RestrictedApi")
    //当底部元素数量多于三个时，会出现效果，去除此效果
    public void disableShiftMode(BottomNavigationView view) {
        //获取子View BottomNavigationMenuView的对象
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            //设置私有成员变量mShiftingMode可以修改
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //去除shift效果
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "没有mShiftingMode这个成员变量", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "无法修改mShiftingMode的值", e);
        }
    }

    /**
     * 下载作物类型列表，GET方法，所用接口：Dic/GetCrops   ------done
     * 下载病害类型列表，GET方法，所用接口：Dic/GetDiseaseType    -----done
     * 下载虫害类型列表，GET方法，所用接口：Dic/GetPestType    -----done
     * 下载资讯类型列表，GET方法，所用接口：Dic/GetNewsType    -----done
     * 获取产品等级列表，GET方法，所用接口：Dic/GetRsiGrade    -----done
     */
    public void download(){

        new Thread(){
            @Override
            public void run() {
                ShangdeApplication application= (ShangdeApplication) getApplication();
                String url=application.getURL();
                SharedPreferences sp=getSharedPreferences("User", Context.MODE_PRIVATE);
                String token=sp.getString("Token","");

                int FarmID=sp.getInt("FarmID",-1);
                String FarmName=sp.getString("FarmName","");
                int ProvinceIndex=sp.getInt("ProvinceIndex",0);
                int CityIndex=sp.getInt("CityIndex",0);
                int CountyIndex=sp.getInt("CountyIndex",0);
                String Province=sp.getString("Province","");
                String City=sp.getString("City","");
                String County=sp.getString("County","");

                Farm farm_sp=new Farm(FarmID,FarmName,Province,City,County,ProvinceIndex,CityIndex,CountyIndex);
                application.setFarm(farm_sp);

                int User_ID=sp.getInt("User_ID",-1);
                String User_Name=sp.getString("User_Name","");
                String PhoneNumber=sp.getString("PhoneNumber","");
                String Password=sp.getString("Password","");
                int Role=sp.getInt("Role",0);
                int Farm=sp.getInt("Farm",0);
                String Icon=sp.getString("Icon","");

                User user_sp=new User(User_ID,User_Name,PhoneNumber,Password,Role,Farm,Icon);
                application.setUser(user_sp);

                OkHttpClient client=application.getClient();
                Request request_getCrop=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Dic/GetCrops")
                        .build();
                try {
                    Response response_getCrop=client.newCall(request_getCrop).execute();
                    if ((response_getCrop.isSuccessful())){
                        String s=response_getCrop.body().string();
                        Log.i("TAG",s);
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONArray data=jsonObject.getJSONArray("data");
                            LinkedHashMap<Integer,String> crop_type=new LinkedHashMap<>();
                            for (int i=0;i<data.length();i++){
                                JSONObject croptype=data.getJSONObject(i);
                                int id=croptype.getInt("id");
                                String type=croptype.getString("crop_type");

                                crop_type.put(id,type);
                            }
                            //添加未种植字段
                            crop_type.put(-1,"未种植");
                            application.setCroptype_map(crop_type);
                        }
                    }
                    else {
                        Log.i("TAG","crop:"+response_getCrop.message());
                    }
                    response_getCrop.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("TAG","IOException");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("TAG","JSONException");
                }



                //下载病害类型列表，GET方法，所用接口：Dic/GetDiseaseType
                Request request_GetDiseaseType=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Dic/GetDiseaseType")
                        .build();
                Log.i("TAG","before");
                try {
                    Response response_GetDiseaseType=client.newCall(request_GetDiseaseType)
                                .execute();
                    Log.i("TAG",response_GetDiseaseType.toString());
                    if (response_GetDiseaseType.isSuccessful()){
                        String s=response_GetDiseaseType.body().string();
                        Log.i("TAG",s);
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONArray data=jsonObject.getJSONArray("data");
                            LinkedHashMap<Integer,String> disease_map=new LinkedHashMap<>();
                            for (int i=0;i<data.length();i++){
                                JSONObject object=data.getJSONObject(i);
                                int id=object.getInt("id");
                                String type=object.getString("disease_type");
                                disease_map.put(id,type);
                            }
                            application.setDisease_map(disease_map);
                        }
                    }
                    response_GetDiseaseType.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //下载虫害类型列表，GET方法，所用接口：Dic/GetPestType
                Request request_GetPestType=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Dic/GetPestType")
                        .build();
                try {
                    Response response_GetPestType=client.newCall(request_GetPestType)
                            .execute();
                    Log.i("TAG",response_GetPestType.toString());
                    if (response_GetPestType.isSuccessful()){
                        String s=response_GetPestType.body().string();
                        Log.i("TAG",s);
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONArray data=jsonObject.getJSONArray("data");
                            LinkedHashMap<Integer,String> pest_map=new LinkedHashMap<>();
                            for (int i=0;i<data.length();i++){
                                JSONObject object=data.getJSONObject(i);
                                int id=object.getInt("id");
                                String type=object.getString("pest_type");
                                pest_map.put(id,type);
                            }
                            application.setPest_map(pest_map);
                        }
                    }
                    response_GetPestType.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //下载资讯类型列表，GET方法，所用接口：Dic/GetNewsType
                Request request_GetNewsType=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Dic/GetNewsType")
                        .build();
                try {
                    Response response_GetNewsType=client.newCall(request_GetNewsType)
                            .execute();
                    Log.i("TAG",response_GetNewsType.toString());
                    if (response_GetNewsType.isSuccessful()){
                        String s=response_GetNewsType.body().string();
                        Log.i("TAG",s);
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONArray data=jsonObject.getJSONArray("data");
                            LinkedHashMap<Integer,String> news_map=new LinkedHashMap<>();
                            for (int i=0;i<data.length();i++){
                                JSONObject object=data.getJSONObject(i);
                                int id=object.getInt("id");
                                String type=object.getString("news_type");
                                news_map.put(id,type);
                            }
                            application.setNews_map(news_map);
                        }
                    }
                    response_GetNewsType.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //获取产品等级列表，GET方法，所用接口：Dic/GetRsiGrade
                Request request_GetRsiGrade=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Dic/GetRsiGrade")
                        .build();
                try {
                    Response response_GetRsiGrade=client.newCall(request_GetRsiGrade)
                            .execute();
                    Log.i("TAG",response_GetRsiGrade.toString());
                    if (response_GetRsiGrade.isSuccessful()){
                        String s=response_GetRsiGrade.body().string();
                        Log.i("TAG",s);
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONArray data=jsonObject.getJSONArray("data");
                            ArrayList<RSInfo_Grade> grades=new ArrayList<>();
                            for (int i=0;i<data.length();i++){
                                JSONObject object=data.getJSONObject(i);
                                int id=object.getInt("id");
                                int grade=object.getInt("grade");
                                int rsi_type=object.getInt("rsi_type");
                                String name=object.getString("name");

                                RSInfo_Grade rsInfo_grade=new RSInfo_Grade(id,grade,rsi_type,name);
                                grades.add(rsInfo_grade);
                            }
                            application.setRsInfo_grades(grades);
                        }
                    }
                    response_GetRsiGrade.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                //获取物候类型列表,GET方法，所用接口：Dic/GetPhenType
//                Request request_GetPhenType=new Request.Builder()
//                        .header("Token",token)
//                        .url(url+"Dic/GetPhenType")
//                        .build();
//                try {
//                    Response response_GetPhenType=client.newCall(request_GetPhenType).execute();
//                    if (response_GetPhenType.isSuccessful()){
//                        String s=response_GetPhenType.body().string();
//                        Log.i("TAG",s);
//                        JSONObject jsonObject=new JSONObject(s);
//                        boolean status=jsonObject.getBoolean("status");
//                        if (status){
//                            JSONArray data=jsonObject.getJSONArray("data");
//                            LinkedHashMap<Integer,String> map_PhenType=new LinkedHashMap<>();
//                            for (int i=0;i<data.length();i++){
//                                JSONObject object=data.getJSONObject(i);
//                                int id=object.getInt("id");
//                                String phen_type=object.getString("phen_type");
//                                map_PhenType.put(id,phen_type);
//                            }
//                            application.setPhenType_map(map_PhenType);
//                        }
//                    }
//                    response_GetPhenType.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


            }
        }.start();

    }

    //查看请求是否通过，GET方法，所用接口：Task/GetUserTask?creator={creator}
    public void TaskStatus(){
        new Thread(){
            @Override
            public void run() {
                ShangdeApplication application= (ShangdeApplication) getApplication();
                String url=application.getURL();
                SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
                int creator=sp.getInt("User_ID",0);
                String token=sp.getString("Token","");

                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .header("Token",token)
                        .url(url+"GetUserTask?creator="+creator)
                        .build();
                try {
                    Response response=okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()){
                        String s=response.body().string();
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONObject data=jsonObject.getJSONObject("data");
                            int state=data.getInt("state");
                            int agree=data.getInt("agree");
                            if ( (state==1) && (agree==1) ){
                                //处理并同意
                                mHandler.obtainMessage(MSE_AGREEED).sendToTarget();
                            }
                            else if ( (state==1) && (agree==0) ){
                                mHandler.obtainMessage(MSE_REJECTED).sendToTarget();
                            }
                            else {
                                mHandler.obtainMessage(MSE_OTHER).sendToTarget();
                            }
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
