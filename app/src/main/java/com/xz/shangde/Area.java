package com.xz.shangde;

import java.io.Serializable;

/**
 * Created by yxq on 2018/5/28.
 */

public class Area implements Serializable{
    public double area_m2;
    public double area_mu;

    public void compute(){
        this.area_mu=this.area_m2*9/6000;
    }
}
