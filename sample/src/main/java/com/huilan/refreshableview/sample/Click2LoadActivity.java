package com.huilan.refreshableview.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huilan.refreshableview.FooterRefreshMode;
import com.huilan.refreshableview.OnFooterRefreshListener;
import com.huilan.refreshableview.RefreshableListView;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Click2LoadActivity extends Activity implements OnFooterRefreshListener {
    private RefreshableListView refreshlistview;

    private LinkedList<String> list;
    private MyAdpter myAdpter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable_listview);
        initView();
        myAdpter = new MyAdpter();
        refreshlistview.setAdapter(myAdpter);
        refreshlistview.setOnFooterRefreshListener(this);
    }

    private void initView() {
        refreshlistview = (RefreshableListView) findViewById(R.id.rl_list);
        refreshlistview.setFooterEnable(FooterRefreshMode.CLICK);
        list = new LinkedList<String>();
        for (int i = 0; i < 30; ++i) {
            list.add("这是listview的数据" + i);
        }
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
                refreshlistview.notifyFooterRefreshFinished(true);
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
                tv = new TextView(Click2LoadActivity.this);
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
