package com.huilan.refreshableview;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.Pull2RefreshHeaderView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liudenghui on 14-7-29.
 */
public abstract class RefreshableBase<T extends View> extends LinearLayout {

    public static final String LOG_TAG = "RefreshableView";
    public final int scrollDurationFactor = 4;

    protected CustomView headerView;
    protected CustomView footerView;

    protected AbsListView.LayoutParams headerLayoutParams;
    protected AbsListView.LayoutParams footerLayoutParams;

    protected OnHeaderRefreshListener onHeaderRefreshListener;
    protected OnFooterRefreshListener onFooterRefreshListener;

    protected HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    protected FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;

    protected RefreshState headerRefreshState = RefreshState.ORIGIN_STATE;
    protected RefreshState footerRefreshState = RefreshState.ORIGIN_STATE;

    protected int headerHeight;
    protected int footerHeight;
    private int startY;
    private T contentView;
    private boolean requireInterupt;
    private int canRefreshDis;

    private Scroller scroller;
    private OnSmoothScrollListener mListener;
    private long mLastHeaderTime;


    public RefreshableBase(Context context) {
        super(context);
        init();
    }

    public RefreshableBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RefreshableBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
            if(scroller.isFinished() && mListener != null){
                mListener.onSmoothScrollFinish();
                mListener = null;
            }
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
    public void notifyFooterRefreshFinished(RefreshResult result, final BaseAdapter adapter, int millis) {
        this.footerView.setWillNotDraw(true);
        this.footerView.refreshFinished(result);
        postDelayed(new Runnable() {
            public void run() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                footerRefreshState = RefreshState.ORIGIN_STATE;
                footerView.originSate();
            }
        }, millis);
    }

    /**
     * 通知底部刷新完毕,0延迟
     * @param result
     */
    public void notifyFooterRefreshFinished(RefreshResult result) {
        notifyFooterRefreshFinished(result, null, 0);
    }

    @Deprecated
    public void notifyFooterRefreshFinished(RefreshResult result, BaseAdapter adapter) {
        notifyFooterRefreshFinished(result, adapter, 500);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏footerview,请使用带适配器参数的方法以获得更好的效果
     *
     * @param result 刷新结果
     */
    @Deprecated
    public void notifyHeaderRefreshFinished(RefreshResult result) {
        notifyHeaderRefreshFinished(result, 1000);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏footerview,该方法会自动刷新适配器,无需额外的手动调用
     *
     * @param result 刷新结果
     * @param adapter 需要刷新的适配器
     */
    public void notifyHeaderRefreshFinished(RefreshResult result, BaseAdapter adapter) {
        notifyHeaderRefreshFinished(result, 1000);
    }


    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起headererview,请使用带适配器参数的方法以获得更好的效果
     * @param result 刷新结果
     * @param millis 延迟毫秒值
     */
    @Deprecated
    public void notifyHeaderRefreshFinished(final RefreshResult result, int millis) {
        notifyHeaderRefreshFinished(result, null, millis);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,延迟一定时间后收起headererview,该方法会自动刷新适配器,无需额外的手动调用
     * @param result 刷新结果
     * @param adapter 需要刷新的适配器
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshFinished(final RefreshResult result, final BaseAdapter adapter, int millis){
        long l = System.currentTimeMillis() - mLastHeaderTime;
        if(l<800){
            l = 1000;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                headerView.refreshFinished(result);
                if(adapter!= null){
                    adapter.notifyDataSetChanged();
                }
            }
        }, l);
//        headerView.refreshFinished(result);
        postDelayed(new Runnable() {
            public void run() {
                if (getScrollY() <= 0) {
                    mListener = new OnSmoothScrollListener() {
                        @Override
                        public void onSmoothScrollStart() {

                        }

                        @Override
                        public void onSmoothScrollFinish() {
                            headerRefreshState = RefreshState.ORIGIN_STATE;
                            headerView.originSate();
                        }
                    };
                    smoothScrollTo(0, headerHeight, mListener);
                }
            }
        }, l+millis);
    }

    /**
     * 通知refreshview进入刷新状态,延迟300毫秒
     */
    public void notifyHeaderRefreshStarted() {
        notifyHeaderRefreshStarted(300);
    }

    /**
     * 通知refreshview进入刷新状态,延迟一定时间
     * @param millis 延迟毫秒值
     */
    public void notifyHeaderRefreshStarted(int millis) {
        headerRefreshState = RefreshState.ORIGIN_STATE;
        headerView.originSate();
        if (getScrollY() <= 0) {
            scrollTo(0, headerHeight);
        }
        postDelayed(new Runnable() {
            public void run() {
                mLastHeaderTime = System.currentTimeMillis();
                headerRefreshState = RefreshState.REFRESHING;
                headerView.refreshing();
                if (onHeaderRefreshListener != null) {
                    onHeaderRefreshListener.onHeaderRefresh();
                }
                smoothScrollTo(0, 0, null);
            }
        }, millis);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                requireInterupt = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (isContentViewAtTop() && dY > 0) {
                    requireInterupt = true;
                }
                if (headerRefreshState == RefreshState.REFRESHING) {
                    requireInterupt = true;
                }
                startY = currY;
                break;
        }
        return requireInterupt;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (headerRefreshMode != HeaderRefreshMode.CLOSE) {
            return onTouchWhenHeaderRefreshEnable(event);
        }
        if (footerRefreshMode != FooterRefreshMode.CLOSE) {
            onTouchWhenFooterRefreshEnable(event);
        }
        return true;
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     */
    public void setFooterEnable() {
        footerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     *
     * @param footerRefreshMode footer刷新模式
     */
    public void setFooterEnable(FooterRefreshMode footerRefreshMode) {
        footerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, footerRefreshMode);
    }

    /**
     * 设置开启上拉刷新,默认上拉模式为 FooterRefreshMode.AUTO
     *
     * @param layoutParams 上拉view的layoutparams
     */
    public void setFooterEnable(AbsListView.LayoutParams layoutParams) {
        setFooterEnable(layoutParams, FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新
     *
     * @param layoutParams      上拉view的layoutparams
     * @param footerRefreshMode 刷新模式,FooterRefreshMode
     */
    public void setFooterEnable(AbsListView.LayoutParams layoutParams, FooterRefreshMode footerRefreshMode) {
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
            if (footerRefreshMode == FooterRefreshMode.AUTO) {
                ((ListView) this.contentView).addFooterView(footerView, null, false);
            } else {
                ((ListView) this.contentView).addFooterView(footerView);
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
        headerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度
     *
     * @param headerRefreshMode 刷新模式
     */
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        headerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, headerRefreshMode);
    }

    /**
     * 设置开启下拉刷新,默认下拉模式为 HeaderRefreshMode.PULL
     *
     * @param layoutParams 下拉view的layoutparams
     */
    public void setHeaderEnable(AbsListView.LayoutParams layoutParams) {
        setHeaderEnable(layoutParams, HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新
     *
     * @param layoutParams      下拉view的layoutparams
     * @param headerRefreshMode 刷新模式,见HeaderRfreshMode
     */
    public void setHeaderEnable(AbsListView.LayoutParams layoutParams, HeaderRefreshMode headerRefreshMode) {
        headerView = getHeaderView(headerRefreshMode);
        this.headerRefreshMode = headerRefreshMode;
        headerLayoutParams = layoutParams;
        headerView.setLayoutParams(layoutParams);
        addView(headerView, 0);
        headerView.originSate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(headerView!=null && headerHeight == 0) {
            headerHeight = headerView.getMeasuredHeight();
            canRefreshDis = headerHeight;
            scrollTo(0, headerHeight);
        }
        if(footerView!=null && footerHeight == 0) {
            footerHeight = footerView.getMeasuredHeight();
        }
        super.onLayout(changed, l, t, r, b);
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
     * @return view
     */
    protected abstract T createContentView();

    /**
     * contentview是否滚动到底部
     * @return 是否
     */
    protected abstract boolean isContentViewAtBottom();

    /**
     * contentview是否滚动到顶部
     * @return 是否
     */
    protected abstract boolean isContentViewAtTop();

    private CustomView getFooterView(FooterRefreshMode footerRefreshMode) {
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

    private CustomView getHeaderView(HeaderRefreshMode headerRefreshMode) {
        if (headerRefreshMode == HeaderRefreshMode.PULL) {
            return new Pull2RefreshHeaderView(getContext());
        }
        return new Pull2RefreshHeaderView(getContext());
    }

    private void init() {
        scroller = new Scroller(getContext());
        setOrientation(VERTICAL);
        contentView = createContentView();
        if (contentView != null) {
            addView(contentView);
            post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, getMeasuredHeight());
                    contentView.setLayoutParams(layoutParams);
                }
            });
        }
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
                    scrollBy(0, -dY);
                }
                if (getScrollY() > headerHeight - canRefreshDis && headerRefreshState == RefreshState.CAN_REFRESH) {
                    headerRefreshState = RefreshState.ORIGIN_STATE;
                    headerView.originSate();
                } else if (getScrollY() < headerHeight - canRefreshDis
                        && headerRefreshState == RefreshState.ORIGIN_STATE) {
                    headerRefreshState = RefreshState.CAN_REFRESH;
                    headerView.canRefresh();
                }
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (headerRefreshState == RefreshState.CAN_REFRESH) {
                    headerRefreshState = RefreshState.REFRESHING;
                    headerView.refreshing();
                    onHeaderRefreshListener.onHeaderRefresh();
                    mLastHeaderTime = System.currentTimeMillis();
                    smoothScrollTo(0, 0, null);

                } else {
                    smoothScrollTo(0, headerHeight, null);
                }
        }
        return true;
    }

    private void smoothScrollBy(int dx, int dy, OnSmoothScrollListener listener) {
        if(!scroller.isFinished()){
            scroller.abortAnimation();
        }
        scroller.startScroll(getScrollX(), getScrollY(), dx, dy, scrollDurationFactor * Math.abs(dy));
        if(listener != null){
            listener.onSmoothScrollStart();
        }
        postInvalidate();
    }

    private void smoothScrollTo(int x, int y, OnSmoothScrollListener listener) {
        smoothScrollBy(x - getScrollX(), y - getScrollY(), listener);

    }

    private interface OnSmoothScrollListener {
        void onSmoothScrollStart();
        void onSmoothScrollFinish();
    }

}