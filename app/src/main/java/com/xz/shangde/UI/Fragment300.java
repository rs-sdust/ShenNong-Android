package com.xz.shangde.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.xz.shangde.Market;
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
 * Created by yxq on 2018/4/26.
 */

public class Fragment300 extends Fragment {

    private Context context;

    private ProgressBar progressBar_300;

    private ViewPager viewPager;
    private ImageView iv_watch_news;
    private ImageView iv_search_market;
    private ImageView iv_learn_tech;
    private ListView list_news_300;

    private ArrayList<News> TopNews=new ArrayList<>();
    private ArrayList<News> RecommandNews=new ArrayList<>();

    private static final int MSE_DOWNLOAD_SUCCESS=0;
    private static final int MSE_DOWNLOAD_FAILED=1;
    private static final int MSE_TOP_NEWS_DOWNLOAD_SUCCESS=2;
    private static final int MSE_RECOMMAND_NEWS_DOWNLOAD_SUCCESS=3;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSE_DOWNLOAD_FAILED:
                    break;
                case MSE_TOP_NEWS_DOWNLOAD_SUCCESS:
                    TopNewsAdapter adapter=new TopNewsAdapter(context,TopNews);
                    viewPager.setAdapter(adapter);
                    break;
                case MSE_RECOMMAND_NEWS_DOWNLOAD_SUCCESS:
                    ListViewAdapter_news recommand_news_adapter=new ListViewAdapter_news(RecommandNews,context);
                    list_news_300.setAdapter(recommand_news_adapter);
//                    progressBar_300.setVisibility(View.INVISIBLE);
                    break;
                case MSE_DOWNLOAD_SUCCESS:
                    int position=viewPager.getCurrentItem();
                    int max_position=RecommandNews.size();
                    if (position<max_position){
                        viewPager.setCurrentItem(position+1,true);
                    }
                    else if (position==max_position){
                        viewPager.setCurrentItem(0,true);
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_300,container,false);

        initView(view);
        initListener();

        DownloadData();

        return view;
    }

    public void initView(View view){
        context=getContext();

        progressBar_300=view.findViewById(R.id.progressbar_300);

        viewPager=view.findViewById(R.id.viewpager_top_news);
        iv_learn_tech=view.findViewById(R.id.iv_learn_tech);
        iv_watch_news=view.findViewById(R.id.iv_watch_news);
        iv_search_market=view.findViewById(R.id.iv_search_market);
        list_news_300=view.findViewById(R.id.list_news_300);
    }

    public void initListener(){
        iv_watch_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,NewsActivity.class);
                startActivity(intent);
            }
        });

        iv_search_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MarketActivity.class);
                startActivity(intent);
            }
        });

        list_news_300.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news=RecommandNews.get(position);
                String url=news.getNews_URL();
                Intent intent=new Intent(context,NewsPageActivity.class);
                intent.putExtra("URL",url);
                startActivity(intent);
            }
        });

        new Thread(){
            @Override
            public void run() {
                try {
                    while (true){
                        Thread.sleep(5000);
                        handler.obtainMessage(MSE_DOWNLOAD_SUCCESS).sendToTarget();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void DownloadData(){
        progressBar_300.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                DownloadMarket();
                //获取头条新闻
                TopNews=DownloadNews(1);
                handler.obtainMessage(MSE_TOP_NEWS_DOWNLOAD_SUCCESS).sendToTarget();
                //获取推荐新闻
                RecommandNews=DownloadNews(2);
                handler.obtainMessage(MSE_RECOMMAND_NEWS_DOWNLOAD_SUCCESS).sendToTarget();
            }
        }.start();
    }

    //获取市场信息，GET方法，所用接口：News/GetMarket?crop_type={crop_type}
    public void DownloadMarket(){
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");

        OkHttpClient client=new OkHttpClient();
        ArrayList<Market> markets=new ArrayList<>();
        for (int i=0;i<2;i++){
            Request request=new Request.Builder()
                    .header("Token",token)
                    .url(url+"News/GetMarket?crop_type="+i)
                    .build();
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
                            JSONObject market=data.getJSONObject(j);
                            int id=market.getInt("id");
                            int croptype=market.getInt("crop_type");
                            float cropprice= (float) market.getDouble("crop_price");
                            String date=market.getString("datetime");

                            Market m=new Market(id,croptype,cropprice,date);
                            markets.add(m);
                        }
                        application.setMarkets(markets);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //获取新闻资讯，GET方法，所用接口：News/GetNewsType?newstype={newstype}
    public ArrayList<News> DownloadNews(int type){
        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
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
