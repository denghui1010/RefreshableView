package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新listview
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends RefreshableListViewBase<ListView> {
    private List<View> mHeaderViews = new ArrayList<View>();
    private ListView mListView;

    public RefreshableListView(Context context) {
        super(context);
        init();
    }

    public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
//        System.out.println("firstVisibleItem="+firstVisibleItem+",visibleItemCount=" + visibleItemCount + ",totalItemCount=" + totalItemCount);
//        View childAt = mListView.getChildAt(mListView.getChildCount() - 1);
//        if (footerView != null) {
////                System.out.println("lastview" + footerView.getBottom() + "mlistview" + getBottom()+ ",listviewheight="+mListView.getHeight());
//            if (footerView.getTop() <= mListView.getBottom()) {
////                mListView.removeFooterView(footerView);
////                    footerWrapper.setVisibility(GONE);
////                mFooterVisible = false;
//            } else {
////                    footerView.setVisibility(VISIBLE);
////                mFooterVisible = true;
//            }
//        } else {
////                footerView.setVisibility(GONE);
////            mFooterVisible = false;
//        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE && mFooterVisible
                && mListView.getLastVisiblePosition() == mListView.getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                if (onFooterRefreshListener != null) {
                    onFooterRefreshListener.onFooterRefresh();
                }
            }
        }
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    protected ListView createContentView(AttributeSet attrs) {
        mListView = new MyListView(getContext(), attrs);
        mListView.setId(R.id.refreshablelistview);
        return mListView;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return mListView.getLastVisiblePosition() == mListView.getCount() - 1;
    }

    @Override
    protected boolean isContentViewAtTop() {
        if (mHeaderViews == null || mHeaderViews.size() == 0) {
            return mListView.getFirstVisiblePosition() == 0;
        }
        View childAt = mListView.getChildAt(0);
//        System.out.println("top"+childAt.getTop() +"padt"+getPaddingTop() + mListView.getPaddingTop());
        if (headerView != null) {
            return childAt == headerView && childAt.getTop() >= mListView.getPaddingTop();
        } else {
            return childAt == mHeaderViews.get(mHeaderViews.size() - 1) && childAt.getTop() >= mListView
                    .getPaddingTop();
        }
    }

    private void init() {
        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);
        mListView.setOnScrollListener(this);
        mListView.setPadding(getResources().getDimensionPixelSize(R.dimen.default_paddingleft), 0,
                             getResources().getDimensionPixelOffset(R.dimen.default_paddingright), 0);
    }



    private class MyListView extends ListView {
        private GestureDetector mGestureDetector;

        public MyListView(Context context) {
            super(context);
            init();
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
        }

        private void init() {
            mGestureDetector = new GestureDetector(new YScrollDetector());
        }

        private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return Math.abs(distanceY) >= Math.abs(distanceX);
            }
        }
    }

}
