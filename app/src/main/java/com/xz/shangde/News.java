package com.xz.shangde;

import java.io.Serializable;

/**
 * @author zxz
 * 存储新闻信息
 */

public class News implements Serializable {
    private int ID;
    private int News_type;
    private String News_summary;
    private String News_date;
    private String News_URL;
    private String News_thumb_URL;

    public News(int ID, int news_type, String news_summary, String news_date, String news_URL,
                String news_thumb_URL) {
        this.ID = ID;
        News_type = news_type;
        News_summary = news_summary;
        News_date = news_date;
        News_URL = news_URL;
        News_thumb_URL = news_thumb_URL;
    }

    public int getID() {
        return ID;
    }

    public int getNews_type() {
        return News_type;
    }

    public String getNews_summary() {
        return News_summary;
    }

    public String getNews_date() {
        return News_date;
    }

    public String getNews_URL() {
        return News_URL;
    }

    public String getNews_thumb_URL() {
        return News_thumb_URL;
    }
}
