package com.xz.shangde.UI;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.shangde.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxz
 * 欢迎界面，并判断用户是否连接网络
 */

public class WelcomeActivity extends AppCompatActivity {

    private TextView tv_welcome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tv_welcome = findViewById(R.id.tv_welcome);

        RequestPower();

        if (isNetworkConnected(this)){
            
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("网络异常")
                    .setMessage("网络连接异常，请连接网络后重试")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create().show();
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
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
            Toast.makeText(getApplicationContext(), "为了方便使用，请全部授予权限", Toast.LENGTH_LONG).show();
            String[] permission = mPermissionList.toArray(new String[mPermissionList.size()]);
            //将List转为数组
            ActivityCompat.requestPermissions(this, permission, 1);
        }
    }
}
