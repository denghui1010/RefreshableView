package com.huilan.refreshableview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;


public class MainActivity extends ActionBarActivity {

    private RefreshableListView refreshlistview;

    private LinkedList<String> list;
    private MyAdpter myAdpter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        myAdpter = new MyAdpter();
        refreshlistview.setAdapter(myAdpter);
//        refreshlistview.setOnRefreshListener(this);
    }

    private void initView() {
        refreshlistview = (RefreshableListView) findViewById(R.id.rl_list);
        list = new LinkedList<String>();
        for (int i = 0; i < 30; ++i) {
            list.add("这是listview的数据" + i);
        }
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
                tv = new TextView(MainActivity.this);
            } else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(25);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(list.get(position));
            return tv;
        }

    }

//    @Override
//    public void onRefresh() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                Random random = new Random();
//                int temp = random.nextInt(5) + 1;
//                for (int i = 0; i < temp; ++i, ++count) {
//                    list.addFirst("这是下拉刷新出来的数据" + count);
//                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                myAdpter.notifyDataSetChanged();
////                refreshlistview.onRefreshFinished();
//            }
//        }.execute();
//    }
}
