package com.xz.shangde.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xz.shangde.News;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxq on 2018/5/24.
 */

public class NewsActivity extends AppCompatActivity {
    private ListView list_news;
    private ArrayList<News> news;
    private Toolbar tb_news_activity;

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ShangdeApplication application= (ShangdeApplication) getApplication();
            news=application.getNews();

            ListViewAdapter_news adapter_news=new ListViewAdapter_news(news,getApplicationContext());
            list_news.setAdapter(adapter_news);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        tb_news_activity=findViewById(R.id.tb_news_activity);
        setSupportActionBar(tb_news_activity);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_news_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Thread(){
            @Override
            public void run() {
                ArrayList<News> n=DownloadNews(3);
                ShangdeApplication application= (ShangdeApplication) getApplication();
                application.setNews(n);
                mhandler.obtainMessage().sendToTarget();
            }
        }.start();

        list_news=findViewById(R.id.list_news);

        list_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),NewsPageActivity.class);
                intent.putExtra("URL",news.get(position).getNews_URL());
                startActivity(intent);
            }
        });
    }

    //获取新闻资讯，GET方法，所用接口：News/GetNewsType?newstype={newstype}
    public ArrayList<News> DownloadNews(int type){
        ShangdeApplication application = (ShangdeApplication) getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .header("Token",token)
                .url(url+"News/GetNewsType?newstype="+type)
                .build();
        ArrayList<News> ns=new ArrayList<>();
        try {
            Response response=client.newCall(request).execute();
            Log.i("TAG",response.message());
            if (response.isSuccessful()){
                String s=response.body().string();
                Log.i("TAG",s);
                JSONObject jsonObject=new JSONObject(s);
                boolean status=jsonObject.getBoolean("status");
                if (status){
                    JSONArray data=jsonObject.getJSONArray("data");
                    for (int j=0;j<data.length();j++){
                        JSONObject news=data.getJSONObject(j);
                        int id=news.getInt("id");
                        Log.i("TAG","id:"+id);
                        int newstype=news.getInt("news_type");
                        Log.i("TAG","newstype:"+newstype);
                        String summary=news.getString("summary");
                        Log.i("TAG","summary:"+summary);
                        String date=news.getString("news_date");
                        Log.i("TAG","date:"+date);
                        String news_url=news.getString("url");
                        Log.i("TAG","news_url:"+news_url);
                        String thumb=news.getString("thumb");
                        Log.i("TAG","thumb:"+thumb);

                        News n=new News(id,newstype,summary,date,news_url,thumb);
                        ns.add(n);
                    }
                }
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ns;
    }
}
