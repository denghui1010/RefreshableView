package com.huilan.refreshableview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableLayout;

/**
 * TextView测试
 */
public class TextViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);
        final TextView textView = (TextView) findViewById(R.id.content_view);
        final RefreshableLayout refreshableLayout = (RefreshableLayout) findViewById(R.id.refresh_view);
        refreshableLayout.setFooterEnable();
        refreshableLayout.setHeaderEnable();
        refreshableLayout.setContentViewShadowEnable(true, true);
        refreshableLayout.setContentViewShadowColor(0x00383838, 0x66000000);
        refreshableLayout.setOnRefreshListener(new RefreshableLayout.OnRefreshListener() {
            @Override
            public void onHeaderRefresh() {
                textView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("我被下拉刷新了");
                        refreshableLayout.notifyHeaderRefreshFinished(new RefreshResult(true, "文本刷新成功啦!!"));
                    }
                }, 3000);
            }

            @Override
            public void onFooterRefresh() {
                textView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("我被上拉刷新了");
                        refreshableLayout.notifyFooterRefreshFinished(new RefreshResult(true, "文本刷新成功啦!!"));
                    }
                }, 3000);
            }
        });
    }
}
