package com.xz.shangde;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by yxq on 2018/5/11.
 */

public class FutureWeatherDate implements Serializable {
    private String date;
    private String dayTime;
    private String night;
    private String temperature_daytime;
    private String temperature_night;
    private String week;
    private String wind_power;
    private String wind_direction;

    public FutureWeatherDate(String date, String dayTime, String night, String
            temperature_daytime, String temperature_night, String week, String wind_power, String
            wind_direction) {
        this.date = date;
        this.dayTime = dayTime;
        this.night = night;
        this.temperature_daytime = temperature_daytime;
        this.temperature_night = temperature_night;
        this.week = week;
        this.wind_power = wind_power;
        this.wind_direction = wind_direction;
    }

    public String getDate() {
        return date;
    }

    public String getDayTime() {
        return dayTime;
    }

    public String getNight() {
        return night;
    }

    public String getTemperature_daytime() {
        return temperature_daytime;
    }

    public String getTemperature_night() {
        return temperature_night;
    }

    public String getWeek() {
        return week;
    }

    public String getWind_power() {
        return wind_power;
    }

    public String getWind_direction() {
        return wind_direction;
    }
}
