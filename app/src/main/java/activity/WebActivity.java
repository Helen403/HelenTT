package activity;

import android.webkit.WebView;

import com.example.snoy.helen.R;

import base.HBaseActivity;

/**
 * Created by Helen on 2017/6/7.
 */
public class WebActivity extends HBaseActivity {


    private WebView webView;


    @Override
    public int getContentView() {
        return R.layout.activity_web;
    }

    @Override
    public void findViews() {
        webView = (WebView)contentView. findViewById(R.id.web_view);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListeners() {

    }
}
