package com.xz.shangde.UI;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.shangde.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxq on 2018/4/26.
 */

public class EditInfoActivity extends Activity {

    Spinner spinner_province;
    Spinner spinner_city;
    Spinner spinner_county;
    Spinner spinner_degree_edit_info;
    EditText et_name_edit_info;
    TextView tv_phone_edit_info;
    RadioGroup rg_sex_edit_info;
    RadioButton rb_man_edit_sex;
    RadioButton rb_woman_edit_sex;
    EditText farm_name_print;
    Spinner farm_name_select;
    Button save_edit_person_info;

    StringBuilder sb = new StringBuilder();
    JSONArray data;
    JSONArray city_array;
    List<String> province_name = new ArrayList<>();
    List<String> city_name = new ArrayList<>();
    List<String> county_name = new ArrayList<>();
    Context context = null;
    String[] degree=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        initView();
    }

    public void initView() {
        spinner_province = findViewById(R.id.spinner_province);
        spinner_city = findViewById(R.id.spinner_city);
        spinner_county = findViewById(R.id.spinner_county);
        spinner_degree_edit_info = findViewById(R.id.spinner_degree_edit_info);
        et_name_edit_info = findViewById(R.id.et_name_edit_info);
        tv_phone_edit_info = findViewById(R.id.tv_phone_edit_info);
        rg_sex_edit_info = findViewById(R.id.rg_sex_edit_info);
        rb_man_edit_sex = findViewById(R.id.rb_man_edit_sex);
        rb_woman_edit_sex = findViewById(R.id.rb_woman_edit_sex);
        farm_name_print = findViewById(R.id.farm_name_print);
        farm_name_select = findViewById(R.id.farm_name_select);
        save_edit_person_info=findViewById(R.id.save_edit_person_info);

        context = this;

        Resources res = getResources();
        degree = res.getStringArray(R.array.degrees);

        initLocation();
        initSex();
        initDegree();
        initUpload();
    }

    public void initLocation() {
        updatelocation update = new updatelocation();
        update.execute();

        spinner_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    city_name.clear();
                    JSONObject data_province = data.getJSONObject(position);

                    city_array = data_province.getJSONArray("city");
                    Log.i("data_province", city_array.toString());
                    for (int i = 0; i < city_array.length(); i++) {
                        JSONObject city = city_array.getJSONObject(i);
                        String cityname = city.getString("name");
                        city_name.add(cityname);
                    }
                    Log.i("data_province", city_name.toString());
                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout
                            .simple_spinner_item, city_name);
                    spinner_city.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("data_province", "wrong");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                county_name.clear();
                Log.i("data_city", city_array.toString());
                try {
                    JSONObject county = city_array.getJSONObject(position);
                    JSONArray county_array = county.getJSONArray("county");
                    for (int i = 0; i < county_array.length(); i++) {
                        String countyname = county_array.getString(i);
                        county_name.add(countyname);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R
                            .layout.simple_spinner_item, county_name);
                    spinner_county.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, county_name.get(position), Toast.LENGTH_LONG).show();

                //todo 异步访问服务器，查询该城市中的所有农场名称
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initSex() {
        rg_sex_edit_info.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int color = 0x00FF00;
                int color_main = 0x51AD58;
                Log.i("rg_sex_edit_info", "rb_man_edit_sex");
                switch (checkedId) {
                    case R.id.rb_man_edit_sex:
                        rb_man_edit_sex.setBackgroundResource(R.drawable.button_login_shape);
                        rb_woman_edit_sex.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        break;
                    case R.id.rb_woman_edit_sex:
                        rb_woman_edit_sex.setBackgroundResource(R.drawable.button_login_shape);
                        rb_man_edit_sex.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        break;
                }
            }
        });
    }

    public void initDegree() {
        spinner_degree_edit_info.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context,degree[position],Toast.LENGTH_LONG).show();
                switch (position) {
                    case 0:
                        farm_name_print.setVisibility(View.VISIBLE);
                        farm_name_select.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        farm_name_print.setVisibility(View.INVISIBLE);
                        farm_name_select.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    public void initUpload(){
        save_edit_person_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"save the configuration",Toast.LENGTH_LONG).show();
                //todo 上传数据
            }
        });
    }

//    解析省-地区-县 名json文件
    public class updatelocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            AssetManager assetManager = context.getAssets();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                        (assetManager.open("country.json"), "utf-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Tag", sb.toString());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = new JSONObject(sb.toString());
                data = jsonObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject data_cities = data.getJSONObject(i);
                    String province = data_cities.getString("name");
                    province_name.add(province);
                    Log.i("Tag", province);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout
                    .simple_spinner_item, province_name);
            spinner_province.setAdapter(adapter);
        }
    }
}
