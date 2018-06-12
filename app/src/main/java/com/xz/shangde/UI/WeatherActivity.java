package com.xz.shangde.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xz.shangde.FutureWeatherDate;
import com.xz.shangde.R;

import java.util.ArrayList;

/**
 * @author zxz
 * 显示当前天气并显示天气预报
 */

public class WeatherActivity extends AppCompatActivity {

    Toolbar tb_activity_weather;
    TextView tv_temperature_now;
    TextView tv_weather_now;
    TextView tv_wind_now;

    //折线图
    RelativeLayout rl_chart_weather;

    ArrayList<FutureWeatherDate> futureWeatherDates;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();
        initData();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        ChartView chartView = new ChartView(this, futureWeatherDates, w_screen);
        rl_chart_weather.addView(chartView);

//        DrawStableView();
    }

    public void initView() {
        tb_activity_weather = findViewById(R.id.tb_activity_weather);

        rl_chart_weather = findViewById(R.id.rl_chart_weather);

        setSupportActionBar(tb_activity_weather);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_activity_weather.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_temperature_now=findViewById(R.id.tv_temperature_now);
        tv_weather_now=findViewById(R.id.tv_weather_now);
        tv_wind_now=findViewById(R.id.tv_wind_now);
    }

    public void initData() {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        futureWeatherDates = (ArrayList<FutureWeatherDate>) b.getSerializable("data");
        String temperature=intent.getStringExtra("temperature");
        String weather=intent.getStringExtra("weather");
        String wind=intent.getStringExtra("wind");

        tv_temperature_now.setText(temperature);
        tv_weather_now.setText(weather);
        tv_wind_now.setText(wind);
    }

}
