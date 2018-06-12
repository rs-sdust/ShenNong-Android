package com.xz.shangde.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import com.xz.shangde.Farm;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;
import com.xz.shangde.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 个人信息和农场信息类
 * 在注册时会在LoginActivity后弹出，在登录后修改信息时会重用该类
 * 判断的参数为boolean Logedin，登陆完成后修改其为true
 */

public class EditInfoActivity extends Activity {

    private Spinner spinner_province;
    private Spinner spinner_city;
    private Spinner spinner_county;
    private Spinner spinner_degree_edit_info;
    private EditText et_name_edit_info;
    private TextView tv_phone_edit_info;
    private EditText farm_name_print;
    private Spinner farm_name_select;
    private Button save_edit_person_info;

    private StringBuilder sb = new StringBuilder();
    private JSONArray data;
    private JSONArray city_array;
    private List<String> province_name = new ArrayList<>();
    private List<String> city_name = new ArrayList<>();
    private List<String> county_name = new ArrayList<>();
    private Context context = null;
    private String[] degree = null;

    private SharedPreferences sp;
    private boolean Logedin;

    private String Token;

    private int User_ID;
    private String User_Name;
    private String PhoneNumber;
    private int Role;
    private int Farm;

    private int FarmID;
    private String FarmName;
    private int province_index;
    private int city_index;
    private int county_index;
    private String province;
    private String city;
    private String county;

    private Dialog dialog;

