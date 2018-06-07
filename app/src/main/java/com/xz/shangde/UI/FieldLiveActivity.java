package com.xz.shangde.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xz.shangde.Field_live;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yxq on 2018/5/23.
 */

public class FieldLiveActivity extends AppCompatActivity {

    private Toolbar tb_field_live;
    private ImageView iv_fields_live;
    private ImageView iv_fields_live2;
    private ImageView iv_fields_live3;
    private TextView tv_growth_field_live;
    private TextView tv_moisture_field_live;
    private TextView tv_disease_field_live;
    private TextView tv_pest_field_live;
    private TextView tv_collector_field_live;
    private TextView tv_date_field_live;

    private Field_live live;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields_live);

        initView();

        initData();
    }

    public void initView(){
        tb_field_live=findViewById(R.id.tb_field_live);
        setSupportActionBar(tb_field_live);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_field_live.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_fields_live=findViewById(R.id.iv_fields_live);
        iv_fields_live2=findViewById(R.id.iv_fields_live2);
        iv_fields_live3=findViewById(R.id.iv_fields_live3);
        tv_growth_field_live=findViewById(R.id.tv_growth_field_live);
        tv_moisture_field_live=findViewById(R.id.tv_moisture_field_live);
        tv_disease_field_live=findViewById(R.id.tv_disease_field_live);
        tv_pest_field_live=findViewById(R.id.tv_pest_field_live);
        tv_collector_field_live=findViewById(R.id.tv_collector_field_live);
        tv_date_field_live=findViewById(R.id.tv_date_field_live);
    }

    public void initData(){
        ShangdeApplication application= (ShangdeApplication) getApplication();
        String url=application.getURL();

        Intent intent=getIntent();
        live= (Field_live) intent.getSerializableExtra("data");

        String pictures=live.getPictures();
        try {
            JSONArray jsonArray=new JSONArray(pictures);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject picture=jsonArray.getJSONObject(i);
                String path=picture.getString("path");
                Log.i("LIVE",path);
                String p=path.substring(2,path.length());
                Log.i("LIVE",p);
                if (i==0){
                    iv_fields_live.setBackground(null);
                    Glide.with(this)
                            .load(url+p)
                            .into(iv_fields_live);
                }else if (i==1){
                    iv_fields_live2.setBackground(null);
                    Glide.with(this)
                            .load(url+p)
                            .into(iv_fields_live2);
                }else {
                    iv_fields_live3.setBackground(null);
                    Glide.with(this)
                            .load(url+p)
                            .into(iv_fields_live3);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Glide.with(this)
//                .load(live.getPictures())
//                .into(iv_fields_live);
        tv_growth_field_live.setText(String.valueOf("长势情况:"+live.getGrowth()));
        tv_moisture_field_live.setText(String.valueOf("土壤湿度:"+live.getMoisture()));
        tv_disease_field_live.setText(String.valueOf("病害:"+live.getDisease()));
        tv_pest_field_live.setText(String.valueOf("虫害:"+live.getPest()));
        tv_collector_field_live.setText(String.valueOf("采集人:"+live.getCollector_ID()));
//        tv_date_field_live.setText(String.valueOf("拍摄时间:"+live.getCollector_Calendar().toString()));
    }
}
