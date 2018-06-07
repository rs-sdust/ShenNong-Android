package com.xz.shangde;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yxq on 2018/5/14.
 */

public class Field implements Serializable {

    private int Field_ID;
    private int Field_Farm_Belong;
    private String Field_Name;
    private ArrayList<GeoPoint> Field_Boundary;
    private float Field_Area;
    private int Field_Crop;
    private String Createdate;
    private String Sowdate;
    private int Field_Phenophase;
    private String Field_Thumb;

    private GeoPoint CenterPoint;

    //从数据库中下载数据用此方法，此时ID已知
    public Field(int field_ID, int field_Farm_Belong, String field_Name, ArrayList<GeoPoint>
            field_Boundary, float field_Area, int field_Crop, String createdate,String sowdate,int field_Phenophase, String
            field_Thumb) {
        Field_ID = field_ID;
        Field_Farm_Belong = field_Farm_Belong;
        Field_Name = field_Name;
        Field_Boundary = field_Boundary;
        Field_Area = field_Area;
        Field_Crop = field_Crop;
        Createdate=createdate;
        Sowdate=sowdate;
        Field_Phenophase = field_Phenophase;
        Field_Thumb = field_Thumb;
    }

    //用户自己创建地块时用此方法，因为此时还不知道ID
    public Field(int field_Farm_Belong, String field_Name, ArrayList<GeoPoint>
            field_Boundary, float field_Area, int field_Crop, String createdate,String sowdate,int field_Phenophase, String
                         field_Thumb) {
        Field_Farm_Belong = field_Farm_Belong;
        Field_Name = field_Name;
        Field_Boundary = field_Boundary;
        Field_Area = field_Area;
        Field_Crop = field_Crop;
        Createdate=createdate;
        Sowdate=sowdate;
        Field_Phenophase = field_Phenophase;
        Field_Thumb = field_Thumb;
    }


    public int getField_ID() {
        return Field_ID;
    }

    public void setField_ID(int field_ID) {
        Field_ID = field_ID;
    }

    public int getField_Farm_Belong() {
        return Field_Farm_Belong;
    }

    public void setField_Farm_Belong(int field_Farm_Belong) {
        Field_Farm_Belong = field_Farm_Belong;
    }

    public String getField_Name() {
        return Field_Name;
    }

    public void setField_Name(String field_Name) {
        Field_Name = field_Name;
    }

    public ArrayList<GeoPoint> getField_Boundary() {
        return Field_Boundary;
    }

    public void setField_Boundary(ArrayList<GeoPoint> field_Boundary) {
        Field_Boundary = field_Boundary;
    }

    public float getField_Area() {
        return Field_Area;
    }

    public void setField_Area(float field_Area) {
        Field_Area = field_Area;
    }

    public int getField_Crop() {
        return Field_Crop;
    }

    public void setField_Crop(int field_Crop) {
        Field_Crop = field_Crop;
    }

    public int getField_Phenophase() {
        return Field_Phenophase;
    }

    public void setField_Phenophase(int field_Phenophase) {
        Field_Phenophase = field_Phenophase;
    }

    public String getField_Thumb() {
        return Field_Thumb;
    }

    public void setField_Thumb(String field_Thumb) {
        Field_Thumb = field_Thumb;
    }

    public GeoPoint getCenterPoint() {
        return CenterPoint;
    }

    public void setCenterPoint(GeoPoint centerPoint) {
        CenterPoint = centerPoint;
    }

    public String getCreatedate() {
        return Createdate;
    }

    public void setCreatedate(String createdate) {
        Createdate = createdate;
    }

    public String getSowdate() {
        return Sowdate;
    }

    public void setSowdate(String sowdate) {
        Sowdate = sowdate;
    }
}
