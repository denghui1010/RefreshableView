package com.huilan.refreshableview;

import com.huilan.refreshableview.smoothscroll.OnSmoothMoveFinishedListener;
import com.huilan.refreshableview.smoothscroll.SmoothPaddingRunnable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉ListView基类
 * Created by liudenghui on 14-10-9.
 */
public abstract class RefreshableListViewBase<T extends ListView> extends RefreshableBase<T>
        implements ListView.OnScrollListener {
    protected boolean mAutoRemoveFooter = true;
    protected boolean mFooterEnable = false;
    private List<View> mHeaderViews = new ArrayList<View>();
    private boolean alreadyHasFooterView = false;

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
            post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
        if (isContentViewAtTop()) {
            //处于顶部位置,延迟一定时间收起headerview
            postDelayed(new Runnable() {
                public void run() {
                    smoothScrollTo(headerHeight);
                    //滑动完毕后才还原状态
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setHeaderState(RefreshState.ORIGIN_STATE);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!alreadyHasFooterView) {
                                        if (canAddFooter()) {
                                            if (footerRefreshMode == FooterRefreshMode.AUTO) {
                                                footerView.setClickable(false);
                                                footerView.setFocusable(false);
                                                contentView.addFooterView(footerView, null, false);
                                            } else {
                                                contentView.addFooterView(footerView);
                                            }
                                            alreadyHasFooterView = true;
                                        } else {
                                            contentView.removeFooterView(footerView);
                                            alreadyHasFooterView = false;
                                        }
                                    }
                                }
                            },100);
                        }
                    }, scrollDurationFactor * Math.abs(headerHeight + getPaddingTop()));
                }
            }, millis);
        } else {
            //不处于顶部,直接还原位置
            setHeaderState(RefreshState.ORIGIN_STATE);
            setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
        }
    }

    @Override
    public void notifyHeaderRefreshStarted(int millis) {
        postDelayed(new Runnable() {
            public void run() {
                if (!isContentViewAtTop() || getPaddingTop() != -headerHeight) {
                    return;
                }
                setHeaderState(RefreshState.REFRESHING);
                smoothScrollTo(0);
                //延时到滑动进行完毕才进行真正的刷新回调
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onHeaderRefreshListener != null) {
                            onHeaderRefreshListener.onHeaderRefresh();
                        }
                    }
                }, scrollDurationFactor * Math.abs(getPaddingTop()));
            }
        }, millis);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE && mFooterEnable
                && contentView.getLastVisiblePosition() == contentView.getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                if (onFooterRefreshListener != null) {
                    onFooterRefreshListener.onFooterRefresh();
                }
            }
        }
    }

    /**
     * 当数据内容小于一页时是否自动去除footerview
     *
     * @param autoRemoveFooter 是否自动去除
     */
    public void setAutoRemoveFooter(boolean autoRemoveFooter) {
        mAutoRemoveFooter = autoRemoveFooter;
    }

    public void setDivider(Drawable divider) {
        contentView.setDivider(divider);
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
        contentView.setEmptyView(emptyView);
    }

    @Override
    public void setFooterEnable(ViewGroup.LayoutParams layoutParams, final FooterRefreshMode footerRefreshMode) {
        footerView = getFooterView(footerRefreshMode);
        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                onFooterRefreshListener.onFooterRefresh();
            }
        });
        this.footerRefreshMode = footerRefreshMode;
        footerLayoutParams = new AbsListView.LayoutParams(layoutParams);
        footerView.setLayoutParams(footerLayoutParams);
        footerView.originSate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!alreadyHasFooterView && canAddFooter()) {
//                    System.out.println("hahahaha");
                    if (footerRefreshMode == FooterRefreshMode.AUTO) {
                        footerView.setClickable(false);
                        footerView.setFocusable(false);
                        contentView.addFooterView(footerView, null, false);
                    } else {
                        contentView.addFooterView(footerView);
                    }
                    alreadyHasFooterView = true;
                }
            }
        }, 100);

    }

    @Override
    public void setHeaderEnable(ViewGroup.LayoutParams layoutParams, HeaderRefreshMode headerRefreshMode) {
        headerHeight = layoutParams.height;
        canRefreshDis = layoutParams.height;
        setPadding(getPaddingLeft(), -layoutParams.height, getPaddingRight(), getPaddingBottom());
        this.headerRefreshMode = headerRefreshMode;
        headerView = getHeaderView(headerRefreshMode);
        headerView.setLayoutParams(new AbsListView.LayoutParams(layoutParams));
        headerView.originSate();
        contentView.addHeaderView(headerView, null, false);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        contentView.setOnItemClickListener(listener);
    }

    @Override
    protected Orientation getRefreshableViewScrollDirection() {
        return Orientation.VERTICAL;
    }

    protected void init() {
        contentView.setOnScrollListener(this);
    }

    @Override
    protected boolean isContentViewAtBottom() {
//        return contentView.getLastVisiblePosition() == contentView.getCount() - 1;
        return false;
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
    protected boolean onTouchWhenHeaderRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (headerRefreshState == RefreshState.REFRESHING) {
                    break;
                }
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (isContentViewAtTop() && dY > 0) {
                    int paddingTop = -headerHeight + dY / 3;
                    if (startY == 0) {
                        startY = dY;
                        paddingTop = -headerHeight;
                    }
                    setPadding(0, paddingTop, 0, 0);
                    if (paddingTop > canRefreshDis - headerHeight && headerRefreshState == RefreshState.ORIGIN_STATE) {
                        setHeaderState(RefreshState.CAN_REFRESH);
                    } else if (paddingTop < canRefreshDis - headerHeight && headerRefreshState == RefreshState.CAN_REFRESH) {
                        setHeaderState(RefreshState.ORIGIN_STATE);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                if (headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.REFRESHING);
                    if (onHeaderRefreshListener != null) {
                        onHeaderRefreshListener.onHeaderRefresh();
                    }
                    smoothScrollTo(0);
                } else if (headerRefreshState != RefreshState.REFRESHING) {
                    smoothScrollTo(headerHeight);
                }

        }
        return true;
    }

    @Override
    protected void smoothScrollTo(int x, int y, int duration, long delayMillis, OnSmoothMoveFinishedListener listener) {
        if (null != mCurrentSmoothScrollRunnable) {
            mCurrentSmoothScrollRunnable.stop();
        }
        if (-getPaddingLeft() != x || -getPaddingTop() != y) {
            mCurrentSmoothScrollRunnable = new SmoothPaddingRunnable(this, -getPaddingLeft(), -getPaddingTop(), x, y, duration,
                                                                     listener);
            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(mCurrentSmoothScrollRunnable);
            }
        }
    }

    @Override
    protected void smoothScrollTo(int value, int delayMillis, OnSmoothMoveFinishedListener listener) {
        switch (getRefreshableViewScrollDirection()) {
            case HORIZONTAL:
                smoothScrollTo(value, 0, Math.abs(value + getPaddingLeft()) * scrollDurationFactor, delayMillis, listener);
                break;
            case VERTICAL:
            default:
                smoothScrollTo(0, value, Math.abs(value + getPaddingTop()) * scrollDurationFactor, delayMillis, listener);
                break;
        }
    }

    private boolean canAddFooter() {
        if (footerView == null) {
            mFooterEnable = false;
            return false;
        }
        int lastChildBottom;
        View childAt = contentView.getChildAt(contentView.getChildCount() - 1);
        if(childAt!=null) {
            lastChildBottom = childAt.getBottom();
        } else {
            lastChildBottom = 0;
        }
        if(headerView!=null){
            lastChildBottom -= headerHeight;
        }
        System.out.println("lastChildBottom="+(lastChildBottom)+",getheight="+getHeight());
        if (mAutoRemoveFooter && isContentViewAtTop() && lastChildBottom < getHeight()) {
            mFooterEnable = false;
            return false;
        }
        mFooterEnable = true;
        return true;
    }
}
