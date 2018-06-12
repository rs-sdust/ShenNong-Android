package com.xz.shangde.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.xz.shangde.R;

/**
 * @author zxz
 * 显示新闻页面，支持js，视频好像不能播放（待解决）
 */

public class NewsPageActivity extends AppCompatActivity {

    private Toolbar tb_news_page;
    private ImageView iv_back_news_page;
    private WebView webview_news;
    private String URL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        Intent intent=getIntent();
        URL=intent.getStringExtra("URL");

        tb_news_page=findViewById(R.id.tb_news_page);
        webview_news=findViewById(R.id.webview_news);
        iv_back_news_page=findViewById(R.id.iv_back_news_page);
        iv_back_news_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //todo 网页还不能播放视频
        WebSettings webSettings = webview_news.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
         webSettings.setUseWideViewPort(true);
        // 将图片调整到适合webview的大小
         webSettings.setLoadWithOverviewMode(true);
        // 缩放至屏幕的大小
        // 缩放操作 webSettings.setSupportZoom(true);
        // 支持缩放，默认为true。是下面那个的前提。
         webSettings.setBuiltInZoomControls(true);
        // 设置内置的缩放控件。若为false，则该WebView不可缩放
         webSettings.setDisplayZoomControls(false);
        // 隐藏原生的缩放控件
        // 其他细节操作
         webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 关闭webview中缓存
         webSettings.setAllowFileAccess(true);
        // 设置可以访问文件
         webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持通过JS打开新窗口
         webSettings.setLoadsImagesAutomatically(true);
        // /支持自动加载图片
        // /设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        webview_news.loadUrl(URL);
    }
}
