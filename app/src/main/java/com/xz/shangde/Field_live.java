package com.xz.shangde;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yxq on 2018/5/22.
 */

public class Field_live implements Serializable {
    private int ID;
    private int Field_ID;
    private int Growth;
    private int Moisture;
    private int Disease;
    private int Pest;
    private int Collector_ID;
    private String Date;
    private GeoPoint GPS;
    private String Picture;

    public Field_live(int ID, int field_ID, int growth, int moisture, int disease, int pest, int
            collector_ID, String date, GeoPoint gps, String
            pictures) {
        this.ID = ID;
        Field_ID = field_ID;
        Growth = growth;
        Moisture = moisture;
        Disease = disease;
        Pest = pest;
        Collector_ID = collector_ID;
        Date = date;
        GPS = gps;
        Picture = pictures;
    }

    public Field_live(int field_ID, int growth, int moisture, int disease, int pest, int
            collector_ID, String date, GeoPoint gps, String
                              pictures) {
        Field_ID = field_ID;
        Growth = growth;
        Moisture = moisture;
        Disease = disease;
        Pest = pest;
        Collector_ID = collector_ID;
        Date = date;
        GPS = gps;
        Picture = pictures;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getField_ID() {
        return Field_ID;
    }

    public void setField_ID(int field_ID) {
        Field_ID = field_ID;
    }

    public int getGrowth() {
        return Growth;
    }

    public void setGrowth(int growth) {
        Growth = growth;
    }

    public int getMoisture() {
        return Moisture;
    }

    public void setMoisture(int moisture) {
        Moisture = moisture;
    }

    public int getDisease() {
        return Disease;
    }

    public void setDisease(int disease) {
        Disease = disease;
    }

    public int getPest() {
        return Pest;
    }

    public void setPest(int pest) {
        Pest = pest;
    }

    public int getCollector_ID() {
        return Collector_ID;
    }

    public void setCollector_ID(int collector_ID) {
        Collector_ID = collector_ID;
    }

    public String getCollector_Calendar() {
        return Date;
    }

    public void setCollector_Calendar(String date) {
        Date = date;
    }

    public GeoPoint getLocation() {
        return GPS;
    }

    public void setLocation(GeoPoint gps) {
        GPS = gps;
    }

    public String getPictures() {
        return Picture;
    }

    public void setPictures(String pictures) {
        this.Picture = pictures;
    }
}
