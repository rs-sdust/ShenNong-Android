package com.xz.shangde.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.xz.shangde.CropType;
import com.xz.shangde.Field;
import com.xz.shangde.Field_live;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yxq on 2018/5/8.
 */

public class UploadPhotoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView iv_photo_1;
    private ImageView iv_photo_2;
    private ImageView iv_photo_3;
    private RadioGroup rg_plant_states;
    private RadioGroup rg_soil_moisture;
    private Button btn_upload_photo;

    private RadioButton rb_plant_state_good;
    private RadioButton rb_plant_state_mid;
    private RadioButton rb_plant_state_bad;
    private RadioButton rb_soil_moisture_good;
    private RadioButton rb_soil_moisture_mid;
    private RadioButton rb_soil_moisture_bad;

    private Spinner spinner_choose_field;
    private Spinner spinner_diease;
    private Spinner spinner_pest;
    //记录选中的数据,从上到下顺序储存
    //todo 注意此处，bitmap太大会引起oom，bitmaps_compression是已经压缩过了
    private String[] data = null;
    private int[] grade=null;
    private ArrayList<Bitmap> bitmaps_compression=new ArrayList<>();

    private ArrayList<Field> fields;
    private String[] fields_name;
    private int[] fields_id;
    private GeoPoint location_now;

    private LinkedHashMap<Integer,String> disease_map=new LinkedHashMap<>();
    private ArrayList<String> disease_name=new ArrayList<>();
    private LinkedHashMap<Integer,String> pest_map=new LinkedHashMap<>();
    private ArrayList<String> pest_name=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        Bitmap bitmap=bundle.getParcelable("bitmap");
        location_now=bundle.getParcelable("location_now");

        ShangdeApplication application= (ShangdeApplication) getApplication();
        fields= application.getFields();
        disease_map=application.getDisease_map();
        pest_map=application.getPest_map();

        initView();

        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        bitmaps_compression.add(bm);

        iv_photo_1.setImageBitmap(bm);
        if (!bitmap.isRecycled()){
            bitmap.recycle();
        }

    }

    public void initView() {
        toolbar = findViewById(R.id.tb_upload_photo);
        iv_photo_1 = findViewById(R.id.iv_photo_1);
        iv_photo_2 = findViewById(R.id.iv_photo_2);
        iv_photo_3 = findViewById(R.id.iv_photo_3);
        rg_plant_states = findViewById(R.id.rg_plant_states);
        rg_soil_moisture = findViewById(R.id.rg_soil_moisture);
        btn_upload_photo = findViewById(R.id.btn_upload_photo);

        rb_plant_state_good = findViewById(R.id.rb_plant_state_good);
        rb_plant_state_mid = findViewById(R.id.rb_plant_state_mid);
        rb_plant_state_bad = findViewById(R.id.rb_plant_state_bad);
        rb_soil_moisture_good = findViewById(R.id.rb_soil_moisture_good);
        rb_soil_moisture_mid = findViewById(R.id.rb_soil_moisture_mid);
        rb_soil_moisture_bad = findViewById(R.id.rb_soil_moisture_bad);

        spinner_choose_field=findViewById(R.id.spinner_choose_field);
        spinner_diease=findViewById(R.id.spinner_disease);
        spinner_pest=findViewById(R.id.spinner_pest);

        data = new String[2];
        data[0] = "好";
        data[1] = "湿润";

        grade = new int[2];
        grade[0] = 1;
        grade[1] = 3;

        initToolbar();
        initRadioButton();
        initButton();
        initImageView();
        initSpinner();
    }

    public void initToolbar() {
        setSupportActionBar(toolbar);
        //设置回退按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //不显示toolbar的title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initRadioButton() {

        rg_plant_states.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_plant_state_good:
                        rb_plant_state_good.setBackgroundResource(R.drawable.button_login_shape);
                        rb_plant_state_mid.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        rb_plant_state_bad.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        data[0] = "好";
                        grade[0]=1;
                        break;
                    case R.id.rb_plant_state_mid:
                        rb_plant_state_mid.setBackgroundResource(R.drawable.button_login_shape);
                        rb_plant_state_good.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        rb_plant_state_bad.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        data[0] = "中";
                        grade[0]=2;
                        break;
                    case R.id.rb_plant_state_bad:
                        rb_plant_state_bad.setBackgroundResource(R.drawable.button_login_shape);
                        rb_plant_state_good.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        rb_plant_state_mid.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        data[0] = "差";
                        grade[0]=3;
                        break;
                }
            }
        });

        rg_soil_moisture.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_soil_moisture_good:
                        rb_soil_moisture_good.setBackgroundResource(R.drawable.button_login_shape);
                        rb_soil_moisture_mid.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        rb_soil_moisture_bad.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        data[1] = "湿润";
                        grade[1]=3;
                        break;
                    case R.id.rb_soil_moisture_mid:
                        rb_soil_moisture_mid.setBackgroundResource(R.drawable.button_login_shape);
                        rb_soil_moisture_good.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        rb_soil_moisture_bad.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        data[1] = "适中";
                        grade[1]=2;
                        break;
                    case R.id.rb_soil_moisture_bad:
                        rb_soil_moisture_bad.setBackgroundResource(R.drawable.button_login_shape);
                        rb_soil_moisture_mid.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        rb_soil_moisture_good.setBackgroundResource(R.drawable
                                .radiobutton_unchecked_shape);
                        data[1] = "干旱";
                        grade[1]=1;
                        break;
                }
            }
        });

    }

    public void initButton() {
        btn_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG","onclick");
                final int ID=fields_id[spinner_choose_field.getSelectedItemPosition()];
                Log.i("TAG","ID"+ID);
                final String gps="POINT("+location_now.getLatitude()+" "+location_now.getLongitude()+")";
                Log.i("TAG","gps"+gps);

                new Thread(){
                    @Override
                    public void run() {
                        Log.i("TAG","in thread");
                        //将图片保存至本地
                        String dir= Environment.getExternalStorageDirectory()+ "/Shangde/Pictures/";
                        String[] filenames=new String[bitmaps_compression.size()];
                        Log.i("TAG","length:"+filenames.length);
                        for (int i=0;i<bitmaps_compression.size();i++){
                            File file = new File(dir, System.currentTimeMillis()+".jpg");
                            Log.i("TAG","step1 correct");
                            FileOutputStream out = null;
                            Log.i("TAG","step2 correct");
                            try {
                                out = new FileOutputStream(file);
                                Log.i("TAG","step3 correct");
                                bitmaps_compression.get(i).compress(Bitmap.CompressFormat.JPEG, 90, out);
                                Log.i("TAG","step4 correct");
                                out.flush();
                                Log.i("TAG","step5 correct");
                                out.close();
                                Log.i("TAG","step6 correct");
                                filenames[i]=file.getAbsolutePath();
                                Log.i("TAG","step7 correct");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("TAG","saved:"+filenames[0]);

                        //首先上传图片，并得到图片地址
                        uploadPhoto(filenames,ID,gps);


                    }
                }.start();

                finish();
            }
        });
    }

    public void initImageView() {
        iv_photo_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, 0);
            }
        });
        iv_photo_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, 1);
            }
        });
        iv_photo_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, 2);
            }
        });
    }

    public void initSpinner(){
        //todo 初始化地块数据
        ParseFieldsData();
        ArrayAdapter<String> adapter_fields=
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,fields_name);
        spinner_choose_field.setAdapter(adapter_fields);

        Iterator entries1 = disease_map.entrySet().iterator();
        while (entries1.hasNext()) {
            Map.Entry entry = (Map.Entry) entries1.next();
            String value = (String) entry.getValue();
            disease_name.add(value);
        }
        ArrayAdapter<String> adapter_disease=
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,disease_name);
        spinner_diease.setAdapter(adapter_disease);

        Iterator entries2 = pest_map.entrySet().iterator();
        while (entries2.hasNext()) {
            Map.Entry entry = (Map.Entry) entries2.next();
            String value = (String) entry.getValue();
            pest_name.add(value);
        }
        ArrayAdapter<String> adapter_pest=
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,pest_name);
        spinner_pest.setAdapter(adapter_pest);
    }

    //解析地块列表
    public void ParseFieldsData(){
        fields_name=new String[fields.size()];
        fields_id=new int[fields.size()];
        for (int i=0;i<fields.size();i++){
            fields_name[i]=fields.get(i).getField_Name();
            fields_id[i]=fields.get(i).getField_ID();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Tag", "requestCode: " + requestCode);
        Log.i("Tag", "resultCode: " + resultCode);
        Log.i("Tag", "Activity.RESULT_OK: " + Activity.RESULT_OK);

        switch (requestCode) {
            case 0:

                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    Log.i("Tag", "fragment onActivityResult used");
                    Log.i("Tag", "bitmap size：" + bitmap.getByteCount());

                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    Log.i("Tag", "bm size：" + bm.getByteCount());

                    if (!bitmap.isRecycled()){
                        Log.i("OOM","bitmap hasn't recycled");
                        bitmap.recycle();
                        Log.i("OOM","bitmap is recycled");
                        Log.i("OOM","bitmap size"+bitmap.getByteCount());
                    }

                    iv_photo_1.setImageBitmap(bm);
                    bitmaps_compression.set(0,bm);
                }

                break;

            case 1:

                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    Log.i("Tag", "fragment onActivityResult used");
                    Log.i("Tag", "bitmap size：" + bitmap.getByteCount());

                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    Log.i("Tag", "bm size：" + bm.getByteCount());

                    if (!bitmap.isRecycled()){
                        Log.i("OOM","bitmap hasn't recycled");
                        bitmap.recycle();
                        Log.i("OOM","bitmap is recycled");
                        Log.i("OOM","bitmap size"+bitmap.getByteCount());
                    }

                    iv_photo_2.setImageBitmap(bm);
                    if (bitmaps_compression.size()==1){
                        bitmaps_compression.add(bm);
                    }
                    else if (bitmaps_compression.size()==2){
                        bitmaps_compression.set(1,bm);
                    }
                    else if (bitmaps_compression.size()==3){
                        bitmaps_compression.set(1,bm);
                    }
                }

                break;

            case 2:

                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    Log.i("Tag", "fragment onActivityResult used");
                    Log.i("Tag", "bitmap size：" + bitmap.getByteCount());

                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    Log.i("Tag", "bm size：" + bm.getByteCount());

                    if (!bitmap.isRecycled()){
                        Log.i("OOM","bitmap hasn't recycled");
                        bitmap.recycle();
                        Log.i("OOM","bitmap is recycled");
                        Log.i("OOM","bitmap size"+bitmap.getByteCount());
                    }

                    iv_photo_3.setImageBitmap(bm);
                    if (bitmaps_compression.size()==1){
                        bitmaps_compression.add(null);
                        bitmaps_compression.add(bm);
                    }
                    else if (bitmaps_compression.size()==2){
                        bitmaps_compression.add(bm);
                    }
                    else if (bitmaps_compression.size()==3){
                        bitmaps_compression.set(2,bm);
                    }
                }

                break;
        }
    }

    //todo 上传图片到云空间
    public ArrayList<String> uploadPhoto(String[] picture_path, final int ID, final String gps){
        ShangdeApplication application= (ShangdeApplication) getApplication();
        String url=application.getURL();
        SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
        String token=sp.getString("Token","");

        //保存上传完成后文件的相对路径
        final ArrayList<String> path=new ArrayList<>();
        OkHttpClient mOkHttpClent = new OkHttpClient();

        Log.i("TAG","upload photo");
        for (int i=0;i<picture_path.length;i++){
            Log.i("TAG", String.valueOf(picture_path.length));
            Log.i("TAG",picture_path[i]);
            File file=new File(picture_path[i]);
            Log.i("TAG",file.getAbsolutePath());
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("img", "HeadPortrait.jpg",
                            RequestBody.create(MediaType.parse("image/png"), file));

            RequestBody requestBody = builder.build();


            Request request = new Request.Builder()
                    .header("Token",token)
                    .url(url+"UploadPicture/PostUpload")
                    .post(requestBody)
                    .build();
            Call call = mOkHttpClent.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("TAG", "onFailure: "+e );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("TAG",response.toString());
                    if (response.isSuccessful()){
                        String s=response.body().string();
                        Log.i("TAG",s);
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            boolean status=jsonObject.getBoolean("status");
                            Log.i("TAG","status:ture");
                            if (status){
                                JSONArray data=jsonObject.getJSONArray("data");
                                for (int j=0;j<data.length();j++){
                                    JSONObject picture=data.getJSONObject(j);
                                    Log.i("TAG","picture:"+picture.toString());
                                    String path_single=picture.getString("path");
                                    Log.i("TAG","path_single:"+path_single);
                                    path.add(path_single);
                                    Log.i("TAG","add to path right");
                                }
                            }
                            response.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //然后将图片地址转换成json格式
                        Log.i("TAG","transform");
                        String pictures_path="";
                        JSONArray jsonArray=new JSONArray();
                        Log.i("TAG","size:"+path.size());
                        for (int j=0;j<path.size();j++){
                            JSONObject jsonObject=new JSONObject();
                            try {
                                jsonObject.put("path", path.get(j));
                                Log.i("TAG",jsonObject.getString("path"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsonArray.put(jsonObject);
                        }
                        Log.i("TAG","right");
                        pictures_path=jsonArray.toString();
                        Log.i("TAG",pictures_path);

                        //最后上传所有信息
                        upload2database(ID,gps,pictures_path);
                    }
                }
            });
        }

        return path;
    }

    //上传到数据库，POST方法，所用接口：Farmwork/AddFieldLive
    public void upload2database(int fieldid,String gps,String picture){
        ShangdeApplication application = (ShangdeApplication) getApplication();
        String url = application.getURL();
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");
        int userid=sharedPreferences.getInt("User_ID",0);

        int postion_disease=spinner_diease.getSelectedItemPosition();
        String diseasename=disease_name.get(postion_disease);
        int diseaseid=0;
        for(int getKey: disease_map.keySet()){
            if(disease_map.get(getKey).equals(diseasename)){
                diseaseid = getKey;
                }
        }
        int postion_pest=spinner_pest.getSelectedItemPosition();
        String pestname=pest_name.get(postion_pest);
        int pestid=0;
        for(int getKey: pest_map.keySet()){
            if(pest_map.get(getKey).equals(pestname)){
                pestid = getKey;
            }
        }

        Log.i("TAG","field:"+fieldid);
        Log.i("TAG","growth:"+grade[0]);
        Log.i("TAG","moisture"+grade[1]);
        Log.i("TAG","disease:"+diseaseid);
        Log.i("TAG","pest:"+pestid);
        Log.i("TAG","collector："+userid);
        Log.i("TAG","gps:"+gps);
        Log.i("TAG","picture:"+picture);

        OkHttpClient client_POSTFieldLive=new OkHttpClient();
        FormBody formBody_POSTFieldLive=new FormBody.Builder()
                .add("field", String.valueOf(fieldid))
                .add("growth", String.valueOf(grade[0]))
                .add("moisture", String.valueOf(grade[1]))
                .add("disease", String.valueOf(diseaseid))
                .add("pest", String.valueOf(pestid))
                .add("collector", String.valueOf(userid))
                .add("gps",gps)
                .add("picture",picture)
                .build();
        Request request_POSTFieldLive=new Request.Builder()
                .header("Token",token)
                .url(url+"Farmwork/AddFieldLive")
                .post(formBody_POSTFieldLive)
                .build();
        try {
            Response response_POSTFieldLive=client_POSTFieldLive.newCall(request_POSTFieldLive)
                    .execute();
            Log.i("TAG",response_POSTFieldLive.message());
            if (response_POSTFieldLive.isSuccessful()){
                String s=response_POSTFieldLive.body().string();
                Log.i("TAG",s);
                JSONObject jsonObject=new JSONObject(s);
                boolean status=jsonObject.getBoolean("status");
                if (status){
                    Log.i("TAG","POSTFieldLive sucess");
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
