package com.xz.shangde;

import java.io.Serializable;

/**
 * @author zxz
 * 存储农情信息等级字典表
 */

public class RSInfo_Grade implements Serializable {
    private int ID;
    private int Grade;
    private int Type;
    private String Name;

    public RSInfo_Grade(int ID, int grade, int type, String name) {
        this.ID = ID;
        Grade = grade;
        Type = type;
        Name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getGrade() {
        return Grade;
    }

    public void setGrade(int grade) {
        Grade = grade;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
