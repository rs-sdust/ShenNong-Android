package com.xz.shangde.UI;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.xz.shangde.R;

/**
 * Created by yxq on 2018/5/24.
 */

public class LoadDataActivity extends AppCompatActivity {
    private ImageView iv_wait;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        iv_wait=findViewById(R.id.iv_wait);
        AnimationDrawable animationDrawable= (AnimationDrawable) iv_wait.getBackground();
        animationDrawable.start();
    }
}
