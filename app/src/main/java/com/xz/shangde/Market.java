package com.xz.shangde;

import java.io.Serializable;

/**
 * @author zxz
 * 存储市场信息
 */

public class Market implements Serializable {
    private int ID;
    private int Crop_Type;
    private float Crop_Price;
    private String Date;

    public Market(int ID, int crop_Type, float crop_Price, String date) {
        this.ID = ID;
        Crop_Type = crop_Type;
        Crop_Price = crop_Price;
        Date = date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCrop_Type() {
        return Crop_Type;
    }

    public void setCrop_Type(int crop_Type) {
        Crop_Type = crop_Type;
    }

    public float getCrop_Price() {
        return Crop_Price;
    }

    public void setCrop_Price(float crop_Price) {
        Crop_Price = crop_Price;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
