package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * 下拉刷新ExpandableListView
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableExpandableListView extends RefreshableListViewBase<ExpandableListView> {

    public RefreshableExpandableListView(Context context) {
        super(context);
    }

    public RefreshableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(ExpandableListAdapter adapter) {
        contentView.setAdapter(adapter);
    }

    @Override
    protected ExpandableListView createContentView(AttributeSet attrs) {
        ExpandableListView expandableListView = new ExpandableListView(getContext(), attrs);
        expandableListView.setId(R.id.refreshableexpandlistview);
        return expandableListView;
    }

    @Override
    protected void init() {
        super.init();
        contentView.setHeaderDividersEnabled(false);
        contentView.setFooterDividersEnabled(false);
        contentView.setPadding(getResources().getDimensionPixelSize(R.dimen.default_paddingleft), 0,
                               getResources().getDimensionPixelOffset(R.dimen.default_paddingright), 0);
    }

}
