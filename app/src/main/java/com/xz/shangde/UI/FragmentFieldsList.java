package com.xz.shangde.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author zxz
 * 地块列表的fragment，显示在FieldsActivity中
 * 用于表现农场的地块数量、面积以及详细信息列表（ExpListViewAdapter）
 */

public class FragmentFieldsList extends Fragment {

    private ArrayList<Field> fields;
    private LinkedHashMap<Integer,String> croptype_map;

    private TextView tv_area;
    private TextView tv_fields_number;
    private ExpandableListView lv_expandable_fields;

    private Button btn_refresh_data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fields_list, null);

        ShangdeApplication application= (ShangdeApplication) getActivity().getApplication();
        fields=application.getFields();
        croptype_map=application.getCroptype_map();

        initView(view);

        return view;
    }

    public void initView(View view) {
        tv_area = view.findViewById(R.id.tv_area);
        tv_fields_number = view.findViewById(R.id.tv_fields_number);
        lv_expandable_fields = view.findViewById(R.id.lv_expandable_fields);

        initTextView();
        initExpandableList(view);
    }

    //在更新完信息后，再次调用该方法来重新计算面积和地块数量
    public void initTextView() {
        float area = 0;
        for (int i = 0; i < fields.size(); i++) {
            area += fields.get(i).getField_Area();
        }
        double mu=area*9/6000;
        DecimalFormat Myformat=new DecimalFormat("#.00");
        String s=Myformat.format(mu)+"亩";
        tv_area.setText(s);
        tv_fields_number.setText(String.valueOf(fields.size()));
    }

    public void initExpandableList(View view) {
        final ExpListViewAdapter adapter = new ExpListViewAdapter(getContext(), fields,croptype_map);
        lv_expandable_fields.setAdapter(adapter);

        lv_expandable_fields.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
                    childPosition, long id) {
                Intent intent = new Intent(getContext(), FieldPropertyActivity.class);
                int postion = adapter.findPosition(groupPosition, childPosition);
                intent.putExtra("position", postion);
                startActivity(intent);
                return true;
            }
        });

        //刷新的方法，以后可以扩展这个功能
        btn_refresh_data=view.findViewById(R.id.btn_refresh_data);
        btn_refresh_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.parseFields();
                initTextView();
                adapter.notifyDataSetChanged();
            }
        });
    }

}
