package com.huilan.refreshableview;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.Pull2RefreshHeaderView;
import com.huilan.refreshableview.smoothscroll.OnSmoothMoveFinishedListener;
import com.huilan.refreshableview.smoothscroll.SmoothMoveRunnableBase;
import com.huilan.refreshableview.smoothscroll.SmoothScrollRunnable;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 下拉刷新基类
 * Created by liudenghui on 14-7-29.
 */
public abstract class RefreshableBase<T extends View> extends LinearLayout {

    public final int scrollDurationFactor = 2;

    protected CustomView headerView;
    protected CustomView footerView;
    protected ViewGroup.LayoutParams headerLayoutParams;
    protected ViewGroup.LayoutParams footerLayoutParams;
    protected OnHeaderRefreshListener onHeaderRefreshListener;
    protected OnFooterRefreshListener onFooterRefreshListener;
    protected HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    protected FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;
    protected RefreshState headerRefreshState = RefreshState.ORIGIN_STATE;
    protected RefreshState footerRefreshState = RefreshState.ORIGIN_STATE;
    protected int headerHeight;
    protected int footerHeight;
    protected int startX;
    protected int startY;
    protected T contentView;
    protected int canRefreshDis;
    protected SmoothMoveRunnableBase mCurrentSmoothScrollRunnable;
    private boolean requireInterupt;
    private Scroller scroller;
    private int mTouchSlop;
    private GestureDetector mGestureDetector;
    private FrameLayout mContentWrapper;

    public RefreshableBase(Context context) {
        super(context);
        init(null);
    }

    public RefreshableBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RefreshableBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 获取中间区域
     *
     * @return 中间区域的view
     */
    public T getContentView() {
        return contentView;
    }

    public FrameLayout getContentWrapper() {
        return mContentWrapper;
    }

    /**
     * 获取底部刷新模式,见FooterRefreshMode
     *
     * @return FooterRefreshMode 底部刷新模式
     */
    public FooterRefreshMode getFooterRefreshMode() {
        return footerRefreshMode;
    }

    public CustomView getFooterView() {
        return footerView;
    }

    /**
     * 获取顶部刷新模式
     *
     * @return HeaderRefreshMode 顶部刷新模式
     */
    public HeaderRefreshMode getHeaderRefreshMode() {
        return headerRefreshMode;
    }

    /**
     * 获取headerView,没有headerView的时候返回null
     *
     * @return headerView
     */
    public CustomView getHeaderView() {
        return headerView;
    }

    @Deprecated
    public void notifyFooterRefreshFinished(final RefreshResult result, final BaseAdapter adapter, int millis) {
        footerView.refreshFinished(result);
        postDelayed(new Runnable() {
            public void run() {
                if (result != RefreshResult.failure && adapter != null) {
                    adapter.notifyDataSetChanged();
                } else if (result == RefreshResult.nomore) {
                    footerRefreshState = RefreshState.NO_MORE;
                    footerView.setFocusable(false);
                    footerView.setClickable(false);
                } else {
                    footerRefreshState = RefreshState.ORIGIN_STATE;
                }
                footerView.refreshFinished(result);
            }
        }, millis);
    }

    /**
     * 通知底部刷新完毕,0延迟
     *
     * @param result 刷新结果
     */
    public void notifyFooterRefreshFinished(RefreshResult result) {
        notifyFooterRefreshFinished(result, null, 0);
    }

