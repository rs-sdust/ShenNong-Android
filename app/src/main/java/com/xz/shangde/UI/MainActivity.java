package com.xz.shangde.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.xz.shangde.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxq on 2018/4/25.
 */

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        RequestPower();
        //初始化底部导航栏
        initBottomNavigation();

        //设置起始的fragment为Fragment100对象
        fm=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        Fragment100 fragment100=new Fragment100();
        fragmentTransaction.add(R.id.container,fragment100,null);
        fragmentTransaction.commit();
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
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        //判断是否为空
        if (!mPermissionList.isEmpty()) {//请求权限方法
            Toast.makeText(MainActivity.this, "为了方便使用，请全部授予权限", Toast.LENGTH_LONG).show();
            String[] permission = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
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
                        Toast.makeText(MainActivity.this, "navigation_farm was clicked", Toast
                                .LENGTH_LONG).show();
                        FragmentTransaction ft1=fm.beginTransaction();
                        Fragment100 fragment1=new Fragment100();
                        ft1.replace(R.id.container,fragment1,null);
                        ft1.commit();
                        return true;
                    case R.id.navigation_farmthing:
                        Toast.makeText(MainActivity.this, "navigation_farmthing was clicked", Toast
                                .LENGTH_LONG).show();
                        FragmentTransaction ft2=fm.beginTransaction();
                        Fragment200 fragment2=new Fragment200();
                        ft2.replace(R.id.container,fragment2,null);
                        ft2.commit();
                        return true;
                    case R.id.navigation_find:
                        Toast.makeText(MainActivity.this, "navigation_find was clicked", Toast
                                .LENGTH_LONG).show();
                        FragmentTransaction ft3=fm.beginTransaction();
                        Fragment300 fragment3=new Fragment300();
                        ft3.replace(R.id.container,fragment3,null);
                        ft3.commit();
                        return true;
                    case R.id.navigation_my:
                        Toast.makeText(MainActivity.this, "navigation_my was clicked", Toast
                                .LENGTH_LONG).show();
                        FragmentTransaction ft4=fm.beginTransaction();
                        Fragment400 fragment4=new Fragment400();
                        ft4.replace(R.id.container,fragment4,null);
                        ft4.commit();
                        return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    //当底部元素数量多于三个时，会出现效果，去除此效果
    public static void disableShiftMode(BottomNavigationView view) {
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


}
