package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends RefreshableBase<ListView> {
    public RefreshableListView(Context context) {
        super(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView listView = new ListView(context);
        return listView;
    }

}
