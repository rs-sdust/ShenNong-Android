package com.xz.shangde.UI;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zxz
 * 批量修改分组时所用的dialog
 * 提供修改种植作物和种植时间两个接口
 */

public class ModifyDialog extends Dialog {

    private LinkedHashMap<Integer,String> croptype_map=new LinkedHashMap<>();
    private ArrayList<String> croptype_name=new ArrayList<>();

    private TextView text_title;
    private Button btn_commit;
    private Spinner spinner_modify_crop_type;
    private DatePicker datepicker_modify;

    public ModifyDialog(Context context, String title, String name) {
        super(context, R.style.noTitleDialog);

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_modify, null);
        text_title = (TextView) view.findViewById(R.id.text_title);
        btn_commit = (Button) view.findViewById(R.id.btn_commit);
        spinner_modify_crop_type=view.findViewById(R.id.spinner_modify_crop_type);
        datepicker_modify=view.findViewById(R.id.datepicker_modify);

        text_title.setText(title);

        ShangdeApplication application= (ShangdeApplication) getContext().getApplicationContext();
        croptype_map=application.getCroptype_map();

        Iterator entries1 = croptype_map.entrySet().iterator();
        while (entries1.hasNext()) {
            Map.Entry entry = (Map.Entry) entries1.next();
            String value = (String) entry.getValue();
            croptype_name.add(value);
        }
        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,croptype_name);
        spinner_modify_crop_type.setAdapter(adapter);

        super.setContentView(view);
    }

    public Spinner getSpinner_modify_crop_type(){return spinner_modify_crop_type;}

    public DatePicker getDatepicker_modify(){return datepicker_modify;}

    public void setOnClickCommitListener(View.OnClickListener listener){
        btn_commit.setOnClickListener(listener);
    }
}
