package com.huilan.refreshableview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by liudenghui on 14-8-8.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void auto_load(View view) {
        Intent intent = new Intent(this, AutoLoadActivity.class);
        startActivity(intent);
    }

    public void click_load(View view) {
        Intent intent = new Intent(this, Click2LoadActivity.class);
        startActivity(intent);
    }

    public void pull_refresh(View view) {
        Intent intent = new Intent(this, Pull2RefreshActivity.class);
        startActivity(intent);
    }
}
