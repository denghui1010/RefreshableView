package com.huilan.refreshableview.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableListView;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Pull2RefreshActivity extends Activity implements RefreshableLayout.OnRefreshListener {
    private RefreshableListView refreshlistview;
    private RefreshableLayout mRefreshableLayout;

    private LinkedList<String> list;
    private MyAdpter myAdpter;
    private int count = 0;

    @Override
    public void onHeaderRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Random random = new Random();
                int temp = random.nextInt(5) + 1;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < temp; ++i, ++count) {
                    list.addFirst("这是下拉刷新出来的数据" + count);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRefreshableLayout.notifyHeaderRefreshFinished(RefreshResult.hasmore);
                myAdpter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onFooterRefresh() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable_listview);

        initView();

        mRefreshableLayout.setHeaderEnable();
        list = new LinkedList<>();
        refreshlistview.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 30; ++i) {
                    list.add("这是listview的数据" + i);
                    myAdpter.notifyDataSetChanged();
                }
            }
        }, 0);
        myAdpter = new MyAdpter();
        refreshlistview.setAdapter(myAdpter);
        mRefreshableLayout.setOnRefreshListener(this);
    }

    private void initView() {
        mRefreshableLayout = (RefreshableLayout) findViewById(R.id.rl_rl);
        refreshlistview = (RefreshableListView) findViewById(R.id.rl_listview);
//        refreshlistview.setEmptyView(getLayoutInflater().inflate(R.layout.layout_loading, mRefreshableLayout.getContentView(), false));
//        refreshlistview.notifyHeaderRefreshStarted();
    }

    private class MyAdpter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(Pull2RefreshActivity.this);
            } else {
                tv = (TextView) convertView;
            }
            tv.setClickable(false);
            tv.setFocusable(false);
            tv.setTextSize(25);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(list.get(position));
            return tv;
        }
    }

}
