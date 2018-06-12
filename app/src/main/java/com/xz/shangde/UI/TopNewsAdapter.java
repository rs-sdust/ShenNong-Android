package com.xz.shangde.UI;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xz.shangde.News;
import com.xz.shangde.R;

import java.util.ArrayList;

/**
 * @author zxz
 * 头条新闻的显示adapter
 */

public class TopNewsAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<News> TopNews;
    private LayoutInflater layoutInflater;

    public TopNewsAdapter(Context context, ArrayList<News> topNews) {
        this.context = context;
        TopNews = topNews;
    }

    @Override
    public int getCount() {
        return TopNews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.top_news_image, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.iv_top_news);

        Glide.with(context)
                .load(TopNews.get(position).getNews_thumb_URL())
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,NewsPageActivity.class);
                intent.putExtra("URL",TopNews.get(position).getNews_URL());
                context.startActivity(intent);
            }
        });

        ViewPager vp = (ViewPager)container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager)container;
        View view = (View)object;
        vp.removeView(view);
    }
}
