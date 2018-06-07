package com.xz.shangde.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxq on 2018/6/1.
 */

public class FeedbackActivity extends AppCompatActivity {
    private Toolbar tb_feedback;
    private EditText et_opinion;
    private ImageView iv_take_pictrue;
    private Button btn_feedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
        initImage();
        initButton();
    }

    public void initView(){
        tb_feedback=findViewById(R.id.tb_feedback);
        et_opinion=findViewById(R.id.et_opinion);
        iv_take_pictrue=findViewById(R.id.iv_take_pictrue);
        iv_take_pictrue.setVisibility(View.INVISIBLE);
        btn_feedback=findViewById(R.id.btn_feedback);

        setSupportActionBar(tb_feedback);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_feedback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initImage(){}

    //上传意见，POST方法，所用接口：User/CreateFeedback
    public void initButton(){
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        ShangdeApplication application= (ShangdeApplication) getApplication();
                        String url=application.getURL();
                        SharedPreferences sp=getSharedPreferences("User", Context.MODE_PRIVATE);
                        String token=sp.getString("Token","");

                        OkHttpClient client=application.getClient();
                        FormBody formBody=new FormBody.Builder()
                                .add("suggestion",et_opinion.getText().toString())
                                .add("url","null")
                                .build();
                        Request request=new Request.Builder()
                                .header("Token",token)
                                .url(url+"User/CreateFeedback")
                                .post(formBody)
                                .build();
                        try {
                            Response response=client.newCall(request).execute();
                            Log.i("TAG",response.toString());
                            Log.i("TAG",response.body().string());
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}
