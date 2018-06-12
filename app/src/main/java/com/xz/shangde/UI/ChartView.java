package com.xz.shangde.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.xz.shangde.FutureWeatherDate;
import com.xz.shangde.R;

import java.util.ArrayList;

/**
 * @author zxz
 * 绘制天气的折线图，目前支持6天的预报天气
 * 如果想要更多的天的天气，需要重新修改或添加组件完成这一工作
 */

public class ChartView extends View {

    private ArrayList<FutureWeatherDate> futureWeatherDates;
    int[][] temperature;
    private int dateNum=0;

    private int canvas_width;
    private int chart_height;
    private int canvas_height;
    //温度的最值，根据最值算出每K（摄氏度）应该增长的高度
    private int temperature_MIN;
    private int temperature_MAX;
    private double height_each_K;
    //两端的留边,单位px
    private int height_in_side;
    private int height_each_line;
    //每一天的宽度
    private int windth_each_day;
    //要显示的图片的宽高，需要计算得来
    private int picture_height_windth;

    public ChartView(Context context, ArrayList<FutureWeatherDate> futureWeatherDates, int
            canvas_width) {
        super(context);
        this.futureWeatherDates = futureWeatherDates;
        this.canvas_width = canvas_width;
        ComputeParameters();
    }

    public void ComputeParameters() {

        if (futureWeatherDates.size()>6){
            dateNum=6;
        }else {
            dateNum=futureWeatherDates.size();
        }

        canvas_height=(int)(canvas_width);
        chart_height = (int) (canvas_width / 4);
        height_in_side=(int)((canvas_height-chart_height)/2 );
        windth_each_day = (int) (canvas_width / dateNum);
        height_each_line=(int)(height_in_side/6);

        temperature = new int[dateNum][2];
        for (int i = 0; i < temperature.length; i++) {
            temperature[i][0] = Integer.parseInt(futureWeatherDates.get(i).getTemperature_daytime()
                    .split("°")[0].trim());
            temperature[i][1] = Integer.parseInt(futureWeatherDates.get(i).getTemperature_night
                    ().split
                    ("°")[0].trim());
        }

        int temperature_max = temperature[0][0];
        int temperature_min = temperature[0][0];
        for (int i = 0; i < temperature.length; i++) {
            for (int j = 0; j < temperature[i].length; j++) {
                if (temperature_max < temperature[i][j]) {
                    temperature_max = temperature[i][j];
                }
                if (temperature_min > temperature[i][j]) {
                    temperature_min = temperature[i][j];
                }
            }
        }
        temperature_MAX = temperature_max;
        temperature_MIN = temperature_min;
        height_each_K = (chart_height) / (temperature_max - temperature_min);

        //计算要显示的图片的宽高
        if (windth_each_day>(height_each_line*2)){
            picture_height_windth=(height_each_line*2)-10;
        }
        else {
            picture_height_windth=windth_each_day-10;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 绘制上方文字和图片
         */
        Paint paint_text_top=new Paint();
        paint_text_top.setTextSize(40);
        paint_text_top.setColor(Color.WHITE);

        for (int i=0;i<dateNum;i++){
            String text_week=futureWeatherDates.get(i).getWeek();
            String text_date=futureWeatherDates.get(i).getDate();
            String text_weather_daytime=futureWeatherDates.get(i).getDayTime();
            canvas.drawText(text_week, (float) (windth_each_day*(i+0.25)),height_each_line,paint_text_top);
            canvas.drawText(text_date, (float) (windth_each_day*(i+0.25)),height_each_line*2,paint_text_top);
            canvas.drawText(text_weather_daytime, (float) (windth_each_day*(i+0.25)),height_each_line*3,paint_text_top);


            Rect mDestRect = new Rect((int)(windth_each_day*(i+0.2)), (int)( height_each_line*3.5), (int) ((windth_each_day*(i+0.2))+picture_height_windth), (int) (height_each_line*3.5+picture_height_windth));
            if (text_weather_daytime.equals("阴")){
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.yin)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
            else if (text_weather_daytime.equals("晴")){
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.qing)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
            else if (text_weather_daytime.contains("云")){
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.duoyun)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
            else{
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.yu)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
        }


        /**
         * 绘制中间的折线图
         */
        Paint paint_text = new Paint();
        paint_text.setColor(Color.BLACK);
        paint_text.setTextSize(30);

        /**
         * 绘制白天气温的折线
         * paint_daytime为折线的画笔
         * paint_point_daytime为每个点的画笔
         */
        Paint paint_daytime = new Paint();
        paint_daytime.setColor(Color.YELLOW);
        paint_daytime.setAntiAlias(true);
        paint_daytime.setStyle(Paint.Style.STROKE);
        paint_daytime.setStrokeWidth(5);
        paint_daytime.setStrokeJoin(Paint.Join.ROUND);

        Paint paint_point_daytime = new Paint();
        paint_daytime.setStyle(Paint.Style.STROKE);
        paint_point_daytime.setStrokeWidth(10);

        Path path_daytime = new Path();

        //记录第一个点，开始路径
        int point_height = (int) (height_in_side + (temperature_MAX - temperature[0][0]) *
                height_each_K);
        int point_width = (int) (windth_each_day / 2);
        canvas.drawPoint(point_width, point_height, paint_point_daytime);
        canvas.drawText(String.valueOf(temperature[0][0]), point_width-10, point_height - 30,
                paint_text);
        path_daytime.moveTo(point_width, point_height);

        for (int i = 1; i < temperature.length; i++) {
            point_height = (int) (height_in_side + (temperature_MAX - temperature[i][0]) *
                    height_each_K);
            point_width = (int) ((i + 0.5) * windth_each_day);
            canvas.drawPoint(point_width, point_height, paint_point_daytime);
            canvas.drawText(String.valueOf(temperature[i][0]), point_width-10, point_height - 30,
                    paint_text);
            path_daytime.lineTo(point_width, point_height);
        }
        canvas.drawPath(path_daytime, paint_daytime);


        /**
         * 绘制晚上气温的折线
         * paint_night为折线的画笔
         * paint_point_night为每个点的画笔
         */
        Paint paint_night = new Paint();
        paint_night.setColor(Color.BLUE);
        paint_night.setAntiAlias(true);
        paint_night.setStyle(Paint.Style.STROKE);
        paint_night.setStrokeWidth(5);
        paint_night.setStrokeJoin(Paint.Join.ROUND);

        Paint paint_point_night = new Paint();
        paint_night.setStyle(Paint.Style.STROKE);
        paint_point_night.setStrokeWidth(10);

        Path path_night = new Path();

        //记录第一个点，开始路径
        point_height = (int) (canvas_height - height_in_side - (temperature[0][1] -
                temperature_MIN) * height_each_K);
        point_width = (int) (windth_each_day / 2);
        canvas.drawPoint(point_width, point_height, paint_point_night);
        canvas.drawText(String.valueOf(temperature[0][1]), point_width-10, point_height + 40,
                paint_text);
        path_night.moveTo(point_width, point_height);

        for (int i = 1; i < temperature.length; i++) {
            point_height = (int) (canvas_height - height_in_side - (temperature[i][1] -
                    temperature_MIN) * height_each_K);
            point_width = (int) ((i + 0.5) * windth_each_day);
            canvas.drawPoint(point_width, point_height, paint_point_night);
            canvas.drawText(String.valueOf(temperature[i][1]), point_width-10, point_height + 40,
                    paint_text);
            path_night.lineTo(point_width, point_height);
        }
        canvas.drawPath(path_night, paint_night);


        /**
         * 绘制下方文字和图片
         */
        Paint paint_text_bottom=new Paint();
        paint_text_bottom.setTextSize(40);
        paint_text_bottom.setColor(Color.WHITE);

        for (int i=0;i<dateNum;i++){
            String text_weather_night=futureWeatherDates.get(i).getNight();
            String text_wind_direction=futureWeatherDates.get(i).getWind_direction();
            String text_wind_power=futureWeatherDates.get(i).getWind_power();
            canvas.drawText(text_weather_night, (float) (windth_each_day*(i+0.25)),(height_each_line*3+height_in_side+chart_height),paint_text_bottom);
            canvas.drawText(text_wind_direction, (float) (windth_each_day*(i+0.25)),(height_each_line*4+height_in_side+chart_height),paint_text_bottom);
            canvas.drawText(text_wind_power, (float) (windth_each_day*(i+0.25)),(height_each_line*5+height_in_side+chart_height),paint_text_bottom);

            Rect mDestRect = new Rect((int)(windth_each_day*(i+0.2)), (int)(height_in_side+chart_height+45), (int) ((windth_each_day*(i+0.2))+picture_height_windth), (int) (height_in_side+chart_height+45+picture_height_windth));
            if (text_weather_night.equals("阴")){
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.yin)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
            else if (text_weather_night.equals("晴")){
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.qing)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
            else if (text_weather_night.contains("云")){
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.duoyun)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
            else{
                Bitmap bitmap=((BitmapDrawable)getResources().getDrawable(R.mipmap.yu)).getBitmap();
                Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,null);
            }
        }
    }
}
