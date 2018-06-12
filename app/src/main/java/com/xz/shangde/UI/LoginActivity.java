package com.xz.shangde.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xz.shangde.Farm;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;
import com.xz.shangde.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 登陆操作界面，启动界面
 * 短信验证码使用的是MOB公司的SMSSDK服务
 */

public class LoginActivity extends Activity {

    private EditText phoneNumber;
    private EditText identifyCode;
    private Button getIdentifyCode;
    private Button load;

    private ProgressBar progressbar_login;

    private final int MSE_FAILURE = 0;
    private final int MSE_SUCCESS_REGISTER = 1;
    private final int MSE_SUCCESS_LOGIN = 2;
    private Handler mhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressbar_login.setVisibility(View.INVISIBLE);
            switch (msg.what) {
                case MSE_FAILURE:
                    Toast.makeText(getApplicationContext(), "登陆失败，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case MSE_SUCCESS_REGISTER:
                    Intent intent1 = new Intent(getApplicationContext(), EditInfoActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
                case MSE_SUCCESS_LOGIN:
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        boolean Logedin = sp.getBoolean("Logedin", false);
        if (Logedin) {
            // 如果已经登陆，需要做的操作(进入主界面，下载数据)
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            //未登录，进入登陆界面
            setContentView(R.layout.activity_login);
            phoneNumber = findViewById(R.id.et_PhoneNumber);
            identifyCode = findViewById(R.id.et_IdentifyCode);
            getIdentifyCode = findViewById(R.id.btn_getIdentifyCode);
            load = findViewById(R.id.btn_load);
            progressbar_login = findViewById(R.id.progressbar_login);

            SpannableString ss = new SpannableString("请输入手机号");//定义hint的值
            AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);//设置字体大小 true表示单位是sp
            ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            phoneNumber.setHint(new SpannedString(ss));

            SpannableString ss1 = new SpannableString("请输入验证码");//定义hint的值
            AbsoluteSizeSpan ass1 = new AbsoluteSizeSpan(14, true);//设置字体大小 true表示单位是sp
            ss1.setSpan(ass1, 0, ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            identifyCode.setHint(new SpannedString(ss1));
        }

    }

    public void getIdentifyCode(View view) {
        sendCode("86", phoneNumber.getText().toString());
        //修改按钮的样式
        getIdentifyCode.setText("请稍后");
        getIdentifyCode.setBackgroundResource(R.drawable.editview_login_shape);
    }

    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //  处理成功得到验证码的结果
                    Log.i("Identify", "start right");
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else {
                    // 处理错误的结果
                    Log.i("Identify", "start wrong");
                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    public void load(View view) {
        progressbar_login.setVisibility(View.VISIBLE);
//        submitCode("86", phoneNumber.getText().toString(), identifyCode.getText().toString());
        testBackEnd(phoneNumber.getText().toString());
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, final String phone, String code) {

        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //处理验证成功的结果，从网络上下载个人信息，跳转页面
                    Log.i("Identify", "right");

                    new Thread() {
                        @Override
                        public void run() {
                            Log.i("TAG", "thread run");
                            final ShangdeApplication application = (ShangdeApplication)
                                    getApplication();
                            final String url = application.getURL();

                            //判断用户是否存在，GET方法,所用接口：User/IsExsit?mobile={mobile}
                            String s = url + "User/IsExsit?mobile=" + phone;
                            Log.i("TAG", s);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url(s).build();
                            try {
                                Response response = client.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    Headers responseHeaders = response.headers();
                                    String Token = responseHeaders.get("Token");
                                    Log.i("TAG", "Token:" + Token);
                                    String response_data = response.body().string();
                                    JSONObject jsonObject = new JSONObject(response_data);
                                    Log.i("TAG", jsonObject.toString());
                                    String status = jsonObject.getString("status");
                                    Log.i("TAG", "status:" + status);
                                    String msg = jsonObject.getString("msg");
                                    Log.i("TAG", "msg:" + msg);

                                    SharedPreferences sp = getSharedPreferences("User", Context
                                            .MODE_PRIVATE);
                                    SharedPreferences.Editor sp_editor = sp.edit();
                                    sp_editor.putString("Token", Token);

                                    if (Boolean.valueOf(status)) {
                                        //登陆成功，并下载用户数据（填充User类）
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        Log.i("TAG", data.toString());
                                        JSONObject data_user = data.getJSONObject(0);
                                        int id = data_user.getInt("id");
                                        String user_name = data_user.getString("name");
                                        String psw = data_user.getString("psw");
                                        int role = data_user.getInt("role");
                                        int farm = data_user.getInt("farm");
                                        String icon = data_user.getString("icon");


                                        //拉取农场信息并保存，GET方法，所用接口：api/Farm/GetFarmbrief?farmid={farmid}
                                        OkHttpClient client1 = new OkHttpClient();
                                        Request request1 = new Request.Builder().header("Token",
                                                Token).url(url + "Farm/GetFarmbrief?farmid="
                                                + farm).build();
                                        Response response1 = client1.newCall(request1).execute();
                                        if (response1.isSuccessful()) {
                                            JSONObject object_farm = new JSONObject
                                                    (response1.body().string());
                                            Log.i("TAG", object_farm.toString());
                                            boolean status_farm = object_farm.getBoolean("status");
                                            if (status_farm) {
                                                JSONArray data_array_farm = object_farm
                                                        .getJSONArray("data");
                                                JSONObject data_farm = data_array_farm
                                                        .getJSONObject(0);
                                                int farm_id = data_farm.getInt("id");
                                                String farm_name = data_farm.getString("name");
                                                JSONObject object_farm_address = data_farm
                                                        .getJSONObject("address");

                                                String province_farm = object_farm_address
                                                        .getString("province");
                                                String city_farm = object_farm_address.getString
                                                        ("city");
                                                String county_farm = object_farm_address
                                                        .getString("county");
                                                int province_index_farm = object_farm_address
                                                        .getInt("province_index");
                                                int city_index_farm = object_farm_address.getInt
                                                        ("city_index");
                                                int county_index_farm = object_farm_address
                                                        .getInt("county_index");

                                                sp_editor.putString("FarmName", farm_name);
                                                sp_editor.putInt("FarmID", farm_id);
                                                sp_editor.putInt("ProvinceIndex",
                                                        province_index_farm);
                                                sp_editor.putInt("CityIndex", city_index_farm);
                                                sp_editor.putInt("CountyIndex", county_index_farm);
                                                sp_editor.putString("Province", province_farm);
                                                sp_editor.putString("City", city_farm);
                                                sp_editor.putString("County", county_farm);

                                                Farm farm1 = new Farm(farm_id, farm_name,
                                                        province_farm, city_farm, county_farm,
                                                        province_index_farm, city_index_farm,
                                                        county_index_farm);
                                                application.setFarm(farm1);
                                            } else {
                                                mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                                            }
                                        } else {
                                            Log.i("TAG", "response1: failed");
                                        }
                                        //保存用户信息
                                        sp_editor.putBoolean("Logedin", true);

                                        sp_editor.putInt("User_ID", id);
                                        sp_editor.putString("User_Name", user_name);
                                        sp_editor.putString("PhoneNumber", phone);
                                        sp_editor.putString("Password", psw);
                                        sp_editor.putInt("Role", role);
                                        sp_editor.putInt("Farm", farm);
                                        sp_editor.putString("Icon", icon);

                                        sp_editor.commit();

                                        User user = new User(user_name, phone, psw, role, farm,
                                                icon);
                                        application.setUser(user);

                                        mhandle.obtainMessage(MSE_SUCCESS_LOGIN).sendToTarget();

                                    } else {
                                        //status为false说明登陆不成功，有可能是要注册，或手机号码有误
                                        if (msg.equals("用户不存在，请编辑用户资料!")) {
                                            //用户不存在，注册操作
                                            sp_editor.putString("PhoneNumber", phone);
                                            sp_editor.commit();
                                            mhandle.obtainMessage(MSE_SUCCESS_REGISTER)
                                                    .sendToTarget();
                                        } else {
                                            mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                                        }
                                    }
                                } else {
                                    Log.i("TAG", "response: failed");
                                    mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                                }
                            } catch (IOException e) {
                                mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                                Log.i("TAG", "request failed");
                                e.printStackTrace();
                            } catch (JSONException e) {
                                mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                                Log.i("TAG", "JSON pharse wrong");
                            }
                        }
                    }.run();
                } else {
                    mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    public void testBackEnd(final String phone) {
        new Thread() {
            @Override
            public void run() {
                Log.i("TAG", "thread run");
                final ShangdeApplication application = (ShangdeApplication)
                        getApplication();
                final String url = application.getURL();

                //判断用户是否存在，GET方法,所用接口：User/IsExist?mobile={mobile}
                String s = url + "User/IsExist?mobile=" + phone;
//                String s="http://192.168.1.116/shennong/api/User/IsExsit?mobile=17136371921";
                Log.i("TAG", s);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().
                        url(s).
                        build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Headers responseHeaders = response.headers();
                        String Token = responseHeaders.get("Token");
                        String response_data = response.body().string();
                        JSONObject jsonObject = new JSONObject(response_data);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        SharedPreferences sp = getSharedPreferences("User", Context
                                .MODE_PRIVATE);
                        SharedPreferences.Editor sp_editor = sp.edit();
                        sp_editor.putString("Token", Token);

                        if (Boolean.valueOf(status)) {
                            //登陆成功，并下载用户数据（填充User类）
                            JSONObject data = jsonObject.getJSONObject("data");
                            Log.i("TAG", data.toString());
                            int id = data.getInt("id");
                            String user_name = data.getString("name");
                            String psw = data.getString("im_pwd");
                            int role = data.getInt("role");
                            int farm = data.getInt("farm");
                            String icon = data.getString("icon");


                            //拉取农场信息并保存，GET方法，所用接口：api/Farm/GetFarm?id={id}
                            OkHttpClient client1 = new OkHttpClient();
                            Request request1 = new Request.Builder().header("Token",
                                    Token).url(url + "Farm/GetFarm?id="
                                    + farm).build();
                            Response response1 = client1.newCall(request1).execute();
                            if (response1.isSuccessful()) {
                                JSONObject object_farm = new JSONObject
                                        (response1.body().string());
                                Log.i("TAG", object_farm.toString());
                                boolean status_farm = object_farm.getBoolean("status");
                                if (status_farm) {
                                    JSONObject data_farm = object_farm.getJSONObject("data");
                                    int farm_id = data_farm.getInt("id");
                                    String farm_name = data_farm.getString("name");

                                    String address=data_farm.getString("address");
                                    address=address.replace("{","");
                                    address=address.replace("}","");
                                    address=address.trim();
                                    String[] address_array=address.split("[\\,]");

                                    String province_farm=address_array[0];
                                    String city_farm=address_array[1];
                                    String county_farm=address_array[2];
                                    int province_index_farm=0;
                                    int city_index_farm=0;
                                    int county_index_farm=0;

                                    sp_editor.putString("FarmName", farm_name);
                                    sp_editor.putInt("FarmID", farm_id);
                                    sp_editor.putInt("ProvinceIndex",
                                            province_index_farm);
                                    sp_editor.putInt("CityIndex", city_index_farm);
                                    sp_editor.putInt("CountyIndex", county_index_farm);
                                    sp_editor.putString("Province", province_farm);
                                    sp_editor.putString("City", city_farm);
                                    sp_editor.putString("County", county_farm);

                                    Farm farm1 = new Farm(farm_id, farm_name,
                                            province_farm, city_farm, county_farm,
                                            province_index_farm, city_index_farm,
                                            county_index_farm);
                                    application.setFarm(farm1);
                                } else {
                                    mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                                }
                            } else {
                                Log.i("TAG", "response1: failed");
                            }
                            //保存用户信息
                            sp_editor.putBoolean("Logedin", true);

                            sp_editor.putInt("User_ID", id);
                            sp_editor.putString("User_Name", user_name);
                            sp_editor.putString("PhoneNumber", phone);
                            sp_editor.putString("Password", psw);
                            sp_editor.putInt("Role", role);
                            sp_editor.putInt("Farm", farm);
                            sp_editor.putString("Icon", icon);

                            sp_editor.commit();

                            User user = new User(user_name, phone, psw, role, farm,
                                    icon);
                            application.setUser(user);

                            mhandle.obtainMessage(MSE_SUCCESS_LOGIN).sendToTarget();

                        } else {
                            //status为false说明登陆不成功，有可能是要注册，或手机号码有误
                            if (msg.equals("用户不存在，请编辑用户资料!")) {
                                //用户不存在，注册操作
                                sp_editor.putString("PhoneNumber", phone);
                                sp_editor.commit();
                                mhandle.obtainMessage(MSE_SUCCESS_REGISTER)
                                        .sendToTarget();
                            } else {
                                mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                            }
                        }
                    } else {
                        Log.i("TAG", "response: failed");
                        mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                    }
                } catch (IOException e) {
                    mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                    Log.i("TAG", "request failed");
                    e.printStackTrace();
                } catch (JSONException e) {
                    mhandle.obtainMessage(MSE_FAILURE).sendToTarget();
                    Log.i("TAG", "JSON pharse wrong");
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }

}
