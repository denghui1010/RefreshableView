package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.Pull2RefreshHeaderView;

/**
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends ListView implements ListView.OnScrollListener {

    public RefreshableListView(Context context) {
        super(context);
        init();
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public static final String LOG_TAG = "RefreshableView";
    public final int SMOOTH_SCROLL_DURATION = 200;
    private CustomView headerView;
    private CustomView footerView;
    private AbsListView.LayoutParams headerLayoutParams;
    private AbsListView.LayoutParams footerLayoutParams;

    private OnHeaderRefreshListener onHeaderRefreshListener;
    private OnFooterRefreshListener onFooterRefreshListener;
    private HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    private FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;
    private RefreshState headerRefreshState = RefreshState.ORIGIN_STATE;
    private RefreshState footerRefreshState = RefreshState.ORIGIN_STATE;

    private int headerHeight;
    private int footerHeight;
    private int startY;
    private int firstVisibleItemPosition;

    private void init() {
        setOnScrollListener(this);
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
        addHeaderView(headerView);
        post(new Runnable() {
            @Override
            public void run() {
                headerHeight = headerView.getMeasuredHeight();
                setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
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
        if (footerRefreshMode == FooterRefreshMode.AUTO) {
            addFooterView(footerView, null, false);
        } else {
            addFooterView(footerView);
        }
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
    public boolean onTouchEvent(MotionEvent event) {
        if (headerRefreshMode != HeaderRefreshMode.CLOSE) {
            onTouchWhenHeaderRefreshEnable(event);
        }
        if (footerRefreshMode != FooterRefreshMode.CLOSE) {
            onTouchWhenFooterRefreshEnable(event);
        }
        return super.onTouchEvent(event);
    }

    private void onTouchWhenHeaderRefreshEnable(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (firstVisibleItemPosition == 0 && dY > 0 && headerRefreshState != RefreshState.REFRESHING) {
//                    System.out.println("padd" + getPaddingTop() + "dY" + dY +"pos"+firstVisibleItemPosition);
                    setPadding(getPaddingLeft(), getPaddingTop() + dY / 3, getPaddingRight(), getPaddingBottom());
                    if (getPaddingTop() > 0 && headerRefreshState == RefreshState.ORIGIN_STATE) {
                        headerRefreshState = RefreshState.CAN_REFRESH;
                        headerView.canRefresh();
                    }
                } else if (dY < 0 && getPaddingTop() > -headerHeight) {
                    if (getPaddingTop() <= headerHeight && headerRefreshState == RefreshState.CAN_REFRESH) {
                        headerRefreshState = RefreshState.ORIGIN_STATE;
                        headerView.originSate();
                    }
                    if (getPaddingTop() + dY < -headerHeight && headerRefreshState != RefreshState.REFRESHING) {
                        setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                    } else if(headerRefreshState != RefreshState.REFRESHING){
                        setPadding(getPaddingLeft(), getPaddingTop() + dY, getPaddingRight(), getPaddingBottom());
                    }
                }
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (firstVisibleItemPosition == 0 && headerRefreshState == RefreshState.CAN_REFRESH) {
                    setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
                    headerRefreshState = RefreshState.REFRESHING;
                    headerView.refreshing();
                    onHeaderRefreshListener.onHeaderRefresh();
                } else if (firstVisibleItemPosition == 0 && headerRefreshState == RefreshState.ORIGIN_STATE) {
                    setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                }
                break;
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
     * 通知下拉刷新已经完成,在你期望结束下拉刷新时需要调用此方法
     */
    public void notifyHeaderRefreshFinished() {
        setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
        headerView.refreshFinished(true);
        headerRefreshState = RefreshState.ORIGIN_STATE;
    }

    /**
     * 通知上拉刷新已经完成,在你期望结束上拉刷新时需要调用此方法
     *
     * @param hasMore 是否还有更多内容等待下次刷新,false:刷新view将默认显示"已无更多"
     */
    public void notifyFooterRefreshFinished(boolean hasMore) {
        if(hasMore){
            footerRefreshState = RefreshState.ORIGIN_STATE;
        } else {
            footerRefreshState = RefreshState.NO_MORE;
        }
        footerView.refreshFinished(hasMore);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (getLastVisiblePosition() == getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                onFooterRefreshListener.onFooterRefresh();
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItemPosition = firstVisibleItem;
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

}
