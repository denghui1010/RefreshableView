package com.huilan.refreshableview.sample;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 测试用的BaseAdapter
 * Created by Liu Denghui on 16/3/23.
 */
public class TestBaseAdapter extends BaseAdapter {
    public ArrayList<String> mLinkedList;

    public TestBaseAdapter(ArrayList<String> linkedList) {
        mLinkedList = linkedList;
    }

    @Override
    public int getCount() {
        return mLinkedList.size();
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
            tv = new TextView(parent.getContext());
            tv.setClickable(false);
            tv.setFocusable(false);
            tv.setTextSize(25);
            tv.setTextColor(Color.DKGRAY);
            tv.setPadding(50, 10, 10, 50);
        } else {
            tv = (TextView) convertView;
        }

        tv.setText(mLinkedList.get(position));
        return tv;
    }
}
