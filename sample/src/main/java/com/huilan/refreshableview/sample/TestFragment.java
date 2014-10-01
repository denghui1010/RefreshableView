package com.huilan.refreshableview.sample;

import com.huilan.refreshableview.NotifyListener;
import com.huilan.refreshableview.OnHeaderRefreshListener;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableListView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by liudenghui on 14-9-23.
 */
public class TestFragment extends Fragment implements OnHeaderRefreshListener{
    private RefreshableListView refreshlistview;
    private LinkedList<String> list;
    private MyAdpter myAdpter;
    private int count = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_refreshable_listview, container, false);
        refreshlistview = (RefreshableListView) inflate.findViewById(R.id.rl_list);
        refreshlistview.setHeaderEnable();
        list = new LinkedList<String>();
        for (int i = 0; i < 30; ++i) {
            list.add("这是listview的数据" + i);
        }
        myAdpter = new MyAdpter();
        refreshlistview.setAdapter(myAdpter);
        refreshlistview.setOnHeaderRefreshListener(this);
        refreshlistview.setEmptyView(inflater.inflate(R.layout.layout_loading, refreshlistview.getContentView(), false));

        return inflate;
    }

    private class MyAdpter extends BaseAdapter {
        @Override
        public int getCount() {
            return list==null?0:list.size();
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
                tv = new TextView(getActivity());
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

    @Override
    public void onHeaderRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Random random = new Random();
                int temp = random.nextInt(5) + 1;
                try {
                    Thread.sleep(2000);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            mHandler.sendEmptyMessageDelayed(0, 100);
        } else {
            mHandler.sendEmptyMessageDelayed(1,100);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    onVisible();
                    break;
                case 1:
                    onInVisible();
            }
        }
    };

    protected void onVisible(){
        refreshlistview.notifyHeaderRefreshStarted();
    }

    protected void onInVisible(){}
}
