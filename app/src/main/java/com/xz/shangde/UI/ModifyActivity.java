package com.xz.shangde.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xz.shangde.R;

import java.util.ArrayList;

/**
 * @author zxz
 * 批量修改分组时所用的dialog，停止使用，转而使用ModifyDialog
 */

@Deprecated
public class ModifyActivity extends AppCompatActivity {

    private ArrayList<String> crop_type;
    private int crop_index;

    private Toolbar tb_modify_fields_property;
//    private TextView tv_original_crop;
    private EditText et_modify_crop;
//    private TextView tv_original_time;
    private EditText et_modify_time;
    private Button btn_commit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_modify);

        initView();
        initData();
    }

    public void initView(){
        tb_modify_fields_property=findViewById(R.id.tb_modify_fields_property);
//        tv_original_crop=findViewById(R.id.tv_original_crop);
//        et_modify_crop=findViewById(R.id.et_modify_crop);
//        tv_original_time=findViewById(R.id.tv_original_time);
//        et_modify_time=findViewById(R.id.et_modify_time);
        btn_commit=findViewById(R.id.btn_commit);

        setSupportActionBar(tb_modify_fields_property);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_modify_fields_property.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initData(){
        Intent intent=getIntent();
        crop_type=intent.getStringArrayListExtra("crop_type");
        crop_index=intent.getIntExtra("crop_index",0);
    }
}
