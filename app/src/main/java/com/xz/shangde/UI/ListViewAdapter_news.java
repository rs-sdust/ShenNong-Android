package com.xz.shangde.UI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xz.shangde.News;
import com.xz.shangde.R;

import java.util.ArrayList;

/**
 * @author zxz
 * 以listview的方式罗列所有的资讯信息
 */

public class ListViewAdapter_news extends BaseAdapter{
    private ArrayList<News> news;
    private Context context;

    public ListViewAdapter_news(ArrayList<News> news, Context context) {
        this.news = news;
        this.context = context;
    }

    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public Object getItem(int position) {
        News news_single=news.get(position);
        return news_single;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){
            convertView=View.inflate(context, R.layout.listview_item_news,null);
        }

        ImageView iv_news_thumb=convertView.findViewById(R.id.iv_news_thumb);
        TextView tv_news_summary=convertView.findViewById(R.id.tv_news_summary);
        TextView tv_news_date=convertView.findViewById(R.id.tv_news_date);

        News New=news.get(position);
        Glide.with(context)
                .load(New.getNews_thumb_URL())
                .into(iv_news_thumb);
        tv_news_summary.setText(New.getNews_summary());
        tv_news_date.setText(New.getNews_date());

        return convertView;
    }
}
