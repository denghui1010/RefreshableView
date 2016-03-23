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
import com.huilan.refreshableview.smoothscroll.OnSmoothMoveListener;
import com.huilan.refreshableview.smoothscroll.SmoothScroller;
import com.huilan.refreshableview.weight.IRefreshable;

/**
 * 下拉刷新基类
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableLayout extends RelativeLayout {

    public final int scrollDurationFactor = 2;
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
    private RefreshState headerRefreshState = RefreshState.ORIGIN_STATE;
    /**
     * 底部刷新状态
     */
    private RefreshState footerRefreshState = RefreshState.ORIGIN_STATE;
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
     * 下拉阀值
     */
    private int canRefreshDis;
    /**
     * 滑动容差
     */
    private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    /**
     * 平滑滚动器
     */
    private SmoothScroller mSmoothScroller;

    private boolean needDispatchDownEvent = false;

    private int startScrollY;

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

    private void init() {
        mSmoothScroller = new SmoothScroller(this);
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
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起footerview,请将刷新适配器的方法写在监听中
     *
     * @param result 刷新结果
     * @param millis 延迟毫秒值
     */
    public void notifyFooterRefreshFinished(final RefreshResult result, final int millis) {
        if (footerRefreshMode == FooterRefreshMode.CLOSE) {
            return;
        }
        footerView.onFinished(result);
        //延迟一定时间收起footerView
        postDelayed(new Runnable() {
            public void run() {
                if (result == RefreshResult.nomore) {
                    setFooterState(RefreshState.NO_MORE);
                } else {
                    setFooterState(RefreshState.ORIGIN_STATE);
                }

            }
        }, millis);
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
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,1秒后会隐藏headerview,请将刷新适配器的方法写在监听中
     *
     * @param result 刷新结果
     */
    public void notifyHeaderRefreshFinished(RefreshResult result) {
        notifyHeaderRefreshFinished(result, 1000);
    }

    /**
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,延迟一定时间后收起headererview,请将刷新适配器的方法写在监听中
     *
     * @param result 刷新结果
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshFinished(final RefreshResult result, final int millis) {
        if (headerRefreshMode == HeaderRefreshMode.CLOSE) {
            return;
        }
        headerView.onFinished(result);
        //回到原位
        mSmoothScroller.smoothScrollTo(0, 0, millis, new OnSmoothMoveListener() {
            @Override
            public void onSmoothScrollFinished() {
                //滑动完毕后才还原状态
                setHeaderState(RefreshState.ORIGIN_STATE);
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
        mSmoothScroller.smoothScrollTo(0, 0, millis, new OnSmoothMoveListener() {
            @Override
            public void onSmoothScrollFinished() {
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
        setFooterEnable(FooterRefreshMode.AUTO);
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
            addView(footerView);
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
            case CAN_REFRESH:
                footerView.onStart();
                break;
            case ORIGIN_STATE:
                footerView.onPrepare();
                break;
            case REFRESHING:
                footerView.onRefreshing();
                break;
            case NO_MORE:
                footerView.setFocusable(false);
                footerView.setClickable(false);
                footerView.onFinished(RefreshResult.nomore);
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
            case CAN_REFRESH:
                headerView.onStart();
                break;
            case ORIGIN_STATE:
                headerView.onPrepare();
                break;
            case REFRESHING:
                headerView.onRefreshing();
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

    public void setOverScrollEnable(boolean enable){
        if(enable){

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
//                ((ListView) contentView).setOnScrollListener(new AbsListView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                    }
//
//                    @Override
//                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                        if(!mSmoothScroller.isRunning()) {
//                            if (((IRefreshable) contentView).canPullUp()) {
//                                mSmoothScroller.smoothScrollTo(0, 100, 0, new OnSmoothMoveListener() {
//                                    @Override
//                                    public void onSmoothScrollFinished() {
//                                        mSmoothScroller.smoothScrollTo(0, 0, 0, null);
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });
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
                mSmoothScroller.stop();
                startX = event.getRawX();
                startY = event.getRawY();
                needInterrupt = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = event.getRawY();
                float currX = event.getRawX();
                float dX = currX - startX;
                float dY = currY - startY;
                if (!((IRefreshable) contentView).canPullDown()) {
                    //列表不在顶端,不拦截
                    needInterrupt = false;
                } else {
                    //列表在顶端
                    if (dY > 0) {
                        //向下滑动,需要拦截
                        needInterrupt = true;
                        if (headerRefreshState == RefreshState.ORIGIN_STATE || headerRefreshState == RefreshState.CAN_REFRESH) {
                            //如果处于下拉刷新或松手刷新状态,下拉距离越大,阻力越大
                            double scaleDY = Math.pow(dY * dY, 1.3 / 4);
//                        System.out.println("dy=" + dY + ", scaleY=" + scaleDY);
                            scrollBy(0, (int) (-scaleDY));
                        } else {
                            scrollBy(0, (int) (-dY));
                        }
                        if (headerRefreshState == RefreshState.ORIGIN_STATE && -getScrollY() > canRefreshDis) {
                            //达到松手刷新的条件,更改状态
                            setHeaderState(RefreshState.CAN_REFRESH);
                        }
                        if (headerRefreshState == RefreshState.CAN_REFRESH && -getScrollY() < canRefreshDis) {
                            //达到还原的条件,还原状态
                            setHeaderState(RefreshState.ORIGIN_STATE);
                        }
                    } else {
                        //上滑
                        needInterrupt = true;
                        if (-getScrollY() > 0) {
                            //header显示了的话,需要拦截,不需要阻力
                            needInterrupt = true;
                            scrollBy(0, (int) (-dY));
                        } else {
                            //header没有显示,不需要拦截
                            needInterrupt = false;
                        }
                    }
                }
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                //如果处于正在刷新,且下滑距离超过下拉条件,更改状态,需要回滚到正在刷新的标准距离
                if (headerRefreshState == RefreshState.CAN_REFRESH && -getScrollY() > canRefreshDis) {
                    setHeaderState(RefreshState.REFRESHING);
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onHeaderRefresh();
                    }
                    mSmoothScroller.smoothScrollTo(0, -headerHeight, 0, null);
                } else {
                    //如果不是正在刷新,需要回滚到初始位置
                    mSmoothScroller.smoothScrollTo(0, 0, 0, null);
                }

                break;
        }
        if (!needInterrupt) {
//            if(needDispatchDownEvent){
//                event.setAction(MotionEvent.ACTION_DOWN);
//            }
            super.dispatchTouchEvent(event);
        } else {
            event.setAction(MotionEvent.ACTION_CANCEL);
            super.dispatchTouchEvent(event);
        }
        System.out.println("needInterrupt=" + needInterrupt);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            initContentView();
            if (headerView != null) {
                headerView.measure(0, 0);
                headerHeight = headerView.getMeasuredHeight();
                canRefreshDis = headerHeight;
            }
            if (footerView != null) {
                footerView.measure(0, 0);
                footerHeight = footerView.getMeasuredHeight();
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

}