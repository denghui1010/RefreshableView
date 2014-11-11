package com.huilan.refreshableview;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.RotateHeaderView;
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
import android.widget.LinearLayout;

/**
 * 下拉刷新基类
 * Created by liudenghui on 14-7-29.
 */
public abstract class RefreshableBase<T extends View> extends LinearLayout {

    public final int scrollDurationFactor = 2;

    protected CustomView headerView;
    protected CustomView footerView;
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
    private int mTouchSlop;
    private GestureDetector mGestureDetector;

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

    /**
     * 获取中间区域
     *
     * @return 中间区域的view
     */
    public T getContentView() {
        return contentView;
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

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起footerview,请将刷新适配器的方法写在监听中
     *
     * @param result   刷新结果
     * @param listener 通知监听器,请将刷新适配器的方法写在监听中
     * @param millis   延迟毫秒值
     */
    public void notifyFooterRefreshFinished(final RefreshResult result, final int millis, final NotifyListener listener) {
        if (listener != null && result != RefreshResult.failure) {
            listener.notifyDataSetChanged();
        }
        if(footerRefreshMode == FooterRefreshMode.CLOSE){
            return;
        }
        footerView.refreshFinished(result);
        //延迟一定时间收起footerview
        postDelayed(new Runnable() {
            public void run() {
                if (result == RefreshResult.nomore) {
                    setFooterState(RefreshState.NO_MORE);
                } else {
                    setFooterState(RefreshState.ORIGIN_STATE);
                }
//                if (isContentViewAtTop()) {
//                    smoothScrollTo(headerHeight, 0, new OnSmoothMoveFinishedListener() {
//                        @Override
//                        public void onSmoothScrollFinished() {
//                            //滑动完毕后才还原状态
//                            if (result == RefreshResult.nomore) {
//                                setFooterState(RefreshState.NO_MORE);
//                            } else {
//                                setFooterState(RefreshState.ORIGIN_STATE);
//                            }
//                        }
//                    });
//                } else {
//                    scrollTo(0, headerHeight);
//                    if (result == RefreshResult.nomore) {
//                        setFooterState(RefreshState.NO_MORE);
//                    } else {
//                        setFooterState(RefreshState.ORIGIN_STATE);
//                    }
//                }
            }
        }, millis);

    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏footerview,请将刷新适配器的方法写在监听中
     *
     * @param result   刷新结果
     * @param listener 通知监听器,请将刷新适配器的方法写在监听中
     */
    public void notifyFooterRefreshFinished(RefreshResult result, NotifyListener listener) {
        notifyFooterRefreshFinished(result, 1000, listener);
    }

    /**
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,1秒后会隐藏headerview,请将刷新适配器的方法写在监听中
     *
     * @param result   刷新结果
     * @param listener 通知监听器,请将刷新适配器的方法写在监听中
     */
    public void notifyHeaderRefreshFinished(RefreshResult result, NotifyListener listener) {
        notifyHeaderRefreshFinished(result, 1000, listener);
    }

    /**
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,延迟一定时间后收起headererview,请将刷新适配器的方法写在监听中
     *
     * @param result   刷新结果
     * @param listener 通知监听器,请将刷新适配器的方法写在监听中
     * @param millis   延迟毫秒值
     */
    public void notifyHeaderRefreshFinished(final RefreshResult result, final int millis, final NotifyListener listener) {
        if (listener != null && result != RefreshResult.failure) {
            listener.notifyDataSetChanged();
        }
        if(headerRefreshMode == HeaderRefreshMode.CLOSE){
            return;
        }
        headerView.refreshFinished(result);
        //延迟一定时间收起headerview
        postDelayed(new Runnable() {
            public void run() {
                if (isContentViewAtTop()) {
                    smoothScrollTo(headerHeight, 0, new OnSmoothMoveFinishedListener() {
                        @Override
                        public void onSmoothScrollFinished() {
                            //滑动完毕后才还原状态
                            setHeaderState(RefreshState.ORIGIN_STATE);
                        }
                    });
                } else {
                    scrollTo(0, headerHeight);
                    setHeaderState(RefreshState.ORIGIN_STATE);
                }
            }
        }, millis);

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
        if(headerRefreshMode == HeaderRefreshMode.CLOSE){
            return;
        }
        postDelayed(new Runnable() {
            public void run() {
                if (!isContentViewAtTop() || getScrollYInternal() != headerHeight) {
                    return;
                }
                setHeaderState(RefreshState.REFRESHING);
                smoothScrollTo(0, 0, new OnSmoothMoveFinishedListener() {
                    @Override
                    public void onSmoothScrollFinished() {
                        //延时到滑动进行完毕才进行真正的刷新回调
                        if (onHeaderRefreshListener != null) {
                            onHeaderRefreshListener.onHeaderRefresh();
                        }
                    }
                });
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
     * 设置开启上拉刷新,默认上拉模式 FooterRefreshMode.AUTO
     */
    public void setFooterEnable() {
        setFooterEnable(FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新
     *
     * @param footerRefreshMode 刷新模式,FooterRefreshMode
     */
    public void setFooterEnable(FooterRefreshMode footerRefreshMode) {
        footerView = getFooterView(footerRefreshMode);
        this.footerRefreshMode = footerRefreshMode;
        addView(footerView);
        measureView(footerView);
        footerHeight = footerView.getMeasuredHeight();
        footerView.originSate();
    }

    /**
     * 设置footerview的状态
     *
     * @param state 状态
     */
    public void setFooterState(RefreshState state) {
        if(footerRefreshMode == FooterRefreshMode.CLOSE){
            return;
        }
        switch (state) {
            case CAN_REFRESH:
                footerView.canRefresh();
                break;
            case ORIGIN_STATE:
                footerView.originSate();
                break;
            case REFRESHING:
                footerView.refreshing();
                break;
            case NO_MORE:
                footerView.setFocusable(false);
                footerView.setClickable(false);
                footerView.refreshFinished(RefreshResult.nomore);
        }
        footerRefreshState = state;
    }

    /**
     * 设置开启下拉刷新,默认下拉模式 HeaderRefreshMode.PULL
     */
    public void setHeaderEnable() {
        setHeaderEnable(HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新
     *
     * @param headerRefreshMode 刷新模式,见HeaderRfreshMode
     */
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        headerView = getHeaderView(headerRefreshMode);
        this.headerRefreshMode = headerRefreshMode;
        addView(headerView, 0);
        headerView.originSate();
        measureView(headerView);
        headerHeight = headerView.getMeasuredHeight();
        canRefreshDis = headerHeight;
//        setPadding(getPaddingLeft(), getPaddingTop() - headerHeight, getPaddingRight(), getPaddingBottom());
        scrollTo(0,headerHeight);
    }

    /**
     * 设置headerview的状态
     *
     * @param state 状态
     */
    public void setHeaderState(RefreshState state) {
        if(headerRefreshMode == HeaderRefreshMode.CLOSE){
            return;
        }
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

//    protected void adjustContentViewSize(int changeSize) {
//        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
//        switch (getRefreshableViewScrollDirection()) {
//            case HORIZONTAL:
//                if (mContentWrapper.getWidth() == contentView.getWidth()) {
//                    layoutParams.width = contentView.getWidth() + changeSize;
//                }
//                break;
//            case VERTICAL:
////                System.out.println("mconh=" + mContentWrapper.getHeight() + ",currhe=" + contentView.getHeight() + "changesize="
////                                           + changeSize);
//                if ((changeSize < 0 && mContentWrapper.getHeight() == contentView.getHeight()) || (changeSize > 0
//                        && mContentWrapper.getHeight() != contentView.getHeight())) {
//                    layoutParams.height = contentView.getHeight() + changeSize;
//                }
//            default:
//                break;
//        }
//        contentView.setLayoutParams(layoutParams);
//    }

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
            return new RotateHeaderView(getContext());
        }
        return new RotateHeaderView(getContext());
    }

    /**
     * 获取该RefreshableView的滚动方向
     *
     * @return Orientation 方向
     */
    protected abstract Orientation getRefreshableViewScrollDirection();

    protected int getScrollXInternal() {
        return getScrollX();
    }

    protected int getScrollYInternal() {
        return getScrollY();
    }

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

    protected void measureView(View child) {
        measureView(child, 0,0);
    }

    private void measureView(View v, int width, int height) {
        int widthSpec = 0;
        int heightSpec = 0;
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if(width == 0){
            widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        }
        else if (params.width > 0) {
            widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
        } else if (params.width == -1) {
            widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        } else if (params.width == -2) {
            widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
        }
        if(height == 0){
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        }
        else if (params.height > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        } else if (params.height == -1) {
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else if (params.height == -2) {
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        }
        v.measure(widthSpec, heightSpec);
    }

    protected boolean onTouchWhenHeaderRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (dY < 0 && -dY + getScrollYInternal()-headerHeight > 0) {
                    scrollTo(0, headerHeight);
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
                if (getScrollYInternal()-headerHeight > -canRefreshDis && headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.ORIGIN_STATE);
                } else if (getScrollYInternal()-headerHeight < -canRefreshDis && headerRefreshState == RefreshState.ORIGIN_STATE) {
                    setHeaderState(RefreshState.CAN_REFRESH);
                }
                headerView.onPull(headerHeight-getScrollYInternal(), canRefreshDis);
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.REFRESHING);
                    if (onHeaderRefreshListener != null) {
                        onHeaderRefreshListener.onHeaderRefresh();
                    }
                    smoothScrollTo(0);
//                    adjustContentViewSize(-headerHeight);
                } else if (headerRefreshState != RefreshState.REFRESHING) {
                    smoothScrollTo(headerHeight);
                }
        }
        return true;
    }

    protected void smoothScrollTo(int value, int delayMillis, OnSmoothMoveFinishedListener listener) {
        switch (getRefreshableViewScrollDirection()) {
            case HORIZONTAL:
                smoothScrollTo(value, 0, Math.abs(value - getScrollXInternal()) * scrollDurationFactor, delayMillis, listener);
                break;
            case VERTICAL:
            default:
                smoothScrollTo(0, value, Math.abs(value - getScrollYInternal()) * scrollDurationFactor, delayMillis, listener);
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
        if (getScrollXInternal() != x || getScrollYInternal() != y) {
            mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(this, getScrollXInternal(), getScrollYInternal(), x, y,
                                                                    duration, listener);
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
        final ViewConfiguration config = ViewConfiguration.get(getContext());
        mTouchSlop = config.getScaledTouchSlop();
        mGestureDetector = new GestureDetector(new YScrollDetector());
        setOrientation(VERTICAL);
        contentView = createContentView(attrs);
        addView(contentView, -1, -1);
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