    private boolean select_changed = false;
    //存储获取的附近农场的信息
    private ArrayList<String> farm_name = new ArrayList<>();
    private ArrayList<FarmNearby> nearbyFarms = new ArrayList<>();
    private final int MSE_LOCATION_CHANGED = 0;
    private final int MSE_DEGREE_CHANGED = 1;
    private final int MSE_FAILED = 2;
    private final int MSE_SUCCESS = 3;
    private final int MSE_UPLOAD_SUCCESS = 4;
    private final int MSE_UPLOAD_FAILED = 5;
    private final int MSE_PHARSE_SUCCESS = 6;
    private final int MSE_CREATED_TASK_SUCCESS=7;
    private Handler mhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSE_DEGREE_CHANGED:
                    SearchFarms();
                    break;
                case MSE_LOCATION_CHANGED:
                    SearchFarms();
                    break;
                case MSE_FAILED:
                    Toast.makeText(context, "获取附近农场失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSE_SUCCESS:
                    ShowNearbyFarm();
                    break;
                case MSE_UPLOAD_SUCCESS:
                    saveSP();
                    break;
                case MSE_UPLOAD_FAILED:
                    break;
                case MSE_PHARSE_SUCCESS:
                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, province_name);
                    spinner_province.setAdapter(adapter);
                    spinner_province.setSelection(province_index, true);
                    break;
                case MSE_CREATED_TASK_SUCCESS:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("请等待");
                    builder.setMessage("您的申请已上传，请稍候重试");//提示内容
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        initData();
        initView();

    }

    public void initView() {
        spinner_province = findViewById(R.id.spinner_province);
        spinner_city = findViewById(R.id.spinner_city);
        spinner_county = findViewById(R.id.spinner_county);
        spinner_degree_edit_info = findViewById(R.id.spinner_degree_edit_info);
        et_name_edit_info = findViewById(R.id.et_name_edit_info);
        tv_phone_edit_info = findViewById(R.id.tv_phone_edit_info);
        farm_name_print = findViewById(R.id.farm_name_print);
        farm_name_select = findViewById(R.id.farm_name_select);
        save_edit_person_info = findViewById(R.id.save_edit_person_info);

        context = this;

        //预加载数据
        if (Logedin) {
            et_name_edit_info.setText(User_Name);
            spinner_degree_edit_info.setSelection(Role, true);
            if (Role == 0) {
                farm_name_print.setText(FarmName);
            } else if (Role == 1) {
                farm_name_print.setVisibility(View.INVISIBLE);
                farm_name_select.setVisibility(View.VISIBLE);
            }
        }
        tv_phone_edit_info.setText(PhoneNumber);
        Resources res = getResources();
        degree = res.getStringArray(R.array.degrees);

        initLocation();
        initDegree();
        initUpload();


    }

    //从SharedPreferences中查看是否已有信息
    public void initData() {
        sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        Logedin = sp.getBoolean("Logedin", false);
        Token = sp.getString("Token", "unknow");
        PhoneNumber = sp.getString("PhoneNumber", "11111111111");
        if (Logedin) {
            User_ID = sp.getInt("User_ID", -1);
            User_Name = sp.getString("User_Name", "something wrong");
            Role = sp.getInt("Role", 0);
            Farm = sp.getInt("Farm", -1);

            FarmID = sp.getInt("FarmID", -1);
            FarmName = sp.getString("FarmName", "unknow");
            province_index = sp.getInt("ProvinceIndex", 0);
            city_index = sp.getInt("CityIndex", 0);
            county_index = sp.getInt("CountyIndex", 0);
        } else {
            return;
        }
    }

    //初始化省市县三级spinner
    public void initLocation() {
        updatelocatio update = new updatelocatio();
        update.execute();
        //        updatelocation();

        spinner_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    province = province_name.get(position);
                    city_name.clear();
                    JSONObject data_province = data.getJSONObject(position);

                    city_array = data_province.getJSONArray("city");
                    for (int i = 0; i < city_array.length(); i++) {
                        JSONObject city = city_array.getJSONObject(i);
                        String cityname = city.getString("name");
                        city_name.add(cityname);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item,
                            city_name);
                    spinner_city.setAdapter(adapter);
                    if (!select_changed) {
                        spinner_city.setSelection(city_index);
                    }
                    Log.i("test", "right in spinner_province");
                    if (spinner_degree_edit_info.getSelectedItemPosition() == 1) {
                        mhandle.obtainMessage(MSE_LOCATION_CHANGED).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = city_name.get(position);
                county_name.clear();
                try {
                    JSONObject county = city_array.getJSONObject(position);
                    JSONArray county_array = county.getJSONArray("county");
                    for (int i = 0; i < county_array.length(); i++) {
                        String countyname = county_array.getString(i);
                        county_name.add(countyname);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout
                            .simple_spinner_item, county_name);
                    spinner_county.setAdapter(adapter);
                    if (!select_changed) {
                        spinner_county.setSelection(county_index);
                    }
                    Log.i("test", "right in spinner_city");
                    if (spinner_degree_edit_info.getSelectedItemPosition() == 1) {
                        mhandle.obtainMessage(MSE_LOCATION_CHANGED).sendToTarget();
                    }
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
                county = county_name.get(position);
                Toast.makeText(context, county_name.get(position), Toast.LENGTH_LONG).show();
                Log.i("test", "right in spinner_county");
                if (spinner_degree_edit_info.getSelectedItemPosition() == 1) {
                    mhandle.obtainMessage(MSE_LOCATION_CHANGED).sendToTarget();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //初始化身份spinner并添加监听
    public void initDegree() {
        spinner_degree_edit_info.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, degree[position], Toast.LENGTH_LONG).show();
                switch (position) {
                    case 0:
                        farm_name_print.setVisibility(View.VISIBLE);
                        farm_name_select.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        farm_name_print.setVisibility(View.INVISIBLE);
                        farm_name_select.setVisibility(View.VISIBLE);
                        mhandle.obtainMessage(MSE_DEGREE_CHANGED).sendToTarget();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    //添加按钮监听，进行上传文件操作
    public void initUpload() {
        save_edit_person_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner_degree_edit_info.getSelectedItemPosition() == 0) {
                    Log.i("Tag", String.valueOf(farm_name_print.getText()) + "," + et_name_edit_info.getText());
                    if ((!farm_name_print.getText().toString().equals("")) && (!et_name_edit_info.getText().toString
                            ().equals(""))) {
                        upload();
                        Intent intent=new Intent(context,MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "请输入完整信息", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //如果选择“农场管理人员”，要判断是否选择农场
                    upload();
                }

            }
        });
    }

    //解析省-地区-县 名json文件
    public class updatelocatio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            AssetManager assetManager = context.getAssets();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open("country"
                        + ".json"), "utf-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = new JSONObject(sb.toString());
                data = jsonObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject data_cities = data.getJSONObject(i);
                    String province = data_cities.getString("name");
                    Log.i("TAG", "prov:" + province);
                    province_name.add(province);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, province_name);
            spinner_province.setAdapter(adapter);
            spinner_province.setSelection(province_index, true);
        }
    }

    public void updatelocation() {

        Log.i("TAG", "update location");

        new Thread() {
            @Override
            public void run() {

                Log.i("TAG", "in thread");
                AssetManager assetManager = context.getAssets();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open
                            ("country.json"), "utf-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        Log.i("TAG", "line:" + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("TAG", "IOException");
                }

                try {
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    data = jsonObject.getJSONArray("data");
                    Log.i("TAG", "data:" + data);

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject data_cities = data.getJSONObject(i);
                        String province = data_cities.getString("name");
                        Log.i("TAG", "prov:" + province);
                        province_name.add(province);
                    }
                    mhandle.obtainMessage(MSE_PHARSE_SUCCESS).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    //上传数据
    public void upload() {
        //上传数据
        new Thread() {
            @Override
            public void run() {
                ShangdeApplication application = (ShangdeApplication) getApplication();
                String url = application.getURL();
                SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);

                OkHttpClient client = new OkHttpClient();
                if (Logedin) {
                    //已登录，更新用户信息，POST方法，使用接口：User/UpdateUser
                    FormBody formBody = new FormBody.Builder()
                            .add("User_Name", et_name_edit_info.getText().toString())
                            .add("mobile", PhoneNumber)
                            .add("role", String.valueOf(spinner_degree_edit_info.getSelectedItemPosition()))
                            .add("farm", String.valueOf(sharedPreferences.getInt("FarmID", -1)))
                            .add("icon", "")
                            .build();

                    Request request = new Request.Builder()
                            .header("Token",Token)
                            .url(url + "User/UpdateUser")
                            .post(formBody).build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String return_msg = response.body().string();
                            JSONObject msg = new JSONObject(return_msg);
                            boolean status = msg.getBoolean("status");
                            if (status) {
                                mhandle.obtainMessage(MSE_UPLOAD_SUCCESS).sendToTarget();
                            } else {
                                mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                            }
                        } else {
                            mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                        }
                    } catch (IOException e) {
                        mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                        e.printStackTrace();
                    }

                    if (spinner_degree_edit_info.getSelectedItemPosition() == 0) {
                        //已登录且为农场主，修改农场信息，POST方法，使用接口：Farm/UpdateFarm
                        JSONObject address = new JSONObject();
                        try {
                            address.put("prov", province);
                            address.put("city", city);
                            address.put("coun", county);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        OkHttpClient client1 = new OkHttpClient();
                        FormBody formBody1 = new FormBody.Builder().add("id", String.valueOf(sharedPreferences.getInt
                                ("FarmID", -1))).add("User_Name", farm_name_print.getText().toString()).add
                                ("address", address.toString()).build();
                        Request request1 = new Request.Builder()
                                .header("Token",Token)
                                .url(url + "Farm/UpdateFarm")
                                .post(formBody1)
                                .build();
                        try {
                            Response response1 = client.newCall(request1).execute();
                            if (response1.isSuccessful()) {
                                String return_msg = response1.body().string();
                                JSONObject msg = new JSONObject(return_msg);
                                boolean status = msg.getBoolean("status");
                                if (status) {
                                    mhandle.obtainMessage(MSE_UPLOAD_SUCCESS).sendToTarget();
                                } else {
                                    mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                                }
                            } else {
                                mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                            }
                        } catch (IOException e) {
                            mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                            e.printStackTrace();
                        }
                    }else {
                        //已登录，身份为管理员,判断是否更换了农场
                        int position=farm_name_select.getSelectedItemPosition();
                        int farmid=nearbyFarms.get(position).FarmNearby_ID;
                        if(farmid==FarmID){
                            //没有更换农场，则更新用户信息
                            UpdateUser(Token,User_ID,et_name_edit_info.getText().toString(),tv_phone_edit_info.getText().toString(),
                                    1,farmid);
                            mhandle.obtainMessage(MSE_SUCCESS).sendToTarget();
                        }else {
                            //更换了农场，请求进入，修改user
                            String description=User_Name+"("+PhoneNumber+") 请求加入农场";
                            CreateTask(User_ID,farmid,0,description);
                        }
                    }


                } else {
                    //未登录，创建用户，POST方法，使用接口：User/Login
                    OkHttpClient client_Login=new OkHttpClient();
                    FormBody formBody_Login=new FormBody.Builder()
                            .add("mobile",tv_phone_edit_info.getText().toString())
                            .add("role", String.valueOf(spinner_degree_edit_info.getSelectedItemPosition()))
                            .build();
                    Request request_Login=new Request.Builder()
                            .post(formBody_Login)
                            .url(url+"User/Login")
                            .build();
                    Log.i("TAG",url+"User/Login");
                    try {
                        Response response_Login=client_Login.newCall(request_Login).execute();
                        if (response_Login.isSuccessful()){
                            Headers headers=response_Login.headers();
                            String Token=headers.get("Token");
                            Log.i("TAG",Token);

                            JSONObject jsonObject=new JSONObject(response_Login.body().string());
                            boolean status=jsonObject.getBoolean("status");
                            Log.i("TAG","status:"+status);
                            if (status){
                                JSONObject data=jsonObject.getJSONObject("data");
                                int id_changed=data.getInt("id");
                                Log.i("TAG","id_changed:"+id_changed);
                                String name_changed=data.getString("name");
                                Log.i("TAG","name_changed:"+name_changed);
                                String moblie_changed=data.getString("mobile");
                                String pwd_changed=data.getString("im_pwd");
                                int role_changed=data.getInt("role");
                                int farm_changed=data.getInt("farm");
                                String icon_changed=data.getString("icon");

                                saveUserLocally(Token,id_changed,name_changed,moblie_changed,pwd_changed,role_changed,farm_changed,icon_changed);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (spinner_degree_edit_info.getSelectedItemPosition() == 0) {
                        //未登录且为农场主，新增农场，POST方法，使用接口：Farm/CreatFarm
                        String upload_farm_name=farm_name_print.getText().toString();
                        String upload_farm_address="{"+province+","+city+","+county+"}";
                        Log.i("TAG","upload_farm_address:"+upload_farm_address);
                        CreateFarm(upload_farm_name,upload_farm_address);

                        int id=sharedPreferences.getInt("User_ID",-1);
                        String name=sharedPreferences.getString("User_Name","");
                        String mobile=sharedPreferences.getString("PhoneNumber","");
                        int farm_id=sharedPreferences.getInt("FarmID",-1);
                        UpdateUser(sharedPreferences.getString("Token",""),id,name,mobile,0,farm_id);
                    } else {
                        //未登录为管理人员，给农场主发出请求，POST方法，使用接口：ask/CreateTask
                        int position=farm_name_select.getSelectedItemPosition();
                        FarmNearby nearby=nearbyFarms.get(position);
                        CreateTask(sharedPreferences.getInt("User_ID",0),nearby.FarmNearby_ID,0,"description");
                    }

                    SharedPreferences.Editor speditor=sharedPreferences.edit();
                    speditor.putBoolean("Logedin",true);
                    speditor.commit();
                }

            }
        }.start();
    }

    //将数据保存到SharedPreferences
    public void saveSP() {
        SharedPreferences.Editor sp_editor = sp.edit();
        sp_editor.putString("Name", String.valueOf(et_name_edit_info.getText()));
        sp_editor.putInt("DegreeIndex", spinner_degree_edit_info.getSelectedItemPosition());
        sp_editor.putInt("ProvinceIndex", spinner_province.getSelectedItemPosition());
        sp_editor.putInt("CityIndex", spinner_city.getSelectedItemPosition());
        sp_editor.putInt("CountyIndex", spinner_county.getSelectedItemPosition());
        sp_editor.putString("Province", province);
        sp_editor.putString("City", city);
        sp_editor.putString("County", county);
        sp_editor.putString("FarmName", String.valueOf(farm_name_print.getText()));
        sp_editor.commit();
        Log.i("Tag", province + "," + city + "," + county);

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //搜索附近的农场，所用接口：api/Farm/GetFarms?address={address}
    public void SearchFarms() {
        farm_name_select.setAdapter(null);
        new Thread() {
            @Override
            public void run() {
                ShangdeApplication application = (ShangdeApplication) getApplication();
                String url = application.getURL();
                SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
                String token=sp.getString("Token","");

                //获取附近农场，GET方法，所用接口：api/Farm/GetFarms?address={address}

                String s = "Farm/GetFarms?address=" + "{"+province+","+city+","+county+"}";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .header("Token",token)
                        .url(url + s)
                        .build();
                Log.i("TAG", url + s);
                Log.i("TAG", "token:"+token);
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONObject msg = new JSONObject(response.body().string());
                        //判断返回的值
                        boolean status = msg.getBoolean("status");
                        if (status) {
                            nearbyFarms.clear();
                            farm_name.clear();
                            //一切正常时的返回值
                            JSONArray data_array = msg.getJSONArray("data");
                            Log.i("TAG",data_array.toString());
                            //解析文件
                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject data_single = data_array.getJSONObject(i);
                                int id = data_single.getInt("id");
                                String name = data_single.getString("name");
                                FarmNearby farmNearby = new FarmNearby(id, name);

                                nearbyFarms.add(farmNearby);
                                farm_name.add(name);
                            }
                            mhandle.obtainMessage(MSE_SUCCESS).sendToTarget();
                        } else {
                            //返回状态错误时
                            Log.i("TAG", "status false");
                            mhandle.obtainMessage(MSE_FAILED).sendToTarget();
                        }
                    } else {
                        //连接错误时
                        Log.i("TAG", "connect failed");
                        mhandle.obtainMessage(MSE_FAILED).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.i("TAG", "IOException");
                    mhandle.obtainMessage(MSE_FAILED).sendToTarget();
                } catch (JSONException e) {
                    Log.i("TAG", "JSONException");
                    mhandle.obtainMessage(MSE_FAILED).sendToTarget();
                }
            }
        }.start();
    }

    //存储附近的农场的信息
    public class FarmNearby {
        public int FarmNearby_ID;
        public String FarmNearby_Name;

        public FarmNearby(int farmNearby_ID, String farmNearby_Name) {
            FarmNearby_ID = farmNearby_ID;
            FarmNearby_Name = farmNearby_Name;
        }
    }

    //显示附近农场信息
    public void ShowNearbyFarm() {
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, farm_name);
        farm_name_select.setAdapter(adapter);
        farm_name_select.setVisibility(View.VISIBLE);
//        int postion = 0;
//        for (; postion < farm_name.size(); postion++) {
//            if (FarmName.equals(farm_name)) {
//                farm_name_select.setSelection(postion);
//                break;
//            }
//        }
    }

    //更新用户信息，POST方法,所用接口：User/UpdateUser
    public void UpdateUser(String token,int id,String name,String mobile,int role,int farm){
        OkHttpClient client_UpdateUser=new OkHttpClient();

        FormBody formBody_UpdateUser = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("name",name)
                .add("mobile", mobile)
                .add("role", String.valueOf(role))
                .add("farm", String.valueOf(farm))
                .build();

        ShangdeApplication application= (ShangdeApplication) getApplication();
        String url=application.getURL();

        Request request = new Request.Builder()
                .header("Token",token)
                .url(url + "User/UpdateUser")
                .post(formBody_UpdateUser)
                .build();

        try {
            Response response=client_UpdateUser.newCall(request).execute();
            if (response.isSuccessful()){
                JSONObject jsonObject=new JSONObject(response.body().string());
                boolean status=jsonObject.getBoolean("status");
                if (status){
                    JSONObject data=jsonObject.getJSONObject("data");
                    int id_changed=data.getInt("id");
                    String name_changed=data.getString("name");
                    String moblie_changed=data.getString("mobile");
                    String pwd_changed=data.getString("im_pwd");
                    int role_changed=data.getInt("role");
                    int farm_changed=data.getInt("farm");
                    String icon_changed=data.getString("icon");

                    saveUserLocally(token,id_changed,name_changed,moblie_changed,pwd_changed,role_changed,farm_changed,icon_changed);
                }else {
                    mhandle.obtainMessage(MSE_UPLOAD_FAILED).sendToTarget();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //将数据保存在本地，更新application
    public void saveUserLocally(String token,int id,String name,String mobile,String paw,int role,int farm,String icon){
        SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor sp_editor=sp.edit();
        ShangdeApplication application= (ShangdeApplication) getApplication();

        sp_editor.putString("Token",token);

        sp_editor.putInt("User_ID", id);
        sp_editor.putString("User_Name", name);
        sp_editor.putString("PhoneNumber", mobile);
        sp_editor.putString("Password", paw);
        sp_editor.putInt("Role", role);
        sp_editor.putInt("Farm", farm);
        sp_editor.putString("Icon", icon);

        sp_editor.commit();

        User user=new User(id,name,mobile,paw,role,farm,icon);
        application.setUser(user);
    }

    //创建农场，POST方法，所用接口：Farm/CreatFarm
    public void CreateFarm(String name,String address){
        Log.i("TAG","create farm");
        ShangdeApplication application= (ShangdeApplication) getApplication();
        String url=application.getURL();
        SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor sp_editor=sp.edit();

        OkHttpClient client_CreateFarm=new OkHttpClient();
        FormBody formBody_CreateFarm=new FormBody.Builder()
                .add("name",name)
                .add("address",address)
                .build();
        Request request_CreateFarm=new Request.Builder()
                .header("Token",sp.getString("Token",""))
                .url(url+"Farm/CreatFarm")
                .post(formBody_CreateFarm)
                .build();
        Log.i("TAG","url:"+url+"Farm/CreatFarm");
        try {
            Response response_CreateFarm=client_CreateFarm.newCall(request_CreateFarm).execute();
            if (response_CreateFarm.isSuccessful()){
                Log.i("TAG","response_CreateFarm:"+"suceess");
                String msg=response_CreateFarm.body().string();
                JSONObject jsonObject=new JSONObject(msg);
                boolean status=jsonObject.getBoolean("status");
                if (status){
                    Log.i("TAG","status:"+"true");
                    JSONObject data=jsonObject.getJSONObject("data");
                    int id=data.getInt("id");
                    String farm_name=data.getString("name");
                    String farm_adress=data.getString("address");
                    Log.i("TAG","farm_adress:"+farm_adress);

                    com.xz.shangde.Farm farm=new Farm(id,farm_name,province,city,county,province_index,city_index,county_index);
                    application.setFarm(farm);

                    sp_editor.putInt("FarmID",id);
                    sp_editor.putString("FarmName",farm_name);
                    sp_editor.putString("Province",province);
                    sp_editor.putString("City",city);
                    sp_editor.putString("County",county);
                    sp_editor.putInt("Province_Index",province_index);
                    sp_editor.putInt("City_Index",city_index);
                    sp_editor.putInt("County_Index",county_index);
                    sp_editor.commit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //创建申请加入农场任务，POST方法，所用接口：Task/CreateTask
    public void CreateTask(int creator_id,int farm_id,int type,String description){
        ShangdeApplication application= (ShangdeApplication) getApplication();
        String url=application.getURL();

        OkHttpClient client_CreateTask=new OkHttpClient();
        FormBody formBody_CreateTask=new FormBody.Builder()
                .add("creator", String.valueOf(creator_id))
                .add("farm", String.valueOf(farm_id))
                .add("type", String.valueOf(type))
                .add("description",description)
                .build();
        Request request_CreateTask=new Request.Builder()
                .url(url+"CreatTask")
                .post(formBody_CreateTask)
                .build();
        try {
            Response response_CreateFarm=client_CreateTask.newCall(request_CreateTask).execute();
            if (response_CreateFarm.isSuccessful()){
                String msg=response_CreateFarm.body().string();
                JSONObject jsonObject=new JSONObject(msg);
                boolean status=jsonObject.getBoolean("status");
                if (status){
                    mhandle.obtainMessage(MSE_CREATED_TASK_SUCCESS).sendToTarget();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
