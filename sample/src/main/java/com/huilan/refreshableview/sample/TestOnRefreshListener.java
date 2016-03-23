package com.huilan.refreshableview.sample;

import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * 测试用的刷新监听
 * Created by Liu Denghui on 16/3/23.
 */
public class TestOnRefreshListener implements RefreshableLayout.OnRefreshListener {

    private ArrayList<String> mList;
    private RefreshableLayout mRefreshableLayout;
    private BaseAdapter mBaseAdapter;

    public TestOnRefreshListener(BaseAdapter baseAdapter, ArrayList<String> list, RefreshableLayout refreshableLayout) {
        mBaseAdapter = baseAdapter;
        mList = list;
        mRefreshableLayout = refreshableLayout;
    }

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
                int count = mBaseAdapter.getCount();
                for (int i = 0; i < temp; ++i, ++count) {
                    mList.add(0, "这是下拉刷新出来的数据" + count);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRefreshableLayout.notifyHeaderRefreshFinished(new RefreshResult(true, "刷新成功"));
                mBaseAdapter.notifyDataSetChanged();
            }
        }.execute();
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
                int count = mBaseAdapter.getCount();
                for (int i = 0; i < temp; ++i, ++count) {
                    mList.add("这是加载更多出来的数据" + count);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRefreshableLayout.notifyFooterRefreshFinished(new RefreshResult(true, "刷新成功"));
                mBaseAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
