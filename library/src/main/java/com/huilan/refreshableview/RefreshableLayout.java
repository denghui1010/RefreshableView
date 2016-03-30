package com.huilan.refreshableview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.RotateHeaderView;
import com.huilan.refreshableview.weight.IRefreshable;

/**
 * 下拉刷新布局
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableLayout extends RelativeLayout {
    /**
     * 顶部刷新View
     */
    private CustomView headerView;
    /**
     * 底部刷新View
     */
    private CustomView footerView;
    /**
     * 刷新监听器
     */
    private OnRefreshListener mOnRefreshListener;
    /**
     * 顶部刷新模式
     */
    private HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    /**
     * 底部刷新模式
     */
    private FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;
    /**
     * 顶部刷新状态
     */
    private RefreshState headerRefreshState = RefreshState.PREPARE;
    /**
     * 底部刷新状态
     */
    private RefreshState footerRefreshState = RefreshState.PREPARE;
    /**
     * headerView高度
     */
    private int headerHeight;
    /**
     * footerView高度
     */
    private int footerHeight;
    /**
     * 开始X轴坐标
     */
    private float startX;
    /**
     * 开始Y轴坐标
     */
    private float startY;
    /**
     * 中间主View
     */
    private View contentView;
    /**
     * HeaderView下拉阀值
     */
    private int headerRefreshDis;
    /**
     * FooterView上拉阈值
     */
    private int footerRefreshDis;
    /**
     * 滑动容差
     */
    private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    /**
     * 平滑滚动器
     */
    private SmoothScroller mSmoothScroller;
    /**
     * 顶部阴影View
     */
    private ShadowView mTopShadowView;
    /**
     * 底部阴影View
     */
    private ShadowView mBottomShadowView;

    private boolean mAutoRefresh;

    public RefreshableLayout(Context context) {
        super(context);
        init();
    }

    public RefreshableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RefreshableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public boolean isAutoRefresh() {
        return mAutoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        mAutoRefresh = autoRefresh;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mAutoRefresh) {
            startHeaderRefresh();
        }
    }

    /**
     * 开始头部刷新
     */
    public void startHeaderRefresh() {
        if (headerRefreshMode != HeaderRefreshMode.CLOSE) {
            setHeaderState(RefreshState.REFRESHING);
            mSmoothScroller.smoothScrollTo(this, 0, -headerHeight, 500, new SmoothScroller.onSmoothScrollListenerAdapter() {
                @Override
                public void onSmoothScrollEnd() {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onHeaderRefresh();
                    }
                }
            });
        }
    }

    /**
     * 设置阴影颜色
     *
     * @param lightColor 浅色,结尾色
     * @param deepColor  深色,开始色
     */
    public void setContentViewShadowColor(int lightColor, int deepColor) {
        if (mTopShadowView != null) {
            mTopShadowView.setShadowColor(lightColor, deepColor);
        }
        if (mBottomShadowView != null) {
            mBottomShadowView.setShadowColor(lightColor, deepColor);
        }
    }

    /**
     * 设置contentView的阴影效果是否开启,开启后会自动在contentView的上方和下方形成阴影效果,开启边界回弹时有效
     *
     * @param top    是否开启顶部阴影
     * @param bottom 是否开启底部阴影
     */
    public void setContentViewShadowEnable(boolean top, boolean bottom) {
        if (top) {
            mTopShadowView = new ShadowView(getContext());
            mTopShadowView.setDirection(ShadowView.Direction.Top);
            addView(mTopShadowView);
        } else {
            mTopShadowView = null;
        }
        if (bottom) {
            mBottomShadowView = new ShadowView(getContext());
            mBottomShadowView.setDirection(ShadowView.Direction.Bottom);
            addView(mBottomShadowView);
        } else {
            mBottomShadowView = null;
        }
    }

    /**
     * 设置阴影的高度基准,高度越大,阴影颜色越深,阴影面基越大
     *
     * @param topElevation    顶部阴影的高度基准
     * @param bottomElevation 底部阴影的高度基准
     */
    public void setContentViewShadowElevation(int topElevation, int bottomElevation) {
        if (mTopShadowView != null) {
            mTopShadowView.setElevation(topElevation);
        }
        if (mBottomShadowView != null) {
            mBottomShadowView.setElevation(bottomElevation);
        }
    }

    private void init() {
        mSmoothScroller = new SmoothScroller();
    }

    /**
     * 获取刷新监听
     *
     * @return 刷新监听
     */
    public OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    /**
     * 设置刷新监听
     *
     * @param onRefreshListener 刷新监听
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * 获取中间区域
     *
     * @return 中间区域的view
     */
    public View getContentView() {
        return contentView;
    }

    /**
     * 获取底部刷新模式,@see{#FooterRefreshMode}
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
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起footerView,延迟最低不会低于500ms
     *
     * @param result 刷新结果
     * @param millis 延迟毫秒值
     */
    public void notifyFooterRefreshFinished(RefreshResult result, int millis) {
        millis = Math.max(500, millis);
        if (footerRefreshMode == FooterRefreshMode.CLOSE) {
            return;
        }
        footerView.onFinished(result);
        setFooterState(RefreshState.FINISHED);
        //延迟一定时间收起footerView
        mSmoothScroller.smoothScrollTo(this, 0, 0, millis, new SmoothScroller.onSmoothScrollListenerAdapter() {
            @Override
            public void onSmoothScrollEnd() {
                //滑动完毕后才还原状态
                System.out.println("还原状态");
                setFooterState(RefreshState.PREPARE);
            }
        });
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏footerView
     *
     * @param result 刷新结果
     */
    public void notifyFooterRefreshFinished(RefreshResult result) {
        notifyFooterRefreshFinished(result, 1000);
    }

    /**
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,1秒后会隐藏headerView
     *
     * @param result 刷新结果
     */
    public void notifyHeaderRefreshFinished(RefreshResult result) {
        notifyHeaderRefreshFinished(result, 1000);
    }

    /**
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,延迟一定时间后收起headerView,延迟最低不会低于500ms
     *
     * @param result 刷新结果
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshFinished(RefreshResult result, int millis) {
        millis = Math.max(500, millis);
        if (headerRefreshMode == HeaderRefreshMode.CLOSE) {
            return;
        }
        headerView.onFinished(result);
        setHeaderState(RefreshState.FINISHED);
        //回到原位
        mSmoothScroller.smoothScrollTo(this, 0, 0, millis, new SmoothScroller.onSmoothScrollListenerAdapter() {
            @Override
            public void onSmoothScrollEnd() {
                //滑动完毕后才还原状态
                setHeaderState(RefreshState.PREPARE);
            }
        });
    }

    /**
     * 进入刷新状态,延迟300毫秒
     */
    public void notifyHeaderRefreshStarted() {
        notifyHeaderRefreshStarted(300);
    }

    /**
     * 进入刷新状态,延迟一定时间
     *
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshStarted(final int millis) {
        if (headerRefreshMode == HeaderRefreshMode.CLOSE) {
            return;
        }
        if (!((IRefreshable) contentView).canPullDown() || getScrollY() != headerHeight) {
            return;
        }
        setHeaderState(RefreshState.REFRESHING);
        mSmoothScroller.smoothScrollTo(this, 0, 0, millis, new SmoothScroller.onSmoothScrollListenerAdapter() {
            @Override
            public void onSmoothScrollEnd() {
                //延时到滑动进行完毕才进行真正的刷新回调
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onHeaderRefresh();
                }
            }
        });
    }

    /**
     * 设置开启上拉刷新,默认上拉模式 FooterRefreshMode.AUTO
     */
    public void setFooterEnable() {
        setFooterEnable(FooterRefreshMode.PULL);
    }

    /**
     * 设置开启上拉刷新
     *
     * @param footerRefreshMode 刷新模式,FooterRefreshMode
     */
    public void setFooterEnable(FooterRefreshMode footerRefreshMode) {
        if (footerRefreshMode != FooterRefreshMode.CLOSE) {
            footerView = getFooterView(footerRefreshMode);
            this.footerRefreshMode = footerRefreshMode;
            addView(footerView, 0);
            footerView.onPrepare();
        }
    }

    /**
     * 设置footerView的状态
     *
     * @param state 状态
     */
    public void setFooterState(RefreshState state) {
        if (footerRefreshMode == FooterRefreshMode.CLOSE) {
            return;
        }
        switch (state) {
            case RELEASE2REFRESH:
                footerView.onStart();
                break;
            case PREPARE:
                footerView.onPrepare();
                break;
            case REFRESHING:
                footerView.onRefreshing();
                break;
            case FINISHED:
                break;
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
     * @param headerRefreshMode 刷新模式,见HeaderRefreshMode
     */
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        headerView = getHeaderView(headerRefreshMode);
        headerView.onPrepare();
        this.headerRefreshMode = headerRefreshMode;
        addView(headerView, 0);
        headerView.onPrepare();

    }

    /**
     * 设置headerView的状态
     *
     * @param state 状态
     */
    public void setHeaderState(RefreshState state) {
        if (headerRefreshMode == HeaderRefreshMode.CLOSE) {
            return;
        }
        switch (state) {
            case RELEASE2REFRESH:
                headerView.onStart();
                break;
            case PREPARE:
                headerView.onPrepare();
                break;
            case REFRESHING:
                headerView.onRefreshing();
                break;
            case FINISHED:
                break;
        }
        headerRefreshState = state;
    }

    protected CustomView getFooterView(FooterRefreshMode footerRefreshMode) {
        if (footerRefreshMode == FooterRefreshMode.AUTO) {
            return new AutoLoadFooterView(getContext());
        } else if (footerRefreshMode == FooterRefreshMode.CLICK) {
            return new Click2LoadFooterView(getContext());
        } else if (footerRefreshMode == FooterRefreshMode.PULL) {
            return new com.huilan.refreshableview.footerview.RotateHeaderView(getContext());
        }
        return new AutoLoadFooterView(getContext());
    }

    protected CustomView getHeaderView(HeaderRefreshMode headerRefreshMode) {
        if (headerRefreshMode == HeaderRefreshMode.PULL) {
            return new RotateHeaderView(getContext());
        }
        return new RotateHeaderView(getContext());
    }

    @Override
    public void setOverScrollMode(int overScrollMode) {
//        super.setOverScrollMode(overScrollMode);
        if (overScrollMode == OVER_SCROLL_ALWAYS) {

        } else if (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS) {

        } else if (overScrollMode == OVER_SCROLL_NEVER) {

        }
    }

    /**
     * 初始化
     */
    private void initContentView() {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof IRefreshable) {
                contentView = v;
                ((IRefreshable) contentView).setOnOverScrollListener(new IntervalOnOverScrollListener());
            }
        }
        if (contentView == null) {
            throw new RuntimeException("RefreshableLayout must has a IRefreshableView");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean needInterrupt = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSmoothScroller.cancel();
                startX = event.getRawX();
                startY = event.getRawY();
                needInterrupt = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = event.getRawY();
                float currX = event.getRawX();
                float dX = currX - startX;
                float dY = currY - startY;
                startY = currY;
                if (((IRefreshable) contentView).canPullDown() && ((IRefreshable) contentView).canPullUp()) {
                    //下拉并且符合拉出HeaderView的条件,需要拦截
//                    System.out.println("1" + ",dy=" + dY + ",getScrollY=" + getScrollY() + ",needInterrupt=" + needInterrupt);
                    if (dY > 0 || (-getScrollY() > 0 && headerRefreshState != RefreshState.REFRESHING)) {
                        needInterrupt = true;
                        //下拉距离越大,阻力越大
                        dY = (float) Math.pow(dY * dY, 1.3 / 4) * dY / Math.abs(dY);
                        if (footerRefreshState == RefreshState.REFRESHING && (-getScrollY() + dY) > 0) {
                            scrollTo(0, 0);
                        } else {
                            scrollBy(0, (int) -dY);
                        }
                        if ((headerRefreshState == RefreshState.PREPARE || headerRefreshState == RefreshState.FINISHED) && -getScrollY() > headerRefreshDis) {
                            //达到松手刷新的条件,更改状态
                            setHeaderState(RefreshState.RELEASE2REFRESH);
                        }
                        if (headerRefreshState == RefreshState.RELEASE2REFRESH && -getScrollY() < headerRefreshDis) {
                            //达到还原的条件,还原状态
                            setHeaderState(RefreshState.PREPARE);
                        }
                        if ((footerRefreshState == RefreshState.PREPARE || footerRefreshState == RefreshState.FINISHED) && getScrollY() > footerRefreshDis) {
                            //达到松手刷新的条件,更改状态
                            setFooterState(RefreshState.RELEASE2REFRESH);
                        }
                        if (footerRefreshState == RefreshState.RELEASE2REFRESH && getScrollY() < footerRefreshDis) {
                            //达到还原的条件,还原状态
                            setFooterState(RefreshState.PREPARE);
                        }
                    } else if (((IRefreshable) contentView).canPullUp()) {
                        //上拉并且符合拉出FooterView的条件,需要拦截
//                        System.out.println("2" + ",dy=" + dY + ",getScrollY=" + getScrollY() + ",needInterrupt=" + needInterrupt);
                        if (dY < 0 || (getScrollY() > 0 && footerRefreshState != RefreshState.REFRESHING)) {
                            needInterrupt = true;
                            //下拉距离越大,阻力越大
                            dY = (float) Math.pow(dY * dY, 1.3 / 4) * dY / Math.abs(dY);
                            if (headerRefreshState == RefreshState.REFRESHING && (-getScrollY() + dY) < 0) {
                                scrollTo(0, 0);
                            } else {
                                scrollBy(0, (int) -dY);
                            }
                            if ((footerRefreshState == RefreshState.PREPARE || footerRefreshState == RefreshState.FINISHED) && getScrollY() > footerRefreshDis) {
                                //达到松手刷新的条件,更改状态
                                setFooterState(RefreshState.RELEASE2REFRESH);
                            }
                            if (footerRefreshState == RefreshState.RELEASE2REFRESH && getScrollY() < footerRefreshDis) {
                                //达到还原的条件,还原状态
                                setFooterState(RefreshState.PREPARE);
                            }
                            if ((headerRefreshState == RefreshState.PREPARE || headerRefreshState == RefreshState.FINISHED) && -getScrollY() > headerRefreshDis) {
                                //达到松手刷新的条件,更改状态
                                setHeaderState(RefreshState.RELEASE2REFRESH);
                            }
                            if (headerRefreshState == RefreshState.RELEASE2REFRESH && -getScrollY() < headerRefreshDis) {
                                //达到还原的条件,还原状态
                                setHeaderState(RefreshState.PREPARE);
                            }
                        }
                    } else {
                        needInterrupt = false;
                    }
                } else if (((IRefreshable) contentView).canPullDown()) {
                    //下拉并且符合拉出HeaderView的条件,需要拦截
//                    System.out.println("1" + ",dy=" + dY + ",getScrollY=" + getScrollY() + ",needInterrupt=" + needInterrupt);
                    if (dY > 0 || (-getScrollY() > 0 && headerRefreshState != RefreshState.REFRESHING)) {
                        needInterrupt = true;
                        //下拉距离越大,阻力越大
                        dY = (float) Math.pow(dY * dY, 1.3 / 4) * dY / Math.abs(dY);
                        if (footerRefreshState == RefreshState.REFRESHING && (-getScrollY() + dY) > 0) {
                            scrollTo(0, 0);
                        } else {
                            scrollBy(0, (int) -dY);
                        }
                        if ((headerRefreshState == RefreshState.PREPARE || headerRefreshState == RefreshState.FINISHED) && -getScrollY() > headerRefreshDis) {
                            //达到松手刷新的条件,更改状态
                            setHeaderState(RefreshState.RELEASE2REFRESH);
                        }
                        if (headerRefreshState == RefreshState.RELEASE2REFRESH && -getScrollY() < headerRefreshDis) {
                            //达到还原的条件,还原状态
                            setHeaderState(RefreshState.PREPARE);
                        }
                    } else {
                        needInterrupt = false;
                    }
                } else if (((IRefreshable) contentView).canPullUp()) {
                    //上拉并且符合拉出FooterView的条件,需要拦截
//                    System.out.println("2" + ",dy=" + dY + ",getScrollY=" + getScrollY() + ",needInterrupt=" + needInterrupt);
                    if (dY < 0 || (getScrollY() > 0 && footerRefreshState != RefreshState.REFRESHING)) {
                        needInterrupt = true;
                        //下拉距离越大,阻力越大
                        dY = (float) Math.pow(dY * dY, 1.3 / 4) * dY / Math.abs(dY);
                        if (headerRefreshState == RefreshState.REFRESHING && (-getScrollY() + dY) < 0) {
                            scrollTo(0, 0);
                        } else {
                            scrollBy(0, (int) -dY);
                        }
                        if ((footerRefreshState == RefreshState.PREPARE || footerRefreshState == RefreshState.FINISHED) && getScrollY() > footerRefreshDis) {
                            //达到松手刷新的条件,更改状态
                            setFooterState(RefreshState.RELEASE2REFRESH);
                        }
                        if (footerRefreshState == RefreshState.RELEASE2REFRESH && getScrollY() < footerRefreshDis) {
                            //达到还原的条件,还原状态
                            setFooterState(RefreshState.PREPARE);
                        }
                    } else {
                        needInterrupt = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                needInterrupt = false;
                if (headerRefreshState == RefreshState.RELEASE2REFRESH) {
                    //如果处于初始状态,且下滑距离超过下拉条件,更改状态,回滚到显示HeaderView的位置
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onHeaderRefresh();
                    }
                    System.out.println("11");
                    mSmoothScroller.smoothScrollTo(this, 0, -headerHeight, 0, new SmoothScroller.onSmoothScrollListenerAdapter() {
                        @Override
                        public void onSmoothScrollStart() {
                            setHeaderState(RefreshState.REFRESHING);
                        }
                    });
                    break;
                }
                if (footerRefreshState == RefreshState.RELEASE2REFRESH) {
                    //如果处于初始状态,且上滑距离超过上拉条件,更改状态,回滚到显示FooterView的位置
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onFooterRefresh();
                    }
                    System.out.println("12");
                    mSmoothScroller.smoothScrollTo(this, 0, footerHeight, 0, new SmoothScroller.onSmoothScrollListenerAdapter() {
                        @Override
                        public void onSmoothScrollStart() {
                            setFooterState(RefreshState.REFRESHING);
                        }
                    });
                    break;
                }
                if (headerRefreshState == RefreshState.REFRESHING) {
                    //如果Header正在刷新,回滚到显示HeaderView的位置
                    System.out.println("13");
                    mSmoothScroller.smoothScrollTo(this, 0, -headerHeight, 0, null);
                    break;
                }
                if (footerRefreshState == RefreshState.REFRESHING) {
                    //如果Footer正在刷新,回滚到显示FooterView的位置
                    System.out.println("14");
                    mSmoothScroller.smoothScrollTo(this, 0, footerHeight, 0, null);
                    break;
                }
                if (getScrollY() != 0) {
                    System.out.println("15");
                    //如果不是正在刷新且位置不正常,需要回滚到初始位置
                    mSmoothScroller.smoothScrollTo(this, 0, 0, 0, null);
                    break;
                }
                break;
        }
//        System.out.println("needInterrupt=" + needInterrupt);
        if (needInterrupt) {
            event.setAction(MotionEvent.ACTION_CANCEL);
            super.dispatchTouchEvent(event);
        } else {
            super.dispatchTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            initContentView();
            if (headerView != null) {
                headerHeight = headerView.getMeasuredHeight();
                headerRefreshDis = headerHeight;
            }
            if (footerView != null) {
                footerHeight = footerView.getMeasuredHeight();
                footerRefreshDis = footerHeight;
            }
        }
        if (headerView != null) {
            headerView.layout(0, -headerView.getMeasuredHeight(), headerView.getMeasuredWidth(), 0);
        }
        if (contentView != null) {
            contentView.layout(0, 0, contentView.getMeasuredWidth(), contentView.getMeasuredHeight());
        }
        if (footerView != null && contentView != null) {
            footerView.layout(0, contentView.getMeasuredHeight(), footerView.getMeasuredWidth(), contentView.getMeasuredHeight() + footerView.getMeasuredHeight());
        }
        if (getOverScrollMode() != OVER_SCROLL_NEVER) {
            if (mTopShadowView != null) {
                mTopShadowView.layout(0, -mTopShadowView.getMeasuredHeight(), mTopShadowView.getMeasuredWidth(), 0);
            }
            if (mBottomShadowView != null) {
                mBottomShadowView.layout(0, contentView.getMeasuredHeight(), mBottomShadowView.getMeasuredWidth(), contentView.getMeasuredHeight() + mBottomShadowView.getMeasuredHeight());
            }
        }
    }


    /**
     * 刷新状态
     * Created by liudenghui on 14-7-30.
     */
    public enum RefreshState {
        PREPARE,
        RELEASE2REFRESH,
        REFRESHING,
        FINISHED
    }

    /**
     * 状态进程监听
     */
    public interface OnProcessChangeListener {
        /**
         * 准备 (设置初始状态)
         */
        void onPrepare();

        /**
         * 开始 (提示达到刷新条件)
         */
        void onStart();

        /**
         * 刷新中 (提示刷新中)
         */
        void onRefreshing();

        /**
         * 刷新完成 (提示刷新完成)
         */
        void onFinished(RefreshResult result);

        /**
         * 用于获取拉取的距离
         *
         * @param pullDistance      拉取距离
         * @param conditionDistance 条件距离
         */
        void onPull(int pullDistance, int conditionDistance);
    }

    /**
     * 刷新加载回调接口
     *
     * @author chenjing
     */
    public interface OnRefreshListener {
        /**
         * 顶部刷新
         */
        void onHeaderRefresh();

        /**
         * 底部刷新
         */
        void onFooterRefresh();
    }

    private class IntervalOnOverScrollListener implements IRefreshable.OnOverScrollListener {
        @Override
        public void onOverScroll(int dx, int dy) {
            if (getOverScrollMode() == OVER_SCROLL_NEVER) {
                return;
            }
            if (dy < 0) {
                dy = Math.max(3 * dy / 4, -80);
//                final int duration = (int) (-0.000066 * dy * dy * dy - 0.0155 * dy * dy - 1.245 * dy + 135.77);
                final int duration = 167;
                System.out.println("上方边界回弹" + ",dy=" + dy + ",getScrollY" + getScrollY() + ",duration=" + duration);
                if (footerRefreshState == RefreshState.REFRESHING || headerRefreshState == RefreshState.REFRESHING) {
                    dy = dy + getScrollY();
                }
                mSmoothScroller.smoothScrollTo(RefreshableLayout.this, 0, dy, duration, 0, new SmoothScroller.onSmoothScrollListenerAdapter() {
                    @Override
                    public void onSmoothScrollEnd() {
                        int y = 0;
                        if (headerRefreshState == RefreshState.REFRESHING) {
                            y = -headerHeight;
                        } else if (footerRefreshState == RefreshState.REFRESHING) {
                            y = footerHeight;
                        }
                        mSmoothScroller.smoothScrollTo(RefreshableLayout.this, 0, y, duration, 0, null);
                    }
                });
            } else {
                dy = Math.min(3 * dy / 4, 80);
//                final int duration = (int) (0.000066 * dy * dy * dy - 0.0155 * dy * dy + 1.245 * dy + 135.77);
                final int duration = 167;
                System.out.println("下边界回弹" + ",dy=" + dy + ",getScrollY" + getScrollY() + ",duration=" + duration);
                if (footerRefreshState == RefreshState.REFRESHING || headerRefreshState == RefreshState.REFRESHING) {
                    dy = dy + getScrollY();
                }
                mSmoothScroller.smoothScrollTo(RefreshableLayout.this, 0, dy, duration, 0, new SmoothScroller.onSmoothScrollListenerAdapter() {
                    @Override
                    public void onSmoothScrollEnd() {
                        int y = 0;
                        if (footerRefreshState == RefreshState.REFRESHING) {
                            y = footerHeight;
                        } else if (headerRefreshState == RefreshState.REFRESHING) {
                            y = -headerHeight;
                        }
                        mSmoothScroller.smoothScrollTo(RefreshableLayout.this, 0, y, duration, 0, null);
                    }
                });
            }
        }
    }


}