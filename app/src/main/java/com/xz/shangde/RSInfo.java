package com.xz.shangde;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zxz
 * 存储农情遥感信息
 */

public class RSInfo implements Serializable {
    private int ID;
    private int Farm_ID;
    private int Field_ID;
    private int RS_type;
    private String Date;
    private int Grade;
    private double Value;

    public RSInfo(int ID, int farm_ID, int field_ID, int RS_type, String date, int grade,
                  double value) {
        this.ID = ID;
        Farm_ID = farm_ID;
        Field_ID = field_ID;
        this.RS_type = RS_type;
        this.Date = date;
        Grade = grade;
        Value = value;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFarm_ID() {
        return Farm_ID;
    }

    public void setFarm_ID(int farm_ID) {
        Farm_ID = farm_ID;
    }

    public int getField_ID() {
        return Field_ID;
    }

    public void setField_ID(int field_ID) {
        Field_ID = field_ID;
    }

    public int getRS_type() {
        return RS_type;
    }

    public void setRS_type(int RS_type) {
        this.RS_type = RS_type;
    }

    public String getCalendar() {
        return Date;
    }

    public void setCalendar(String date) {
        this.Date = date;
    }

    public int getGrade() {
        return Grade;
    }

    public void setGrade(int grade) {
        Grade = grade;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(double value) {
        Value = value;
    }
}
