package com.xz.shangde.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xz.shangde.CropType;
import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zxz
 * 自动分组（根据地块的种植作物）显示地块的adapter
 * 可以进入每一个地块的详细信息进行查看，也可以批量修改一组的信息
 */

public class ExpListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Field> fields;
    private LinkedHashMap<Integer,String> crop_type;
    private ArrayList<String> croptype_name=new ArrayList<>();

    ArrayList<Integer> field_parent=new ArrayList<>();
    Map<Integer,ArrayList<Field>> fields_child=new HashMap<>();

    public ExpListViewAdapter(Context context, ArrayList<Field> fields,LinkedHashMap<Integer,String> map) {
        this.context = context;
        this.fields = fields;
        this.crop_type=map;

        parseFields();
    }

    //解析地块的list，填充至field_parent和fields_child中
    public void parseFields(){
        List<Integer> listWithDuplicateElements=new ArrayList<>();
        for (int i=0;i<fields.size();i++){
            listWithDuplicateElements.add(fields.get(i).getField_Crop());
        }
        LinkedHashSet<Integer> set=new LinkedHashSet<Integer>(listWithDuplicateElements);
        field_parent=new ArrayList<Integer>(set);

        for (int i=0;i<field_parent.size();i++){

            int spices=field_parent.get(i);
            ArrayList<Field> childs=new ArrayList<>();

            for (int j=0;j<fields.size();j++){
                if (field_parent.get(i).equals(fields.get(j).getField_Crop())){
                    childs.add(fields.get(j));
                }
            }

            fields_child.put(spices,childs);
        }

        Iterator entries1 = crop_type.entrySet().iterator();
        while (entries1.hasNext()) {
            Map.Entry entry = (Map.Entry) entries1.next();
            String value = (String) entry.getValue();
            croptype_name.add(value);
        }
    }

    //获取某group某child的position
    public int findPosition(int groupPosition,int childPosition){
        int position=0;
        int count=0;
        int crop_type=field_parent.get(groupPosition);
        for (position=0;position<fields.size();position++){
            Field field=fields.get(position);
            if (field.getField_Crop()==crop_type){
                if (count==childPosition){
                    break;
                }
                count++;
            }
        }
        return position;
    }

    @Override
    public int getGroupCount() {
        return field_parent.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int crop=field_parent.get(groupPosition);
        int childrencount=fields_child.get(crop).size();
        return childrencount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return field_parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int crop=field_parent.get(groupPosition);
        ArrayList<Field> chidrenfields=fields_child.get(crop);
        return chidrenfields;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {

        Log.i("TAG","Group Position:"+groupPosition);
        if (convertView==null){
            convertView=View.inflate(context, R.layout.fields_parent_view,null);
        }
        TextView tv_fields_crop=convertView.findViewById(R.id.tv_fields_crop);
        int id=field_parent.get(groupPosition);
        tv_fields_crop.setText(crop_type.get(id));

        ImageView iv_change_fields_crop=convertView.findViewById(R.id.iv_change_fields_crop);
        iv_change_fields_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int crop_before_change=field_parent.get(groupPosition);
                //批量修改地块操作，修改这一组所有的地块
                final ModifyDialog dialog = new ModifyDialog(context, "批量修改地块属性", null);
                final Spinner spinner=dialog.getSpinner_modify_crop_type();
                final DatePicker datePicker=dialog.getDatepicker_modify();
                dialog.setOnClickCommitListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取修改后的作物id
                        int postion_crop=spinner.getSelectedItemPosition();
                        String cropname=croptype_name.get(postion_crop);
                        int crop_id=0;
                        for(int getKey: crop_type.keySet()){
                            if(crop_type.get(getKey).equals(cropname)){
                                crop_id = getKey;
                            }
                        }
                        //获取修改后的播种时间
                        String date="";
                        int y=datePicker.getYear();
                        date+=y+"-";
                        int m=datePicker.getMonth()+1;
                        if (m<10){
                            String month="0"+m;
                            date+=month;
                        }else {
                            String month= String.valueOf(m);
                            date+=month;
                        }
                        int d=datePicker.getDayOfMonth();
                        if (d<10){
                            String day="0"+d;
                            date=date+"-"+day;
                        }else {
                            String day= String.valueOf(d);
                            date=date+"-"+day;
                        }


                        for (int i=0;i<fields.size();i++){
                            Field field=fields.get(i);
                            if (field.getField_Crop()==crop_before_change){
                                fields.get(i).setField_Crop(crop_id);
                                fields.get(i).setSowdate(date);
                            }
                        }

                        //逐个更新地块信息，POST方法，所用接口：Fields/BatchField
                        final int finalCrop_id = crop_id;
                        final String finalDate = date;
                        new Thread(){
                            @Override
                            public void run() {
                                ShangdeApplication application = (ShangdeApplication) context.getApplicationContext();
                                String url = application.getURL();
                                SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                                String token = sharedPreferences.getString("Token", "");

                                ArrayList<Field> fields=fields_child.get(crop_before_change);
                                for (int i=0;i<fields.size();i++){
                                    Field field=fields.get(i);
                                    OkHttpClient client_update_one=application.getClient();
                                    FormBody formBody_update_one=new FormBody.Builder()
                                            .add("id", String.valueOf(field.getField_ID()))
                                            .add("currentcrop", String.valueOf(finalCrop_id))
                                            .add("sowdate", finalDate)
                                            .add("farm", String.valueOf(field.getField_Farm_Belong()))
                                            .build();
                                    Request request_update_one=new Request.Builder()
                                            .header("Token",token)
                                            .url(url+"Fields/BatchField")
                                            .post(formBody_update_one)
                                            .build();
                                    try {
                                        Response response_update_one=client_update_one.newCall(request_update_one)
                                                    .execute();
                                        if (response_update_one.isSuccessful()){
                                            JSONObject jsonObject=new JSONObject(response_update_one.body().string());
                                            boolean status=jsonObject.getBoolean("status");
                                            if (status){
                                                //正确的修改了一个地块
                                                return;
                                            }
                                        }
                                        response_update_one.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();

                        parseFields();
                        notifyDataSetChanged();
                        Toast.makeText(context,"Commit",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {

        Log.i("TAG","Group Position:"+groupPosition+",Child Postion:"+childPosition);

        if (convertView==null){
            convertView=View.inflate(context,R.layout.fields_chidren_view,null);
        }

        MapView iv_image_field=convertView.findViewById(R.id.iv_image_field);
        TextView tv_field_name=convertView.findViewById(R.id.tv_field_name);
        TextView tv_field_area=convertView.findViewById(R.id.tv_field_area);
        TextView tv_field_crop_name=convertView.findViewById(R.id.tv_field_crop_name);

        int cropid=field_parent.get(groupPosition);
        ArrayList<Field> fields=fields_child.get(cropid);
        Field field=fields.get(childPosition);

//        wv_image_field.loadUrl(field.getField_Thumb());
        tv_field_name.setText(field.getField_Name());
        int mu=(int) (field.getField_Area()*9/6000);
        int fen=(int) ((field.getField_Area()*9/6000-mu)*10);
        tv_field_area.setText(mu+"亩"+fen+"分");
        int id=field.getField_Crop();
        tv_field_crop_name.setText(crop_type.get(id));

        iv_image_field.setTileSource(GoogleHybrid);
        iv_image_field.setMultiTouchControls(true);
        iv_image_field.setBuiltInZoomControls(false);
        ArrayList<GeoPoint> points=field.getField_Boundary();
        BoundingBox boundingBox=BoundingBox.fromGeoPoints(points);
        IMapController mapController = iv_image_field.getController();
        mapController.setZoom(17);
        GeoPoint centerPoint = boundingBox.getCenterWithDateLine();
        mapController.setCenter(centerPoint);

        Polygon polygon = new Polygon();
        polygon.setPoints(field.getField_Boundary());
        iv_image_field.getOverlayManager().add(polygon);
        iv_image_field.invalidate();

        //test Glide
//        String url = "http://img1.dzwww.com:8080/tupian_pl/20150813/16/7858995348613407436.jpg";
//        Glide.with(context)
//                .load(url)
//                .into(iv_image_field);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static final OnlineTileSourceBase GoogleHybrid = new XYTileSource("Google-Hybrid",
            0, 19, 256, ".png", new String[]{
            "http://mt0.google.cn",
            "http://mt1.google.cn",
            "http://mt2.google.cn",
            "http://mt3.google.cn",

    }) {
        @Override
        public String getTileURLString(long pTileIndex) {
            return getBaseUrl() + "/vt/lyrs=y&x=" + MapTileIndex.getX(pTileIndex) + "&y=" +
                    MapTileIndex.getY(pTileIndex) + "&z=" + MapTileIndex.getZoom(pTileIndex);
        }
    };
}
