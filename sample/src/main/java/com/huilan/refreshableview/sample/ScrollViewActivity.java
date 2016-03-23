package com.huilan.refreshableview.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableScrollView;

import java.util.ArrayList;

/**
 * Created by liudenghui on 14-8-29.
 */
public class ScrollViewActivity extends Activity implements RefreshableLayout.OnRefreshListener {
    private LinearLayout mLinearLayout;
    private RefreshableScrollView mScrollView;
    private ArrayList<TextView> mTextViews;
    private RefreshableLayout mRefreshableLayout;

    public void onHeaderRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                for (int i = 0; i < 5; i++) {
                    TextView textView = new TextView(ScrollViewActivity.this);
                    textView.setTextSize(25.0F);
                    textView.setText("这是scrollview刷新出来第" + i + "条");
                    mLinearLayout.addView(textView, 0);
                }
                mRefreshableLayout.notifyHeaderRefreshFinished(new RefreshResult(true, "刷新成功"));
            }
        }.execute();
    }

    @Override
    public void onFooterRefresh() {

    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_scrollview);
        initData();
        init();
//        mRefreshableLayout.notifyHeaderRefreshStarted();
    }

    private void init() {
        mScrollView = ((RefreshableScrollView) findViewById(R.id.scrollview));
        mRefreshableLayout = (RefreshableLayout) findViewById(R.id.rl_rl);
        mRefreshableLayout.setOnRefreshListener(this);
        mScrollView.addView(mLinearLayout);
        mRefreshableLayout.setHeaderEnable();
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