    @Deprecated
    public void notifyFooterRefreshFinished(RefreshResult result, BaseAdapter adapter) {
        notifyFooterRefreshFinished(result, adapter, 500);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏footerview,不会进行刷新,使用带回调参数的方法可以获得更好的效果
     *
     * @param result 刷新结果
     */
    @Deprecated
    public void notifyHeaderRefreshFinished(RefreshResult result) {
        notifyHeaderRefreshFinished(result, 1000);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏headerview,请将刷新适配器的方法写在监听中
     *
     * @param result   刷新结果
     * @param listener 通知监听器,请将刷新适配器的方法写在监听中
     */
    public void notifyHeaderRefreshFinished(RefreshResult result, NotifyListener listener) {
        notifyHeaderRefreshFinished(result, 1000, listener);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起headererview,不会进行刷新,使用带回调参数的方法可以获得更好的效果
     *
     * @param result 刷新结果
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshFinished(final RefreshResult result, int millis) {
        notifyHeaderRefreshFinished(result, millis, null);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起headererview,请将刷新适配器的方法写在监听中
     *
     * @param result   刷新结果
     * @param listener 通知监听器,请将刷新适配器的方法写在监听中
     * @param millis   延迟毫秒值
     */
    public void notifyHeaderRefreshFinished(final RefreshResult result, final int millis, final NotifyListener listener) {
        headerView.refreshFinished(result);
        if (listener != null && result != RefreshResult.failure) {
            listener.notifyDataSetChanged();
        }
        //延迟一定时间收起headerview
        if (isContentViewAtTop()) {
            postDelayed(new Runnable() {
                public void run() {
                    smoothScrollTo(0);
                    //滑动完毕后才还原状态
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setHeaderState(RefreshState.ORIGIN_STATE);
                        }
                    }, scrollDurationFactor * Math.abs(headerHeight - getScrollY()));
                }
            }, millis);
        } else {
            scrollTo(0, 0);
            setHeaderState(RefreshState.ORIGIN_STATE);
        }
    }

    /**
     * 通知refreshview进入刷新状态,延迟300毫秒
     */
    public void notifyHeaderRefreshStarted() {
        notifyHeaderRefreshStarted(300);
    }

    /**
     * 通知refreshview进入刷新状态,延迟一定时间
     *
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshStarted(int millis) {
        postDelayed(new Runnable() {
            public void run() {
                if (!isContentViewAtTop() || getScrollY() != headerHeight) {
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
                }, scrollDurationFactor * Math.abs(0 - getScrollY()));
            }
        }, millis);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (headerRefreshMode != HeaderRefreshMode.CLOSE) {
            return onInterceptWhenHeaderRefreshEnable(event);
        }
        if (footerRefreshMode != FooterRefreshMode.CLOSE) {
            onInterceptWhenFooterRefreshEnable(event);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (headerRefreshMode != HeaderRefreshMode.CLOSE) {
            return onTouchWhenHeaderRefreshEnable(event);
        }
        if (footerRefreshMode != FooterRefreshMode.CLOSE) {
            onTouchWhenFooterRefreshEnable(event);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     */
    public void setFooterEnable() {
        footerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                        getResources().getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     *
     * @param footerRefreshMode footer刷新模式
     */
    public void setFooterEnable(FooterRefreshMode footerRefreshMode) {
        footerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                        getResources().getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, footerRefreshMode);
    }

    /**
     * 设置开启上拉刷新,默认上拉模式为 FooterRefreshMode.AUTO
     *
     * @param layoutParams 上拉view的layoutparams
     */
    public void setFooterEnable(ViewGroup.LayoutParams layoutParams) {
        setFooterEnable(layoutParams, FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新
     *
     * @param layoutParams      上拉view的layoutparams
     * @param footerRefreshMode 刷新模式,FooterRefreshMode
     */
    public void setFooterEnable(ViewGroup.LayoutParams layoutParams, FooterRefreshMode footerRefreshMode) {
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
        if (contentView instanceof ListView) {
            footerLayoutParams = new AbsListView.LayoutParams(layoutParams);
            footerView.setLayoutParams(footerLayoutParams);
            if (footerRefreshMode == FooterRefreshMode.AUTO) {
                footerView.setClickable(false);
                footerView.setFocusable(false);
                ((ListView) contentView).addFooterView(footerView, null, false);
            } else {
                ((ListView) contentView).addFooterView(footerView);
            }
        } else {
            footerView.setLayoutParams(footerLayoutParams);
            footerLayoutParams = layoutParams;
            addView(footerView);
        }
        footerView.originSate();
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度,默认下拉模式 HeaderRefreshMode.PULL
     */
    public void setHeaderEnable() {
        headerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                        getResources().getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度
     *
     * @param headerRefreshMode 刷新模式
     */
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        headerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                        getResources().getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, headerRefreshMode);
    }

    /**
     * 设置开启下拉刷新,默认下拉模式为 HeaderRefreshMode.PULL
     *
     * @param layoutParams 下拉view的layoutparams
     */
    public void setHeaderEnable(ViewGroup.LayoutParams layoutParams) {
        setHeaderEnable(layoutParams, HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新
     *
     * @param layoutParams      下拉view的layoutparams
     * @param headerRefreshMode 刷新模式,见HeaderRfreshMode
     */
    public void setHeaderEnable(ViewGroup.LayoutParams layoutParams, HeaderRefreshMode headerRefreshMode) {
        setPadding(0, -layoutParams.height, 0, 0);
        headerView = getHeaderView(headerRefreshMode);
        this.headerRefreshMode = headerRefreshMode;
        headerLayoutParams = layoutParams;
        headerView.setLayoutParams(layoutParams);
        addView(headerView, 0);
        headerView.originSate();
    }

    /**
     * 设置上拉刷新监听
     *
     * @param onFooterRefreshListener 上拉刷新监听
     */
    public void setOnFooterRefreshListener(OnFooterRefreshListener onFooterRefreshListener) {
        this.onFooterRefreshListener = onFooterRefreshListener;
    }

    /**
     * 设置下拉刷新监听
     *
     * @param onHeaderRefreshListener 下拉刷新监听
     */
    public void setOnHeaderRefreshListener(OnHeaderRefreshListener onHeaderRefreshListener) {
        this.onHeaderRefreshListener = onHeaderRefreshListener;
    }

    /**
     * 停止滚动并回到原始状态
     */
    @Deprecated
    public void stopAndReset() {
        if (mCurrentSmoothScrollRunnable != null) {
            mCurrentSmoothScrollRunnable.stop();
        }
        setHeaderState(RefreshState.ORIGIN_STATE);
        scrollTo(0, headerHeight);
    }

    /**
     * 中间内容view
     *
     * @return view
     */
    protected abstract T createContentView(AttributeSet attrs);

    protected CustomView getFooterView(FooterRefreshMode footerRefreshMode) {
        if (footerRefreshMode == FooterRefreshMode.AUTO) {
            return new AutoLoadFooterView(getContext());
        } else if (footerRefreshMode == FooterRefreshMode.CLICK) {
            return new Click2LoadFooterView(getContext());
        } else if (footerRefreshMode == FooterRefreshMode.PULL) {
            //todo pull
            return null;
        }
        return new AutoLoadFooterView(getContext());
    }

    protected CustomView getHeaderView(HeaderRefreshMode headerRefreshMode) {
        if (headerRefreshMode == HeaderRefreshMode.PULL) {
            return new Pull2RefreshHeaderView(getContext());
        }
        return new Pull2RefreshHeaderView(getContext());
    }

    protected abstract Orientation getRefreshableViewScrollDirection();

    /**
     * contentview是否滚动到底部
     *
     * @return 是否
     */
    protected abstract boolean isContentViewAtBottom();

    /**
     * contentview是否滚动到顶部
     *
     * @return 是否
     */
    protected abstract boolean isContentViewAtTop();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (headerView != null && headerHeight == 0) {
            headerHeight = headerView.getHeight();
            canRefreshDis = headerHeight;
        }
        if (footerView != null && footerHeight == 0) {
            footerHeight = footerView.getHeight();
        }
        super.onLayout(changed, l, t, r, b);
    }

    protected boolean onTouchWhenHeaderRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (dY < 0 && -dY + getScrollY() > 0) {
                    scrollTo(0, 0);
                    return true;
                }
                if (headerRefreshState != RefreshState.REFRESHING) {
                    scrollBy(0, -dY / 3);
                } else {
//                    if (getScrollY() + headerHeight - dY <= 0) {
//                        scrollTo(0, headerHeight);
//                    } else if (dY < 0) {
                    scrollBy(0, -dY);
//                    } else {
//                        scrollBy(0, -dY / 3);
//                    }
                }
                if (getScrollY() > -canRefreshDis && headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.ORIGIN_STATE);
                } else if (getScrollY() < -canRefreshDis && headerRefreshState == RefreshState.ORIGIN_STATE) {
                    setHeaderState(RefreshState.CAN_REFRESH);
                }
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.REFRESHING);
                    if (onHeaderRefreshListener != null) {
                        onHeaderRefreshListener.onHeaderRefresh();
                    }
                    smoothScrollTo(-headerHeight);
                } else if (headerRefreshState != RefreshState.REFRESHING) {
                    smoothScrollTo(0);
                }
        }
        return true;
    }

    protected void setHeaderState(RefreshState state) {
        switch (state) {
            case CAN_REFRESH:
                headerView.canRefresh();
                break;
            case ORIGIN_STATE:
                headerView.originSate();
                break;
            case REFRESHING:
                headerView.refreshing();
                break;
        }
        headerRefreshState = state;
    }

    //    private void smoothScrollBy(int dx, int dy, OnSmoothScrollListener listener) {
