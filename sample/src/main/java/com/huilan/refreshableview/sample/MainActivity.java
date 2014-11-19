package com.huilan.refreshableview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by liudenghui on 14-8-8.
 */
public class MainActivity extends Activity {

    public void auto_load(View view) {
        Intent intent = new Intent(this, AutoLoadActivity.class);
        startActivity(intent);
    }

    public void click_load(View view) {
        Intent intent = new Intent(this, Click2LoadActivity.class);
        startActivity(intent);
    }

    public void commonlistview(View view) {
        Intent intent = new Intent(this, CommonListViewActivity.class);
        startActivity(intent);
    }

    public void expandlistview(View view) {
        Intent intent = new Intent(this, ExpandableListActivity.class);
        startActivity(intent);
    }

    public void imagepager(View view) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        startActivity(intent);
    }

    public void pull_refresh(View view) {
        Intent intent = new Intent(this, Pull2RefreshActivity.class);
        startActivity(intent);
    }

    public void scrollview(View view) {
        Intent intent = new Intent(this, ScrollViewActivity.class);
        startActivity(intent);
    }

    public void viewpager(View view) {
        Intent intent = new Intent(this, ViewPagerPull2RActivity.class);
        startActivity(intent);
    }

    public void webview(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
