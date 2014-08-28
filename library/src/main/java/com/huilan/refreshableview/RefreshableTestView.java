package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by liudenghui on 14-8-28.
 */
public class RefreshableTestView extends RefreshableBase<ListView> {

    private ListView mTextView;
    private LinkedList<String> list;


    public RefreshableTestView(Context context) {
        super(context);
        init();
    }

    public RefreshableTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshableTestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        list = new LinkedList<String>();
        for (int i = 0; i < 30; ++i) {
            list.add("这是listview的数据" + i);
        }
    }

    @Override
    protected ListView createContentView() {
        init();
        mTextView = new MyListView(getContext());
        mTextView.setAdapter(new MyAdpter());
        return mTextView;
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
                tv = new TextView(getContext());
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
