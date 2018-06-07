package com.xz.shangde.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xz.shangde.CalendarUtil;
import com.xz.shangde.Farm;
import com.xz.shangde.FrameAnimation;
import com.xz.shangde.FutureWeatherDate;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxq on 2018/4/26.
 */

public class Fragment100 extends Fragment {

    //    private Context context;

    private ImageView iv_show_background_100;

    //左上角
    private LinearLayout ll_weather_100;
    private TextView tv_position_city_100;
    private TextView tv_farm_name_100;
    private TextView tv_date_100;
    private TextView tv_week_100;
    private TextView tv_lunar_calendar_100;
    private TextView tv_temperature_100;
    private TextView tv_weather_100;
    private TextView tv_wind_direction_100;
    private TextView tv_wind_power_100;

    //右上角
    private ImageView iv_add_farm_100;

    //进入农场
    private LinearLayout ll_enter_farm_100;
    private TextView tv_report_detail_100;

    private String province;
    private String city;
    private String county;
    private static final int WEATHER_SUCCESS = 0;
    private static final int WEATHER_FAILURE = 1;
    private static final int MSE_REPORT_SUCCESS=2;

    private ArrayList<FutureWeatherDate> futureWeatherDateList;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WEATHER_SUCCESS:
                    Bundle bundle = (Bundle) msg.obj;
                    String city = bundle.getString("city");
                    tv_position_city_100.setText(city);
                    String temperature = bundle.getString("temperature");
                    tv_temperature_100.setText(temperature);
                    String weather = bundle.getString("weather");
                    tv_weather_100.setText(weather);
                    String wind_direction = bundle.getString("wind_direction");
                    tv_wind_direction_100.setText(wind_direction);
                    String wind_power = bundle.getString("wind_power");
                    tv_wind_power_100.setText(wind_power);
                    break;
                case WEATHER_FAILURE:
                    Toast.makeText(getContext(), "天气更新错误，请打开网络后重试", Toast.LENGTH_SHORT).show();
                    break;
                case MSE_REPORT_SUCCESS:
                    String report= (String) msg.obj;
                    tv_report_detail_100.setText(report);
                    break;
            }
        }
    };

    private int[] getRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.pictures);
        int len = typedArray.length();
        Log.i("TAG", String.valueOf(len));
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_100, container, false);

        //        context = getContext();

        initView(view);
        try {
            initDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initWeather();
        initReport();
        initListener();
        return view;
    }

    public void initView(View view) {
//        iv_show_background_100 = view.findViewById(R.id.iv_show_background_100);
        //        iv_show_background_100.setImageBitmap(decodeSampledBitmapFromResource
        // (getResources(),R.drawable.background,450,800));
        //        anim = (AnimationDrawable) iv_show_background_100.getDrawable();
        //        anim.start();

        //        int[] pictrues=getResources().getIntArray(R.array.pictures);
//        FrameAnimation frameAnimation = new FrameAnimation(iv_show_background_100, getRes(), 100,
//                true);
//        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
//            @Override
//            public void onAnimationStart() {
//                Log.i("TAG", "start");
//            }
//
//            @Override
//            public void onAnimationEnd() {
//                Log.i("TAG", "end");
//            }
//
//            @Override
//            public void onAnimationRepeat() {
//                Log.i("TAG", "repeat");
//            }
//        });

        iv_show_background_100=view.findViewById(R.id.iv_background);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //**这个属性很重要，是否支持缩放，false不支持缩放，就是图片按原比例展示**
        options.inScaled = false;
        //**设置图片比率，1是1/1、2是1/2、3是1/3,设置该参数会影响图片的质量**
        options.inSampleSize =1;

        //**使用RGB_565可以比系统默认RGB_888内存再减少1倍，会损失部分精度，测试看效果肉眼看不出来差距**

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap img =BitmapFactory.decodeResource(getResources(),R.mipmap.background,options);
        Drawable drawable = new BitmapDrawable(img);
//        iv_show_background_100.setBackground(drawable);
        Glide.with(getContext())
                .load(drawable)
                .into(iv_show_background_100);

        ll_weather_100 = view.findViewById(R.id.ll_weather_100);
        tv_position_city_100 = view.findViewById(R.id.tv_position_city_100);
        tv_farm_name_100 = view.findViewById(R.id.tv_farm_name_100);
        tv_date_100 = view.findViewById(R.id.tv_date_100);
        tv_week_100 = view.findViewById(R.id.tv_week_100);
        tv_lunar_calendar_100 = view.findViewById(R.id.tv_lunar_calendar_100);
        tv_temperature_100 = view.findViewById(R.id.tv_temperature_100);
        tv_weather_100 = view.findViewById(R.id.tv_weather_100);
        tv_wind_direction_100 = view.findViewById(R.id.tv_wind_direction_100);
        tv_wind_power_100 = view.findViewById(R.id.tv_wind_power_100);

        iv_add_farm_100 = view.findViewById(R.id.iv_add_farm_100);

        ll_enter_farm_100=view.findViewById(R.id.ll_enter_farm_100);
        tv_report_detail_100=view.findViewById(R.id.tv_report_detail_100);

        SharedPreferences sp = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        province = sp.getString("Province", "山东");
        city = sp.getString("City", "德州");
        county = sp.getString("County", "德城区");
    }

    public void initWeather() {
        futureWeatherDateList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("weather", "WeatherInfo is null");
                    String apilocation = "http://apicloud.mob" +
                            ".com/v1/weather/query?key=2560586b2f675&city=";
                    String url = apilocation + county + "&province=" + province;
                    Log.i("weather", url);
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url)
                            //请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    Log.i("weather", "response" + response.message());
                    Log.i("weather", "response" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        Log.i("kwwl", "response.code()==" + response.code());
                        Log.i("kwwl", "response.message()==" + response.message());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        String msg = response.body().string();
                        Log.i("weather", msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        String retcode = jsonObject.getString("retCode");
                        if (retcode.equals("200")) {
                            Log.i("weather", "200");
                            ParseWeatherJson(jsonObject);
                        } else if (retcode.equals("20402")) {
                            Log.i("weather", "20402");
                            String url_city = apilocation + city + "&province=" + province;
                            OkHttpClient client_city = new OkHttpClient();
                            Request request_city = new Request.Builder()
                                    .url(url_city)
                                    .build();
                            Response response_city = client_city.newCall(request_city)
                                    .execute();
                            if (response_city.isSuccessful()) {
                                String msg_city = response_city.body().string();
                                JSONObject jsonObject_city = new JSONObject(msg_city);
                                ParseWeatherJson(jsonObject_city);
                            }
                        }
                    }
                    response.close();
                } catch (Exception e) {
                    mhandler.obtainMessage(WEATHER_FAILURE).sendToTarget();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析天气的json文件
    public void ParseWeatherJson(JSONObject jsonObject) {
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            //            Log.i("Tag","result length"+result.length());
            JSONObject condition = result.getJSONObject(0);
            //            Log.i("Tag","condition:"+condition);

            String airCondition = condition.getString("airCondition");
            //            Log.i("Tag","aircondition"+airCondition);
            String city = condition.getString("city");
            //            Log.i("Tag","city"+city);
            String coldIndex = condition.getString("coldIndex");
            //            Log.i("Tag","coldIndex"+coldIndex);
            String date = condition.getString("date");
            //            Log.i("Tag","date"+date);
            String distrct = condition.getString("distrct");
            //            Log.i("Tag","distrct"+distrct);
            String dressingIndex = condition.getString("dressingIndex");
            //            Log.i("Tag","dressingIndex"+dressingIndex);
            String exerciseIndex = condition.getString("exerciseIndex");
            //            Log.i("Tag","exerciseIndex"+exerciseIndex);
            String humidity = condition.getString("humidity");
            //            Log.i("Tag","humidity"+humidity);
            String pollutionIndex = condition.getString("pollutionIndex");
            //            Log.i("Tag","pollutionIndex"+pollutionIndex);
            String province = condition.getString("province");
            //            Log.i("Tag","province"+province);
            String sunrise = condition.getString("sunrise");
            //            Log.i("Tag","sunrise"+sunrise);
            String sunset = condition.getString("sunset");
            //            Log.i("Tag","sunset"+sunset);
            String temperature = condition.getString("temperature");
            //            Log.i("Tag","temperature"+temperature);
            String time = condition.getString("time");
            //            Log.i("Tag","time"+time);
            String updateTime = condition.getString("updateTime");
            //            Log.i("Tag","updateTime"+updateTime);
            String washIndex = condition.getString("washIndex");
            //            Log.i("Tag","washIndex"+washIndex);
            String weather = condition.getString("weather");
            //            Log.i("Tag","weather"+weather);
            String week = condition.getString("week");
            //            Log.i("Tag","week"+week);
            String wind = condition.getString("wind");
            //            Log.i("Tag","wind"+wind);


            JSONArray future = condition.getJSONArray("future");
            for (int i = 0; i < future.length(); i++) {

                JSONObject everyday = future.getJSONObject(i);
                String date_future = everyday.getString("date");
                String date_futre_cut = date_future.substring(5);
                String dayTime_future = everyday.getString("dayTime");
                String night_future = everyday.getString("night");
                String temperature_future = everyday.getString("temperature");
                String temperater_daytime = temperature_future.split("/")[0];
                String temperater_night = temperature_future.split("/")[1];
                String week_future = everyday.getString("week");
                String wind_future = everyday.getString("wind");
                String wind_direction = wind_future.split(" ")[0];
                String wind_power = wind_future.split(" ")[1];
                //                Log.i("Tag",week_future+"是"+date_future+"白天"+dayTime_future+"晚间"
                //                        +night_future+"气温"+temperature_future+"风力"+wind_future);

                FutureWeatherDate futureWeatherDate = new FutureWeatherDate(date_futre_cut,
                        dayTime_future
                        , night_future, temperater_daytime, temperater_night, week_future,
                        wind_power, wind_direction);
                futureWeatherDateList.add(futureWeatherDate);
            }
            String wind_direction = wind.split("风")[0] + "风";
            String wind_power = wind.split("风")[1];
            Bundle bundle = new Bundle();
            bundle.putString("city", city);
            bundle.putString("temperature", temperature);
            bundle.putString("weather", weather);
            bundle.putString("wind_direction", wind_direction);
            bundle.putString("wind_power", wind_power);
            mhandler.obtainMessage(WEATHER_SUCCESS, bundle).sendToTarget();

        } catch (JSONException e) {
            mhandler.obtainMessage(WEATHER_FAILURE).sendToTarget();
            e.printStackTrace();
        }

    }

    //初始化时间控件
    public void initDate() throws Exception {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat format2 = new SimpleDateFormat("E");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyyMMdd");
        String s = CalendarUtil.solarToLunar(format3.format(date));
        String lunar = "农历" + s.split("年")[1];
        tv_date_100.setText(format1.format(date));
        tv_week_100.setText(format2.format(date));
        tv_lunar_calendar_100.setText(lunar);
    }

    public void initListener() {
        iv_add_farm_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFarmActivity.class);
                startActivity(intent);
            }
        });

        ll_enter_farm_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),FieldsActivity.class);
                startActivity(intent);
            }
        });

        ll_weather_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_temperature_100.getText().toString().equals("温度")) {
                    Toast.makeText(getContext(), "数据获取错误", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("data", futureWeatherDateList);
                    intent.putExtras(b);
                    intent.putExtra("temperature",tv_temperature_100.getText());
                    intent.putExtra("weather",tv_weather_100.getText());
                    intent.putExtra("wind",tv_wind_direction_100.getText()+"  "+tv_wind_power_100.getText());
                    startActivity(intent);
                }
            }
        });
    }

    public void initReport(){
        new Thread(){
            @Override
            public void run() {
                GetReport();
            }
        }.start();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFilePath(String imagePath,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    //获取农场简报，GET方法，所用接口：Farm/GetFarmbrief?farmid={farmid}
    public void GetReport(){
        ShangdeApplication application= (ShangdeApplication) getActivity().getApplication();
        String url=application.getURL();
        int farmid=application.getFarm().getFarm_ID();

        OkHttpClient client_CreateFarm=application.getClient();
        Request request_CreateFarm=new Request.Builder()
                .url(url+"Farm/GetFarmbrief?farmid="+farmid)
                .build();
        try {
            Response response_CreateFarm=client_CreateFarm.newCall(request_CreateFarm).execute();
            if (response_CreateFarm.isSuccessful()){
                String msg=response_CreateFarm.body().string();
                Log.i("TAG",msg);
                JSONObject data=new JSONObject(msg);
                boolean status=data.getBoolean("status");
                if (status){
                    //todo 根据传过来的格式获取简报信息并通过handler显示
                    mhandler.obtainMessage(MSE_REPORT_SUCCESS,msg).sendToTarget();
                }
                }
                response_CreateFarm.close();
            } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
