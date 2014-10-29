package com.huilan.refreshableview.sample;

import com.huilan.refreshableview.OnHeaderRefreshListener;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableExpandableListView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class ExpandableListActivity extends Activity implements OnHeaderRefreshListener {
    private int count = 0;
    private LinkedList<String> mGroups;
    private Myadapter mMyadapter;
    private RefreshableExpandableListView mRefreshableview;
    private ArrayList<String> mSubItems;

    public void onHeaderRefresh() {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                Random random = new Random();
                int temp = random.nextInt(5) + 1;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < temp; ++i, ++count) {
                    mGroups.addFirst("这是下拉刷新出来的数据" + count);
                }
                return null;
            }

            protected void onPostExecute(Void paramAnonymousVoid) {
                mMyadapter.notifyDataSetChanged();
                mRefreshableview.notifyHeaderRefreshFinished(RefreshResult.hasmore, null);
            }
        }.execute();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_expandablelistview);
        initData();
        init();
    }

    private void init() {
        mRefreshableview = ((RefreshableExpandableListView) findViewById(R.id.expandlistview));
        mRefreshableview.setOnHeaderRefreshListener(this);
        mRefreshableview.setHeaderEnable();
        mMyadapter = new Myadapter();
        mRefreshableview.setAdapter(mMyadapter);
        mRefreshableview.notifyHeaderRefreshStarted();
    }

    private void initData() {
        this.mGroups = new LinkedList<String>();
        this.mSubItems = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            this.mGroups.add("这是第" + i + "组");
        }
        for (int j = 0; j < 5; j++) {
            this.mSubItems.add("第" + j + "个子条目");
        }
    }

    class Myadapter extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(ExpandableListActivity.this);
            } else {
                tv = (TextView) convertView;
            }
            tv.setClickable(false);
            tv.setFocusable(false);
            tv.setTextSize(25);
            tv.setPadding(100, 10, 10, 10);
            tv.setText(mSubItems.get(childPosition));
            return tv;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mSubItems.size();

        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        public int getGroupCount() {
            return mGroups.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(ExpandableListActivity.this);
            } else {
                tv = (TextView) convertView;
            }
            tv.setClickable(false);
            tv.setFocusable(false);
            tv.setTextSize(25);
            tv.setPadding(100, 10, 10, 10);
            tv.setText(mGroups.get(groupPosition));
            return tv;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    }
}