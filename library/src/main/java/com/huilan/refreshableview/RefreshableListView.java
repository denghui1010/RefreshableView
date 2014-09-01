package com.huilan.refreshableview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends RefreshableBase<ListView> implements ListView.OnScrollListener {
    private ListView mListView;

    public RefreshableListView(Context context) {
        super(context);
        init();
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void addFooterView(View view) {
        mListView.addFooterView(view);
    }

    public void addHeaderView(View view) {
        mListView.addHeaderView(view);
    }

    public int getFooterViewsCount() {
        return mListView.getFooterViewsCount();
    }

    public int getHeaderViewsCount() {
        return mListView.getHeaderViewsCount();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mListView.getLastVisiblePosition() == mListView.getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                onFooterRefreshListener.onFooterRefresh();
            }
        }
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setDivider(Drawable divider) {
        mListView.setDivider(divider);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setEmptyView(final View emptyView){
        addView(emptyView);
        mListView.setEmptyView(emptyView);
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = getMeasuredHeight();
                emptyView.setLayoutParams(layoutParams);
            }
        });
    }


    @Override
    protected ListView createContentView() {
        mListView = new ListView(getContext());
        return mListView;
    }

    protected boolean isContentViewAtBottom() {
        return mListView.getLastVisiblePosition() == mListView.getCount() - 1;
    }

    protected boolean isContentViewAtTop() {
        return mListView.getFirstVisiblePosition() == 0;
    }

    private void init() {
        mListView.setOnScrollListener(this);
        mListView.setSelected(false);
        mListView.setOverScrollMode(OVER_SCROLL_NEVER);
        mListView.setHeaderDividersEnabled(true);
        mListView.setFooterDividersEnabled(true);
        mListView.setPadding(getResources().getDimensionPixelSize(R.dimen.default_paddingleft), 0,
                             getResources().getDimensionPixelSize(R.dimen.default_paddingright), 0);
        mListView.setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);
    }

}
