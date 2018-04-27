package com.xz.shangde.UI;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.shangde.R;

/**
 * Created by yxq on 2018/4/26.
 */

public class GridviewAdapter extends BaseAdapter {

    Context context;
    int[] imagedata={R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,R.drawable.ic_launcher_background};
    String[] stringdata={"员工","地块","农事","种子","肥料","农药","设备","仓储","市场"};

    public GridviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return imagedata.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView==null){
            view=View.inflate(context,R.layout.gridview_item,null);

            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }

        ImageView imageView=viewHolder.getImageView();
        imageView.setImageResource(imagedata[position]);
        TextView textView=viewHolder.getTextView();
        textView.setText(stringdata[position]);

        return view;
    }

    class ViewHolder{
        View view;
        ImageView imageView;
        TextView textView;

        public ViewHolder(View view) {
            this.view = view;
        }

        public ImageView getImageView() {
            if (this.imageView==null){
                imageView=view.findViewById(R.id.image_gridview);
            }
            return imageView;
        }

        public TextView getTextView() {
            if (this.textView==null){
                textView=view.findViewById(R.id.tv_gridview);
            }
            return textView;
        }
    }
}
