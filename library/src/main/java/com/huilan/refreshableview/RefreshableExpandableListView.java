package com.huilan.refreshableview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableExpandableListView extends RefreshableBase<ExpandableListView>
        implements AbsListView.OnScrollListener {
    private ExpandableListView mExpandableListView;

    public RefreshableExpandableListView(Context context) {
        super(context);
    }

    public RefreshableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addFooterView(View view) {
        mExpandableListView.addFooterView(view);
    }

    public void addHeaderView(View view) {
        mExpandableListView.addHeaderView(view);
    }

    public int getFooterViewsCount() {
        return mExpandableListView.getFooterViewsCount();
    }

    public int getHeaderViewsCount() {
        return mExpandableListView.getHeaderViewsCount();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mExpandableListView.getLastVisiblePosition() == mExpandableListView.getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                onFooterRefreshListener.onFooterRefresh();
            }
        }
    }

    public void setAdapter(BaseExpandableListAdapter adapter) {
        mExpandableListView.setAdapter(adapter);
    }

    public void setDivider(Drawable divider) {
        mExpandableListView.setDivider(divider);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mExpandableListView.setOnItemClickListener(listener);
    }

    @Override
    protected ExpandableListView createContentView() {
        mExpandableListView = new ExpandableListView(getContext());
        mExpandableListView.setOnScrollListener(this);
        return mExpandableListView;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return mExpandableListView.getLastVisiblePosition() == mExpandableListView.getCount() - 1;
    }

    @Override
    protected boolean isContentViewAtTop() {
        return mExpandableListView.getFirstVisiblePosition() == 0;
    }
}
