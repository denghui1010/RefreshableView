package com.huilan.refreshableview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    private CustomView mHeaderLoadingView;
    private CustomView mFooterLoadingView;

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

    public void setEmptyView(View emptyView) {
        FrameLayout contentWrapper = getContentWrapper();
        if (null != emptyView) {
            emptyView.setClickable(true);
            ViewParent emptyViewParent = emptyView.getParent();
            if (null != emptyViewParent && emptyViewParent instanceof ViewGroup) {
                ((ViewGroup) emptyViewParent).removeView(emptyView);
            }
            FrameLayout.LayoutParams newLp = null;
            ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (null != lp) {
                newLp = new FrameLayout.LayoutParams(lp);
                if (lp instanceof LinearLayout.LayoutParams) {
                    newLp.gravity = ((LinearLayout.LayoutParams) lp).gravity;
                } else {
                    newLp.gravity = Gravity.CENTER;
                }
            }
            if (null != newLp) {
                contentWrapper.addView(emptyView, lp);
            } else {
                contentWrapper.addView(emptyView);
            }
        }
        mListView.setEmptyView(emptyView);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    @Override
    protected ListView createContentView(AttributeSet attrs) {
        mListView = new MyListView(getContext(), attrs);
        mListView.setId(R.id.refreshablelistview);
        return mListView;
    }

    @Override
    protected Orientation getRefreshableViewScrollDirection() {
        return Orientation.VERTICAL;
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
        if (subHeaderView != null) {
            return childAt == subHeaderView.getParent() && childAt.getTop() >= mListView.getPaddingTop();
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

        private void init(){
            mGestureDetector = new GestureDetector(new YScrollDetector());
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
        }

        private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                return Math.abs(distanceY) >= Math.abs(distanceX);
            }
        }
    }

}
