package com.huilan.refreshableview;

import com.huilan.refreshableview.footerview.AutoLoadFooterViewI;
import com.huilan.refreshableview.footerview.Click2LoadFooterViewI;
import com.huilan.refreshableview.headerview.Pull2RefreshHeaderViewI;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by liudenghui on 14-7-29.
 */
public abstract class RefreshableBase<T extends View> extends LinearLayout{

    public final int scrollDurationFactor = 2;

    protected CustomView headerView;
    protected CustomView subHeaderView;
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
    private int dividerHeight;
    private int startX;
    private int startY;
    private T contentView;
    private boolean requireInterupt;
    private int canRefreshDis;

    private Scroller scroller;
    private int mTouchSlop;

    private SmoothScrollRunnable mCurrentSmoothScrollRunnable;
    private Interpolator mScrollAnimationInterpolator;//滚动动画插入器
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

    /**
     * 获取底部刷新模式,见FooterRefreshMode
     *
     * @return FooterRefreshMode 底部刷新模式
     */
    public FooterRefreshMode getFooterRefreshMode() {
        return footerRefreshMode;
    }

    /**
     * 获取顶部刷新模式
     *
     * @return HeaderRefreshMode 顶部刷新模式
     */
    public HeaderRefreshMode getHeaderRefreshMode() {
        return headerRefreshMode;
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
        if(subHeaderView!=null){
            subHeaderView.refreshFinished(result);
        }
        if (listener != null && result != RefreshResult.failure) {
            listener.notifyDataSetChanged();
        }
        //延迟一定时间收起headerview
        postDelayed(new Runnable() {
            public void run() {
                if (getScrollY() <= 0) {
                    if(subHeaderView!=null){
                        headerView.setVisibility(VISIBLE);
                        subHeaderView.setVisibility(GONE);
                    }
                    smoothScrollTo(headerHeight);
                }
            }
        }, millis);
        //滑动完毕后才还原状态
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setHeaderState(RefreshState.ORIGIN_STATE);
            }
        }, millis + scrollDurationFactor * Math.abs(headerHeight - getScrollY()));
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
        setHeaderState(RefreshState.ORIGIN_STATE);
        if (getScrollY() <= 0) {
            scrollTo(0, headerHeight);
        }
        postDelayed(new Runnable() {
            public void run() {
                setHeaderState(RefreshState.REFRESHING);
                smoothScrollTo(0);
            }
        }, millis);
        //延时到滑动进行完毕才进行真正的刷新回调
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onHeaderRefreshListener != null) {
                    onHeaderRefreshListener.onHeaderRefresh();
                }
            }
        }, millis + scrollDurationFactor * Math.abs(0 - getScrollY()));
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
        footerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     *
     * @param footerRefreshMode footer刷新模式
     */
    public void setFooterEnable(FooterRefreshMode footerRefreshMode) {
        footerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_footer_height));
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
        footerLayoutParams = layoutParams;
        footerView.setLayoutParams(footerLayoutParams);
        if (contentView instanceof ListView) {
            FrameLayout frame = new FrameLayout(getContext());
            frame.addView(footerView);
            if (footerRefreshMode == FooterRefreshMode.AUTO) {
                footerView.setClickable(false);
                footerView.setFocusable(false);
                ((ListView)contentView).addFooterView(frame, null, false);
            } else {
                ((ListView) contentView).addFooterView(frame);
            }
        } else {
            addView(footerView);
        }
        footerView.originSate();
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度,默认下拉模式 HeaderRefreshMode.PULL
     */
    public void setHeaderEnable() {
        headerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度
     *
     * @param headerRefreshMode 刷新模式
     */
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        headerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_header_height));
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
        headerView = getHeaderView(headerRefreshMode);
        this.headerRefreshMode = headerRefreshMode;
        headerLayoutParams = layoutParams;
        headerView.setLayoutParams(layoutParams);
        addView(headerView, 0);
        headerView.originSate();
        if(contentView instanceof ListView){
            FrameLayout frame = new FrameLayout(getContext());
            subHeaderView = getHeaderView(headerRefreshMode);
            subHeaderView.setLayoutParams(layoutParams);
            subHeaderView.originSate();
            subHeaderView.setVisibility(GONE);
            frame.addView(subHeaderView);
            ((ListView)contentView).addHeaderView(frame,null,false);
        }
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
     * 中间内容view
     *
     * @return view
     */
    protected abstract T createContentView(AttributeSet attrs);

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
            scrollTo(0, headerHeight);
        }
        if (footerView != null && footerHeight == 0) {
            footerHeight = footerView.getHeight();
        }
        super.onLayout(changed, l, t, r, b);
    }

    private CustomView getFooterView(FooterRefreshMode footerRefreshMode) {
        if (footerRefreshMode == FooterRefreshMode.AUTO) {
            return new AutoLoadFooterViewI(getContext());
        } else if (footerRefreshMode == FooterRefreshMode.CLICK) {
            return new Click2LoadFooterViewI(getContext());
        } else if (footerRefreshMode == FooterRefreshMode.PULL) {
            //todo pull
            return null;
        }
        return new AutoLoadFooterViewI(getContext());
    }

    private CustomView getHeaderView(HeaderRefreshMode headerRefreshMode) {
        if (headerRefreshMode == HeaderRefreshMode.PULL) {
            return new Pull2RefreshHeaderViewI(getContext());
        }
        return new Pull2RefreshHeaderViewI(getContext());
    }

    private void init(AttributeSet attrs) {
        ViewConfiguration config = ViewConfiguration.get(getContext());
        mTouchSlop = config.getScaledTouchSlop();
        scroller = new Scroller(getContext());
        setOrientation(VERTICAL);
        mContentWrapper = new FrameLayout(getContext());
        mContentWrapper.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        contentView = createContentView(attrs);
        addView(mContentWrapper);
        if (contentView != null) {
            mContentWrapper.addView(contentView);
            post(new Runnable() {
                @Override
                public void run() {
                    mContentWrapper.setLayoutParams(new LinearLayout.LayoutParams(-1, getMeasuredHeight()));
                }
            });
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
                if(headerRefreshState == RefreshState.REFRESHING){
                    return false;
                }
                if (isContentViewAtTop() && dY > 0) {
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

    private boolean onTouchWhenHeaderRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (dY < 0 && -dY + getScrollY() > headerHeight) {
                    scrollTo(0, headerHeight);
                    return true;
                }
                if (headerRefreshState != RefreshState.REFRESHING) {
                    scrollBy(0, -dY / 3);
                } else {
                    if(getScrollY()-dY<=0){
                        scrollTo(0,0);
                    } else if(dY<0){
                        scrollBy(0, -dY);
                    } else {
                        scrollBy(0, -dY/3);
                    }
                }
                if (getScrollY() > headerHeight - canRefreshDis && headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.ORIGIN_STATE);
                } else if (getScrollY() < headerHeight - canRefreshDis
                        && headerRefreshState == RefreshState.ORIGIN_STATE) {
                    setHeaderState(RefreshState.CAN_REFRESH);
                }
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (headerRefreshState == RefreshState.CAN_REFRESH) {
                    setHeaderState(RefreshState.REFRESHING);
                    if(onHeaderRefreshListener != null) {
                        onHeaderRefreshListener.onHeaderRefresh();
                    }
                    smoothScrollTo(0);
                } else if(headerRefreshState != RefreshState.REFRESHING){
                    smoothScrollTo(headerHeight);
                }
        }
        return true;
    }

    private void setHeaderState(RefreshState state){
        switch (state){
            case CAN_REFRESH:
                headerView.canRefresh();
                if(subHeaderView!=null){
                    subHeaderView.canRefresh();
                }
                break;
            case ORIGIN_STATE:
                headerView.originSate();
                if(subHeaderView!=null){
                    subHeaderView.originSate();
                }
                break;
            case REFRESHING:
                headerView.refreshing();
                if(subHeaderView!=null){
                    headerView.setVisibility(GONE);
                    subHeaderView.refreshing();
                    subHeaderView.setVisibility(VISIBLE);
                }
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
    private void smoothScrollTo(int value, int delayMillis, OnSmoothScrollFinishedListener listener){
        switch (getRefreshableViewScrollDirection()) {
            case HORIZONTAL:
                smoothScrollTo(value, 0, Math.abs(value-getScrollX())*scrollDurationFactor, delayMillis, listener);
                break;
            case VERTICAL:
            default:
                smoothScrollTo(0, value, Math.abs(value - getScrollY()) * scrollDurationFactor, delayMillis, listener);
                break;
        }
    }

    private void smoothScrollTo(int value, int delayMillis){
        smoothScrollTo(value, delayMillis, null);
    }

    private void smoothScrollTo(int value){
        smoothScrollTo(value, 0);
    }

    private void smoothScrollTo(int x, int y, int duration, long delayMillis, OnSmoothScrollFinishedListener listener) {
        if (null != mCurrentSmoothScrollRunnable) {
            mCurrentSmoothScrollRunnable.stop();
        }
        if (getScrollX() != x || getScrollY() != y) {
            if (null == mScrollAnimationInterpolator) {
                // Default interpolator is a Decelerate Interpolator
                mScrollAnimationInterpolator = new DecelerateInterpolator();
            }
            mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(getScrollX(), getScrollY(), x, y, duration, listener);
            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(mCurrentSmoothScrollRunnable);
            }
        }
    }

    private void smoothScrollToAndBack(int y) {
        smoothScrollTo(y, 0, new OnSmoothScrollFinishedListener() {
            @Override
            public void onSmoothScrollFinished() {
                smoothScrollTo(0);
            }
        });
    }

    static interface OnSmoothScrollFinishedListener {
        void onSmoothScrollFinished();
    }

    public static enum Orientation {
        VERTICAL, HORIZONTAL;
    }

    protected abstract Orientation getRefreshableViewScrollDirection();

    /**
     * 平滑滚动,抄来的
     */
    private class SmoothScrollRunnable implements Runnable {
        private final Interpolator mInterpolator;
        private final int mStartX;
        private final int mStartY;
        private final int mStopX;
        private final int mStopY;
        private OnSmoothScrollFinishedListener mListener;
        private int mDuration;
        private boolean mContinueRunning = true;
        private long mStartTime = -1;
        private int mCurrentX = -1;
        private int mCurrentY = -1;

        public SmoothScrollRunnable(int startX, int startY, int stopX, int stopY, int duration, OnSmoothScrollFinishedListener listener) {
            mStartX = startX;
            mStartY = startY;
            mStopX = stopX;
            mStopY = stopY;
            mDuration = duration;
            mInterpolator = mScrollAnimationInterpolator;
            mListener = listener;
        }

        @Override
        public void run() {
            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                if(mDuration == 0){
                    scrollTo(mStopX, mStopY);
                    return;
                }
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
                int dX;
                int dY;
                if(mStartX - mStopX == 0){
                    dX = 0;
                } else {
                    dX = Math.round((mStartX - mStopX) * mInterpolator.getInterpolation(normalizedTime / 1000f));
                }
                if(mStartY - mStopY == 0){
                    dY = 0;
                } else {
                    dY = Math.round((mStartY - mStopY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
                }
                mCurrentX = mStartX - dX;
                mCurrentY = mStartY - dY;
                scrollTo(mCurrentX, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && (mStopY != mCurrentY || mStopX != mCurrentX)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    postOnAnimation(this);
                } else {
                    postDelayed(this, 16);
                }
            } else {
                scrollTo(mStopX, mStopY);
                if (null != mListener) {
                    mListener.onSmoothScrollFinished();
                }
            }
        }

        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }

    public FrameLayout getContentWrapper(){
        return mContentWrapper;
    }

}