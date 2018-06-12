package com.xz.shangde;

import android.app.Application;
import android.app.NotificationManager;
import android.util.Log;

import com.mob.MobSDK;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.OkHttpClient;

/**
 * @author zxz
 * 存储全局变量，初始化Mob
 */

public class ShangdeApplication extends Application {

    //下载状态判断
    private boolean HasDownload=false;

    private final String URL="http://192.168.1.116/shennong/api/";

    private final OkHttpClient client=new OkHttpClient();

    /**
     * 与数据表对应关系：
     * fields 地块
     * user 用户
     * farm 农场
     * crop_type 作物类型
     * field_lives 地面实况信息
     * rsInfosmation 遥感农情信息
     * rsInfo_grades 产品等级字典（遥感农情信息表）
     */
    private ArrayList<com.xz.shangde.Field> fields=new ArrayList<>();
    private User user;
    private Farm farm;
    private ArrayList<Field_live> field_lives=new ArrayList<>();
    private ArrayList<RSInfo> rsInfosmation=new ArrayList<>();
    private ArrayList<RSInfo_Grade> rsInfo_grades=new ArrayList<>();
    private ArrayList<News> news=new ArrayList<>();
    private ArrayList<Market> markets=new ArrayList<>();
    private LinkedHashMap<Integer,String> croptype_map=new LinkedHashMap<>();
    private LinkedHashMap<Integer,String> disease_map=new LinkedHashMap<>();
    private LinkedHashMap<Integer,String> pest_map=new LinkedHashMap<>();
    private LinkedHashMap<Integer,String> news_map=new LinkedHashMap<>();
//    LinkedHashMap<Integer,String> phenType_map=new LinkedHashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        MobSDK.init(this);
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getURL() {
        return URL;
    }

    public boolean isHasDownload() {
        return HasDownload;
    }

    public void setHasDownload(boolean hasDownload) {
        HasDownload = hasDownload;
    }

    //地块操作方法
    public ArrayList<com.xz.shangde.Field> getFields() {
        return fields;
    }

    public com.xz.shangde.Field getField(int position) {
        return fields.get(position);
    }

    public void setFields(ArrayList<com.xz.shangde.Field> fields) {
        this.fields.clear();
        this.fields = fields;
    }

    public void addField(com.xz.shangde.Field field) {
        this.fields.add(field);
    }

    public void removeField(int position) {
        this.fields.remove(position);
    }

    public void editField(int position, com.xz.shangde.Field field) {
        this.fields.set(position,field);
    }

    //用户操作方法
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //农场操作方法
    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    //拍照所获取的地块实况信息的操作方法
    public ArrayList<Field_live> getField_lives() {
        return field_lives;
    }

    public void setField_lives(ArrayList<Field_live> field_lives) {
        this.field_lives = field_lives;
    }

    public void addField_lives(Field_live field_live){
        this.field_lives.add(field_live);
    }

    public void addField_lives(ArrayList<Field_live> lives){
        this.field_lives.addAll(lives);
    }

    //遥感农情信息的操作方法
    public ArrayList<RSInfo> getRsInfosmation() {
        return rsInfosmation;
    }

    public void setRsInfosmation(ArrayList<RSInfo> rsInfosmation) {
        this.rsInfosmation = rsInfosmation;
    }

    public void addRsInfosmation(ArrayList<RSInfo> rsInfos) {
        this.rsInfosmation.addAll(rsInfos);
    }

    //产品字典的操作方法
    public ArrayList<RSInfo_Grade> getRsInfo_grades() {
        return rsInfo_grades;
    }

    public void setRsInfo_grades(ArrayList<RSInfo_Grade> rsInfo_grades) {
        this.rsInfo_grades = rsInfo_grades;
    }

    //新闻的操作方法
    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    //市场的操作方法
    public ArrayList<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(ArrayList<Market> markets) {
        this.markets = markets;
    }

    public LinkedHashMap<Integer, String> getDisease_map() {
        return disease_map;
    }

    public void setDisease_map(LinkedHashMap<Integer, String> disease_map) {
        this.disease_map = disease_map;
    }

    public LinkedHashMap<Integer, String> getPest_map() {
        return pest_map;
    }

    public void setPest_map(LinkedHashMap<Integer, String> pest_map) {
        this.pest_map = pest_map;
    }

    public LinkedHashMap<Integer, String> getNews_map() {
        return news_map;
    }

    public void setNews_map(LinkedHashMap<Integer, String> news_map) {
        this.news_map = news_map;
    }

    public LinkedHashMap<Integer, String> getCroptype_map() {
        return croptype_map;
    }

    public void setCroptype_map(LinkedHashMap<Integer, String> croptype_map) {
        this.croptype_map = croptype_map;
    }

//    public LinkedHashMap<Integer, String> getPhenType_map() {
//        return phenType_map;
//    }
//
//    public void setPhenType_map(LinkedHashMap<Integer, String> phenType_map) {
//        this.phenType_map = phenType_map;
//    }

}
