package com.xz.shangde.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.xz.shangde.R;

/**
 * Created by yxq on 2018/4/26.
 */

public class Fragment100 extends Fragment {

    private GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_100,container,false);
        gridView=view.findViewById(R.id.gridview_100);
        gridView.setAdapter(new GridviewAdapter(getActivity()));
        return view;
    }
}
