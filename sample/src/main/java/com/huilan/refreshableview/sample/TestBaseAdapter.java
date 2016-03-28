package com.huilan.refreshableview.sample;

import android.view.LayoutInflater;
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
    public ArrayList<String> mList;

    public TestBaseAdapter(ArrayList<String> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
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
        MyHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            TextView text = (TextView) convertView.findViewById(R.id.item_text);
            holder = new MyHolder();
            holder.text = text;
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }
        holder.text.setText(mList.get(position));
        return convertView;
    }

    private class MyHolder {
        public TextView text;

    }
}
