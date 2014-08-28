package com.huilan.refreshableview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.Pull2RefreshHeaderView;

import java.util.logging.LogRecord;

/**
 * Created by liudenghui on 14-7-29.
 */
public abstract class RefreshableBase<T extends View> extends LinearLayout{

    public static final String LOG_TAG = "RefreshableView";
    public final int smooth_scroll_duration = 400;
    
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
    protected int firstVisibleItemPosition;
    private boolean requireInterupt;
    private int canRefreshDis;

    private Scroller scroller;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

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
    
    protected abstract T createContentView();
    
    private void init(){
        scroller = new Scroller(getContext());
        setOrientation(VERTICAL);
        contentView = createContentView();
        if(contentView!=null){
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

    /**
     * 获取中间区域
     * @return 中间区域的view
     */
    public T getContentView(){
        return contentView;
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度,默认下拉模式 HeaderRefreshMode.PULL
     */
    public void setHeaderEnable() {
        headerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, HeaderRefreshMode.PULL);
    }

    /**
     * 设置开启下拉刷新,刷新view使用默认宽高度
     *
     * @param headerRefreshMode 刷新模式
     */
    public void setHeaderEnable(HeaderRefreshMode headerRefreshMode) {
        headerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_header_height));
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
        post(new Runnable() {
            @Override
            public void run() {
                headerHeight = headerView.getMeasuredHeight();
                canRefreshDis = headerHeight;
//                setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                scrollTo(0,headerHeight);
            }
        });
        headerView.originSate();
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     */
    public void setFooterEnable() {
        footerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, FooterRefreshMode.AUTO);
    }

    /**
     * 设置开启上拉刷新,刷新view使用默认宽高度,默认上拉模式 FooterRefreshMode.AUTO
     *
     * @param footerRefreshMode footer刷新模式
     */
    public void setFooterEnable(FooterRefreshMode footerRefreshMode) {
        footerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_footer_height));
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
//        if (footerRefreshMode == FooterRefreshMode.AUTO) {
//            addFooterView(footerView, null, false);
//        } else {
//            addFooterView(footerView);
//        }
        addView(footerView);
        post(new Runnable() {
            @Override
            public void run() {
                footerHeight = footerView.getMeasuredHeight();
            }
        });
        footerView.originSate();
    }

    private CustomView getHeaderView(HeaderRefreshMode headerRefreshMode) {
        if (headerRefreshMode == HeaderRefreshMode.PULL) {
            return new Pull2RefreshHeaderView(getContext());
        }
        return new Pull2RefreshHeaderView(getContext());
    }

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
                if (firstVisibleItemPosition == 0 && dY > 0) {
                    requireInterupt = true;
                }
                if(headerRefreshState == RefreshState.REFRESHING){
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

    private boolean onTouchWhenHeaderRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                contentView.setVerticalScrollBarEnabled(false);
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if(dY< 0 && -dY+getScrollY()>headerHeight){
                    scrollTo(0, headerHeight);
                    return true;
                }
                if(headerRefreshState != RefreshState.REFRESHING) {
                    scrollBy(0, -dY / 3);
                } else {
                    scrollBy(0, -dY);
                }
                if (getScrollY() > headerHeight-canRefreshDis && headerRefreshState == RefreshState.CAN_REFRESH) {
                    headerRefreshState = RefreshState.ORIGIN_STATE;
                    headerView.originSate();
                }else if (getScrollY() < headerHeight-canRefreshDis && headerRefreshState == RefreshState.ORIGIN_STATE) {
                    headerRefreshState = RefreshState.CAN_REFRESH;
                    headerView.canRefresh();
                }
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                contentView.setVerticalScrollBarEnabled(true);
                if(headerRefreshState == RefreshState.CAN_REFRESH){
                    scrollTo(0, 0);
                    headerRefreshState = RefreshState.REFRESHING;
                    headerView.refreshing();
                    onHeaderRefreshListener.onHeaderRefresh();
                }else {
                    scrollTo(0, headerHeight);
                }
        }
        return true;
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

    private void smoothScroll(int x, int y){
        scroller.startScroll(getScrollX(),getScrollY(),x,y,2000);
        postInvalidate();
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
     * 设置上拉刷新监听
     *
     * @param onFooterRefreshListener 上拉刷新监听
     */
    public void setOnFooterRefreshListener(OnFooterRefreshListener onFooterRefreshListener) {
        this.onFooterRefreshListener = onFooterRefreshListener;
    }

    /**
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法,1秒后会隐藏headerview
     * @param result 刷新结果
     */
    public void notifyHeaderRefreshFinished(RefreshResult result) {
        headerView.refreshFinished(result);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                headerRefreshState = RefreshState.ORIGIN_STATE;
                headerView.originSate();
                if(getScrollY() <= 0) {
                    smoothScroll(0, headerHeight);
                }
            }
        },1000);
    }

    /**
     * 通知refreshview进入刷新状态
     */
    public void notifyHeaderRefreshStarted(){
        mHandler.sendEmptyMessageDelayed(0,1000);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                headerRefreshState = RefreshState.REFRESHING;
                headerView.refreshing();
                smoothScroll(0, 0);
                if (onHeaderRefreshListener != null) {
                    onHeaderRefreshListener.onHeaderRefresh();
                }
            }
        }, 1000);
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法,1秒后会隐藏footerview
     *
     * @param result 刷新结果
     */
    public void notifyFooterRefreshFinished(final RefreshResult result) {
        footerView.refreshFinished(result);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(result.rr_int == 1 || result.rr_int == 0){
                    footerRefreshState = RefreshState.ORIGIN_STATE;
                } else {
                    footerRefreshState = RefreshState.NO_MORE;
                }
                footerView.originSate();
            }
        },1000);
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
     * 获取底部刷新模式,见FooterRefreshMode
     *
     * @return FooterRefreshMode 底部刷新模式
     */
    public FooterRefreshMode getFooterRefreshMode() {
        return footerRefreshMode;
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()){
            System.out.println("---------------------------------"+scroller.getCurrY());
            scrollBy(0,-1);
            postInvalidate();
        }
        super.computeScroll();
    }

}