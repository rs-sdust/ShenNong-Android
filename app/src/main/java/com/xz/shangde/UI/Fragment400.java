package com.xz.shangde.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.xz.shangde.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxz
 * MainActivity下的第四个fragment，对应于底部导航栏为“我的”
 * 用于编辑个人及农场信息、提供设置等其他接口
 */

public class Fragment400 extends Fragment {

    private List<String> item_name;
    private Context context;
    private ListView listView;
    private ImageView iv_my_info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_400, container, false);

        context = getContext();
        init(view);
        ListViewAdapter_400 adapter = new ListViewAdapter_400(item_name, context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent=new Intent(context,TaskActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent intent1=new Intent(context,FeedbackActivity.class);
                        startActivity(intent1);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
            }
        });

        iv_my_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EditInfoActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void init(View view) {
        listView = view.findViewById(R.id.listview_info_400);
        iv_my_info=view.findViewById(R.id.iv_my_info);
        initData();
    }

    public void initData() {
        item_name = new ArrayList<>();
        item_name.add("我的消息");
        item_name.add("我的伙伴");
        item_name.add("分享APP");
        item_name.add("意见反馈");
        item_name.add("联系客服");
        item_name.add("设置");
    }
}