//        if (!scroller.isFinished()) {
//            scroller.abortAnimation();
//        }
//        scroller.startScroll(getScrollX(), getScrollY(), dx, dy, scrollDurationFactor * Math.abs(dy));
//        if (listener != null) {
//            listener.onSmoothScrollStart();
//        }
//        postInvalidate();
//    }
//
//    private void smoothScrollTo(int x, int y, OnSmoothScrollListener listener) {
//        smoothScrollBy(x - getScrollX(), y - getScrollY(), listener);
//
//    }
    protected void smoothScrollTo(int value, int delayMillis, OnSmoothMoveFinishedListener listener) {
        switch (getRefreshableViewScrollDirection()) {
            case HORIZONTAL:
                smoothScrollTo(value, 0, Math.abs(value - getScrollX()) * scrollDurationFactor, delayMillis, listener);
                break;
            case VERTICAL:
            default:
                smoothScrollTo(0, value, Math.abs(value - getScrollY()) * scrollDurationFactor, delayMillis, listener);
                break;
        }
    }

    protected void smoothScrollTo(int value, int delayMillis) {
        smoothScrollTo(value, delayMillis, null);
    }

    protected void smoothScrollTo(int value) {
        smoothScrollTo(value, 0);
    }

    protected void smoothScrollTo(int x, int y, int duration, long delayMillis, OnSmoothMoveFinishedListener listener) {
        if (null != mCurrentSmoothScrollRunnable) {
            mCurrentSmoothScrollRunnable.stop();
        }
        if (getScrollX() != x || getScrollY() != y) {
            mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(this, getScrollX(), getScrollY(), x, y, duration, listener);
            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(mCurrentSmoothScrollRunnable);
            }
        }
    }

    protected void smoothScrollToAndBack(int y) {
        smoothScrollTo(y, 0, new OnSmoothMoveFinishedListener() {
            @Override
            public void onSmoothScrollFinished() {
                smoothScrollTo(0);
            }
        });
    }

    private void init(AttributeSet attrs) {
        ViewConfiguration config = ViewConfiguration.get(getContext());
        mTouchSlop = config.getScaledTouchSlop();
        mGestureDetector = new GestureDetector(new YScrollDetector());
        scroller = new Scroller(getContext());
        setOrientation(VERTICAL);
        mContentWrapper = new FrameLayout(getContext());
        mContentWrapper.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        contentView = createContentView(attrs);
        addView(mContentWrapper);
        if (contentView != null) {
            mContentWrapper.addView(contentView);
        }
    }

    private boolean onInterceptWhenFooterRefreshEnable(MotionEvent event) {
        return false;
    }

    private boolean onInterceptWhenHeaderRefreshEnable(MotionEvent event) {
        if (headerRefreshMode == HeaderRefreshMode.CLOSE) {
            return super.onInterceptTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                requireInterupt = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int currX = (int) event.getRawX();
                int dX = currX - startX;
                int dY = currY - startY;
                if (headerRefreshState == RefreshState.REFRESHING) {
                    return false;
                }
                if (isContentViewAtTop() && dY > 0 && mGestureDetector.onTouchEvent(event)) {
                    requireInterupt = true;
                }
//                if (headerRefreshState == RefreshState.REFRESHING && (getScrollY()+dY<0)) {
//                    requireInterupt = true;
//                }
//                System.out.println("slop="+mTouchSlop+",dx="+dX);
//                if (dX > 0) {
//                    requireInterupt = false;
//                }
                break;
        }
        return requireInterupt;
    }

    private void onTouchWhenFooterRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    public static enum Orientation {
        VERTICAL,
        HORIZONTAL;
    }

    private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * 如果滚动更接近水平方向,返回false
             */
            return (Math.abs(distanceY) > Math.abs(distanceX));
        }
    }

}