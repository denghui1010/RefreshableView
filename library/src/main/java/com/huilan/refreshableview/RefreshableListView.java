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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends RefreshableBase<ListView> implements ListView.OnScrollListener {
    private ListView mListView;
    private List<View> mHeaderViews = new ArrayList<View>();

    public RefreshableListView(Context context) {
        super(context);
        init();
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int divider = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "divider", 0x0);
        if (divider != 0x0) {
            mListView.setDivider(getResources().getDrawable(divider));
        }
        init();
    }

    public void addFooterView(View view) {
        mListView.addFooterView(view);
    }

    public void addHeaderView(View view) {
        mListView.addHeaderView(view);
        mHeaderViews.add(view);
    }

    public void addHeaderView(View view, Object data, boolean isSelectable) {
        mListView.addHeaderView(view, data, isSelectable);
        mHeaderViews.add(view);
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

    public void setEmptyView(final View emptyView) {
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

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
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
        if (mHeaderViews == null || mHeaderViews.size() == 0) {
            return mListView.getFirstVisiblePosition() == 0;
        }
        View childAt = mListView.getChildAt(0);
//        System.out.println("top"+childAt.getTop() +"padt"+getPaddingTop() + mListView.getPaddingTop());
        return childAt == mHeaderViews.get(mHeaderViews.size() - 1) && childAt.getTop() >= mListView .getPaddingTop();
    }

    private void init() {
        mListView.setOnScrollListener(this);
        mListView.setOverScrollMode(OVER_SCROLL_NEVER);
        mListView.setHeaderDividersEnabled(true);
        mListView.setFooterDividersEnabled(true);
        mListView.setPadding(getResources().getDimensionPixelSize(R.dimen.default_paddingleft),
                             getResources().getDimensionPixelOffset(R.dimen.default_paddingtop),
                             getResources().getDimensionPixelOffset(R.dimen.default_paddingright), 0);
        mListView.setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);
    }

}
