package com.xz.shangde.UI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xz.shangde.R;

import java.util.List;

/**
 * Created by yxq on 2018/5/9.
 */

public class ListViewAdapter_400 extends BaseAdapter {
    List<String> data;
    Context context;

    public ListViewAdapter_400(List<String> data,Context context) {
        this.data = data;
        this.context=context;
    }

    @Override
    public int getCount() {
        return data.size();
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
        if (convertView==null){
            convertView=View.inflate(context, R.layout.listview_item_400,null);
        }

        TextView textView=convertView.findViewById(R.id.tv_item_name_400);
        textView.setText(data.get(position));

        return convertView;
    }
}
