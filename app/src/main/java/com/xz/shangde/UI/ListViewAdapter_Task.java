package com.xz.shangde.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;
import com.xz.shangde.Task;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 以listview的方式罗列所有的TASK信息
 */

public class ListViewAdapter_Task extends BaseAdapter {
    private Context context;
    private ArrayList<Task> tasks;

    private String url;
    private String token;

    public ListViewAdapter_Task(Context context, ArrayList<Task> tasks, String url, String token) {
        this.context = context;
        this.tasks = tasks;
        this.url = url;
        this.token = token;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        Task task=tasks.get(position);
        return task;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=View.inflate(context, R.layout.list_item_task,null);
        }

        TextView tv_description_task=convertView.findViewById(R.id.tv_description_task);
        TextView tv_date_task=convertView.findViewById(R.id.tv_date_task);
        final Button btn_agree_task=convertView.findViewById(R.id.btn_agree_task);
        final Button btn_reject_task=convertView.findViewById(R.id.btn_reject_task);

        final Task task=tasks.get(position);
        tv_description_task.setText(task.getDescription());
        int state=task.getState();
        if (state==0){
            //未处理
            tv_date_task.setText("申请时间："+task.getCreatedate()+"  未处理");
            btn_agree_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProcessTask(task,true);
                    btn_agree_task.setVisibility(View.INVISIBLE);
                    btn_reject_task.setVisibility(View.INVISIBLE);
                }
            });

            btn_reject_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProcessTask(task,false);
                    btn_agree_task.setVisibility(View.INVISIBLE);
                    btn_reject_task.setVisibility(View.INVISIBLE);
                }
            });
        }
        else if (state==1){
            btn_agree_task.setVisibility(View.INVISIBLE);
            btn_reject_task.setVisibility(View.INVISIBLE);
            //已经处理
            boolean agree=task.isAgree();
            if (agree){
                tv_date_task.setText("申请时间："+task.getCreatedate()+",处理时间："+task.getProcessdate()+"。  已通过");
            }else {
                tv_date_task.setText("申请时间："+task.getCreatedate()+",处理时间："+task.getProcessdate()+"。  已拒绝");
            }
        }


        return convertView;
    }

    //处理请求，POST方法，所用接口：Task/ProcessTask
    public void ProcessTask(final Task task, final boolean agree){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                FormBody formBody=new FormBody.Builder()
                        .add("creator", String.valueOf(task.getCreator()))
                        .add("farm", String.valueOf(task.getFarmid()))
                        .add("agree", String.valueOf(agree))
                        .add("type", String.valueOf(task.getType()))
                        .build();
                Request request=new Request.Builder()
                        .header("Token",token)
                        .url(url+"Task/ProcessTask")
                        .post(formBody)
                        .build();
                try {
                    Response response=client.newCall(request).execute();
                    Log.i("TAG",response.message());
                    Log.i("TAG",response.body().string());
                    if (response.isSuccessful()){

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
