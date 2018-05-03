package com.xz.shangde.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.xz.shangde.R;

/**
 * Created by yxq on 2018/4/26.
 */

public class Fragment100 extends Fragment {

    private GridView gridView;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_100,container,false);
        gridView=view.findViewById(R.id.gridview_100);
        gridView.setAdapter(new GridviewAdapter(getActivity()));
        toolbar=view.findViewById(R.id.NavigaterBar_100);
        toolbar.inflateMenu(R.menu.toolbar_menu_add_100);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId==R.id.action_add_farm_100){
                    //todo 新建农场地块
                    Toast.makeText(getActivity(),"点击事件", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),AddFarmActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        return view;
    }
}
