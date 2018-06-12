package com.xz.shangde.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;
import com.xz.shangde.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 判断用户身份并显示TASK，允许权限用户操作TASK
 */

public class TaskActivity extends AppCompatActivity {

    private Toolbar tb_Task;
    private ListView listview_task;

    private ArrayList<Task> tasks=new ArrayList<>();

    private final int MSE_DOWNLOAD_TASK_SUCCESS=0;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSE_DOWNLOAD_TASK_SUCCESS:
                    initListView();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initView();

        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        int role=sharedPreferences.getInt("Role",0);
        if (role==0){
            DownloadTask();
        }

    }

    public void initView(){
        tb_Task=findViewById(R.id.tb_Task);
        listview_task=findViewById(R.id.listview_task);

        setSupportActionBar(tb_Task);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_Task.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void initListView(){
        ShangdeApplication application= (ShangdeApplication) getApplication();
        String url=application.getURL();
        SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
        String token=sp.getString("Token","");
        ListViewAdapter_Task adapter_task=new ListViewAdapter_Task(this,tasks,url,token);
        listview_task.setAdapter(adapter_task);
    }

    //获取指定农场的所有信息,GET方法,所用接口：Task/GetFarmerTask?examiner={examiner}
    public void DownloadTask(){
        new Thread(){
            @Override
            public void run() {
                ShangdeApplication application = (ShangdeApplication) getApplication();
                String url = application.getURL();
                SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("Token", "");
                int userid=sharedPreferences.getInt("User_ID",0);

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Task/GetFarmerTask?examiner="+userid)
                        .build();
                try {
                    Response response=client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String s=response.body().string();
                        Log.i("TAG",s);
                        JSONObject jsonObject=new JSONObject(s);
                        boolean status=jsonObject.getBoolean("status");
                        if (status){
                            JSONArray data=jsonObject.getJSONArray("data");
                            for (int i=0;i<data.length();i++){
                                JSONObject object=data.getJSONObject(i);
                                int id=object.getInt("id");
                                int creator=object.getInt("creator");
                                int farm=object.getInt("farm");
                                int examiner=object.getInt("examiner");
                                int type=object.getInt("type");
                                String description=object.getString("description");
                                int state=object.getInt("state");
                                boolean agree=object.getBoolean("agree");
                                String createdate=object.getString("createdate");
                                String processdate=object.getString("processdate");

                                Task task=new Task(id,creator,farm,examiner,type,description,state,agree,createdate,processdate);
                                tasks.add(task);
                            }
                        }
                        mhandler.obtainMessage(MSE_DOWNLOAD_TASK_SUCCESS).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
