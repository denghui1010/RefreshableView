package com.huilan.refreshableview.sample;

import com.huilan.refreshableview.NotifyListener;
import com.huilan.refreshableview.OnHeaderRefreshListener;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableListView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Pull2RefreshActivity extends Activity implements OnHeaderRefreshListener {
    private RefreshableListView refreshlistview;

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
                refreshlistview.notifyHeaderRefreshFinished(RefreshResult.hasmore, new NotifyListener() {
                    @Override
                    public void notifyDataSetChanged() {
                        myAdpter.notifyDataSetChanged();
                    }
                });
            }
        }.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable_listview);
        initView();
        myAdpter = new MyAdpter();
        refreshlistview.setAdapter(myAdpter);
        refreshlistview.setOnHeaderRefreshListener(this);
    }

    private void initView() {
        refreshlistview = (RefreshableListView) findViewById(R.id.rl_list);
        refreshlistview.setHeaderEnable();
        list = new LinkedList<String>();
        refreshlistview.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 30; ++i) {
                    list.add("这是listview的数据" + i);
                    myAdpter.notifyDataSetChanged();
                }
            }
        }, 5000);
        TextView textView = new TextView(getApplicationContext());
        textView.setText("1111111111");
        refreshlistview.setEmptyView(textView);
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
