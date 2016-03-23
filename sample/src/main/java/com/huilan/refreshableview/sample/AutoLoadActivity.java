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


public class AutoLoadActivity extends Activity implements RefreshableLayout.OnRefreshListener {
    private RefreshableListView refreshlistview;

    private LinkedList<String> list;
    private MyAdpter myAdpter;
    private int count = 0;
    private RefreshableLayout mRefreshableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable_listview);
        initView();

        list = new LinkedList<>();
        for (int i = 0; i < 30; ++i) {
            list.add("这是listview的数据" + i);
        }
        myAdpter = new MyAdpter();
        refreshlistview.setAdapter(myAdpter);

        mRefreshableLayout.setFooterEnable();
        mRefreshableLayout.setOnRefreshListener(this);

    }

    private void initView() {
        refreshlistview = (RefreshableListView) findViewById(R.id.rl_listview);
//        mRefreshableLayout = (RefreshableLayout) findViewById(R.id.rl_rl);
    }

    @Override
    public void onHeaderRefresh() {

    }

    @Override
    public void onFooterRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Random random = new Random();
                int temp = random.nextInt(5) + 10;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < temp; ++i, ++count) {
                    list.add("这是加载更多出来的数据" + count);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                myAdpter.notifyDataSetChanged();
                mRefreshableLayout.notifyFooterRefreshFinished(RefreshResult.nomore);
            }
        }.execute();
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
                tv = new TextView(AutoLoadActivity.this);
            } else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(25);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(list.get(position));
            return tv;
        }

    }
}
