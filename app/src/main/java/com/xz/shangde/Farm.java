package com.xz.shangde;

import java.io.Serializable;

/**
 * Created by yxq on 2018/5/8.
 */

public class Farm implements Serializable{

    private int Farm_ID;
    private String Farm_Name;
    private String Farm_Province;
    private String Farm_City;
    private String Farm_County;
    private int Farm_Province_Index;
    private int Farm_City_Index;
    private int Farm_County_Index;

    public Farm(int farm_ID, String farm_Name, String farm_Province, String farm_City, String
            farm_County, int farm_Province_Index, int farm_City_Index, int farm_County_Index) {
        Farm_ID = farm_ID;
        Farm_Name = farm_Name;
        Farm_Province = farm_Province;
        Farm_City = farm_City;
        Farm_County = farm_County;
        Farm_Province_Index = farm_Province_Index;
        Farm_City_Index = farm_City_Index;
        Farm_County_Index = farm_County_Index;
    }

    public int getFarm_ID() {
        return Farm_ID;
    }

    public void setFarm_ID(int farm_ID) {
        Farm_ID = farm_ID;
    }

    public String getFarm_Name() {
        return Farm_Name;
    }

    public void setFarm_Name(String farm_Name) {
        Farm_Name = farm_Name;
    }

    public String getFarm_Province() {
        return Farm_Province;
    }

    public void setFarm_Province(String farm_Province) {
        Farm_Province = farm_Province;
    }

    public String getFarm_City() {
        return Farm_City;
    }

    public void setFarm_City(String farm_City) {
        Farm_City = farm_City;
    }

    public String getFarm_County() {
        return Farm_County;
    }

    public void setFarm_County(String farm_County) {
        Farm_County = farm_County;
    }

    public int getFarm_Province_Index() {
        return Farm_Province_Index;
    }

    public void setFarm_Province_Index(int farm_Province_Index) {
        Farm_Province_Index = farm_Province_Index;
    }

    public int getFarm_City_Index() {
        return Farm_City_Index;
    }

    public void setFarm_City_Index(int farm_City_Index) {
        Farm_City_Index = farm_City_Index;
    }

    public int getFarm_County_Index() {
        return Farm_County_Index;
    }

    public void setFarm_County_Index(int farm_County_Index) {
        Farm_County_Index = farm_County_Index;
    }
}
