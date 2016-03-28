package com.huilan.refreshableview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableScrollView;

import java.util.ArrayList;

/**
 * ScrollView测试
 * Created by liudenghui on 14-8-29.
 */
public class ScrollViewActivity extends AppCompatActivity implements RefreshableLayout.OnRefreshListener {
    private LinearLayout mLinearLayout;
    private RefreshableScrollView mScrollView;
    private ArrayList<TextView> mTextViews;
    private RefreshableLayout mRefreshableLayout;

    public void onHeaderRefresh() {
        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    TextView textView = new TextView(ScrollViewActivity.this);
                    textView.setTextSize(25.0F);
                    textView.setText("这是scrollview刷新出来第" + i + "条");
                    mLinearLayout.addView(textView, 0);
                }
                mRefreshableLayout.notifyHeaderRefreshFinished(new RefreshResult(true, "刷新成功"));
            }
        }, 3000);
    }

    @Override
    public void onFooterRefresh() {

    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_scrollview);
        initData();
        init();
    }

    private void init() {
        mScrollView = ((RefreshableScrollView) findViewById(R.id.scrollview));
        mRefreshableLayout = (RefreshableLayout) findViewById(R.id.rl_rl);
        mRefreshableLayout.setOnRefreshListener(this);
        mScrollView.addView(mLinearLayout);
        mRefreshableLayout.setHeaderEnable();
        mRefreshableLayout.setContentViewShadowEnable(true, false);
    }

    private void initData() {
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mTextViews = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            TextView textView = new TextView(this);
            textView.setTextSize(25);
            textView.setText("这是scrollview第" + i + "条");
            textView.setPadding(10, 10, 10, 10);
            mLinearLayout.addView(textView);
        }
    }
}