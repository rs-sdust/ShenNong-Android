package com.xz.shangde.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xz.shangde.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by yxq on 2018/4/26.
 */

public class LoginActivity extends Activity {

    EditText phoneNumber=null;
    EditText identifyCode=null;
    Button getIdentifyCode=null;
    Button load=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp=getSharedPreferences("User", Context.MODE_PRIVATE);
        boolean Logedin=sp.getBoolean("Logedin",false);
        if(Logedin){
            Log.i("starttest","right");
            //            todo 如果已经登陆，需要做的操作(进入主界面，下载数据)
            String UserID=sp.getString("UserID","11111111111");
            Intent intent=new Intent(this,MainActivity.class);
            intent.putExtra("UserID",UserID);
            startActivity(intent);
        }
        else {
            //未登录，进入登陆界面
            setContentView(R.layout.activity_login);
            phoneNumber = findViewById(R.id.et_PhoneNumber);
            identifyCode = findViewById(R.id.et_IdentifyCode);
            getIdentifyCode = findViewById(R.id.btn_getIdentifyCode);
            load = findViewById(R.id.btn_load);
        }

    }

    public void getIdentifyCode(View view){
        sendCode("86",phoneNumber.getText().toString());
        //修改按钮的样式
        getIdentifyCode.setText("请稍后");
        getIdentifyCode.setBackgroundResource(R.drawable.editview_login_shape);
    }

    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else{
                    // TODO 处理错误的结果
                    Log.i("Identify","start wrong");
                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    public void load(View view){
        submitCode("86",phoneNumber.getText().toString(),identifyCode.getText().toString());
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, final String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
                    SharedPreferences.Editor sp_editor=sp.edit();
                    sp_editor.putBoolean("Logedin",true);
                    sp_editor.putString("UserID",phone);
                    sp_editor.commit();

                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("UserID",phone);
                    startActivity(intent);
                    finish();
                    
                    Log.i("Identify","success");
                    Toast.makeText(getApplicationContext(),"验证成功",Toast.LENGTH_LONG).show();
                } else{
                    Log.i("Identify","failed");
                    // TODO 处理错误的结果
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }

}
