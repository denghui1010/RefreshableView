package com.huilan.refreshableview;

import com.huilan.refreshableview.smoothscroll.OnSmoothMoveFinishedListener;
import com.huilan.refreshableview.smoothscroll.SmoothPaddingRunnable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉ListView基类
 * Created by liudenghui on 14-10-9.
 */
public abstract class RefreshableListViewBase<T extends ListView> extends RefreshableBase<T>
        implements ListView.OnScrollListener {
    private List<View> mHeaderViews = new ArrayList<View>();

    public RefreshableListViewBase(Context context) {
        super(context);
        init();
    }

    public RefreshableListViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshableListViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void addFooterView(View view) {
        contentView.addFooterView(view);
    }

    public void addHeaderView(View view) {
        contentView.addHeaderView(view);
        mHeaderViews.add(view);
    }

    public void addHeaderView(View view, Object data, boolean isSelectable) {
        contentView.addHeaderView(view, data, isSelectable);
        mHeaderViews.add(view);
    }

    public ListAdapter getAdapter() {
        return contentView.getAdapter();
    }

    public void setAdapter(ListAdapter adapter) {
        contentView.setAdapter(adapter);
    }

    public int getFooterViewsCount() {
        return contentView.getFooterViewsCount();
    }

    public int getHeaderViewsCount() {
        return contentView.getHeaderViewsCount();
    }

    @Override
    public void notifyHeaderRefreshFinished(final RefreshResult result, final int millis, final NotifyListener listener) {
        headerView.refreshFinished(result);
        if (listener != null && result != RefreshResult.failure) {
            listener.notifyDataSetChanged();
        }
        if (!isContentViewAtTop()) {
            //回到顶部位置
//            contentView.smoothScrollToPosition(0);
            contentView.setSelection(0);
        }
        //延迟一定时间收起headerview
        smoothScrollTo(headerHeight, millis, new OnSmoothMoveFinishedListener() {
            @Override
            public void onSmoothScrollFinished() {
                //滑动完毕后才还原状态
                setHeaderState(RefreshState.ORIGIN_STATE);
            }
        });
    }

    @Override
    public void setFooterEnable(final FooterRefreshMode footerRefreshMode) {
        footerView = getFooterView(footerRefreshMode);

        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setFooterState(RefreshState.REFRESHING);
                onFooterRefreshListener.onFooterRefresh();
            }
        });
        this.footerRefreshMode = footerRefreshMode;
        footerView.originSate();
        if (footerRefreshMode == FooterRefreshMode.AUTO) {
            footerView.setClickable(false);
            footerView.setFocusable(false);
            contentView.addFooterView(footerView, null, false);
        } else {
            contentView.addFooterView(footerView);
        }
        measureView(footerView);
        footerHeight = footerView.getMeasuredHeight();
    }

    @Override
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        this.headerRefreshMode = headerRefreshMode;
        headerView = getHeaderView(headerRefreshMode);
        headerView.originSate();
        contentView.addHeaderView(headerView, null, false);
        measureView(headerView);
        headerHeight = headerView.getMeasuredHeight();
        canRefreshDis = headerHeight;
        headerView
                .setPadding(headerView.getPaddingLeft(), headerView.getPaddingTop() - headerHeight, headerView.getPaddingRight(),
                            headerView.getPaddingBottom());
    }

    @Override
    protected Orientation getRefreshableViewScrollDirection() {
        return Orientation.VERTICAL;
    }

        @Override
    public void scrollBy(int x, int y) {
        headerView.setPadding(headerView.getPaddingLeft()-x, headerView.getPaddingTop()-y, headerView.getPaddingRight(),
                              headerView.getPaddingBottom());
    }

    @Override
    public void scrollTo(int x, int y) {
        headerView.setPadding(-x, -y, headerView.getPaddingRight(), headerView.getPaddingBottom());
    }

    @Override
    protected int getScrollXInternal() {
        return -headerView.getPaddingLeft();
    }

    @Override
    protected int getScrollYInternal() {
        return -headerView.getPaddingTop();
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return contentView.getLastVisiblePosition() == contentView.getCount() - 1;
    }

    @Override
    protected boolean isContentViewAtTop() {
        if (mHeaderViews == null || mHeaderViews.size() == 0) {
            return contentView.getFirstVisiblePosition() == 0;
        }
        View childAt = contentView.getChildAt(0);
//        System.out.println("top"+childAt.getTop() +"padt"+getPaddingTop() + mListView.getPaddingTop());
        if (headerView != null) {
            return childAt == headerView && childAt.getTop() >= contentView.getPaddingTop();
        } else {
            return childAt == mHeaderViews.get(mHeaderViews.size() - 1) && childAt.getTop() >= contentView.getPaddingTop();
        }
    }

    @Override
    protected void smoothScrollTo(int x, int y, int duration, long delayMillis, OnSmoothMoveFinishedListener listener) {
        if (null != mCurrentSmoothScrollRunnable) {
            mCurrentSmoothScrollRunnable.stop();
        }
        if (getScrollXInternal() != x || getScrollYInternal() != y) {
            mCurrentSmoothScrollRunnable = new SmoothPaddingRunnable(headerView, getScrollXInternal(), getScrollYInternal(), x, y,
                                                                     duration, listener);
            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(mCurrentSmoothScrollRunnable);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE && contentView.getLastVisiblePosition() == contentView.getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                setFooterState(RefreshState.REFRESHING);
                if (onFooterRefreshListener != null) {
                    onFooterRefreshListener.onFooterRefresh();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void setDivider(Drawable divider) {
        contentView.setDivider(divider);
    }

    public void setEmptyView(View emptyView) {
        if (null != emptyView) {
            emptyView.setClickable(true);
            ViewParent emptyViewParent = emptyView.getParent();
            if (null != emptyViewParent && emptyViewParent instanceof ViewGroup) {
                ((ViewGroup) emptyViewParent).removeView(emptyView);
            }
            addView(emptyView);
            contentView.setEmptyView(emptyView);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        contentView.setOnItemClickListener(listener);
    }

    protected void init() {
        contentView.setOnScrollListener(this);
    }
}
