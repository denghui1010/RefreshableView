package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends ListView implements ListView.OnScrollListener {

    private TextView header_text_1;
    private TextView footer_text_1;

    public RefreshableListView(Context context) {
        super(context);
        setOnScrollListener(this);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(this);
    }

    public static final String LOG_TAG = "RefreshableView";
    public final int SMOOTH_SCROLL_DURATION = 200;
    private View headerView;
    private View footerView;
    private AbsListView.LayoutParams headerLayoutParams;
    private AbsListView.LayoutParams footerLayoutParams;

    private OnHeaderRefreshListener onHeaderRefreshListener;
    private OnFooterRefreshListener onFooterRefreshListener;
    private HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    private FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;
    private RefreshState headerRefreshState = RefreshState.PULL_2_REFRESH;
    private RefreshState footerRefreshState = RefreshState.REFRESHING;

    private int headerHeight;
    private int footerHeight;
    private int startY;
    private int firstVisibleItemPosition;

    /**
     * 获取顶部刷新模式
     *
     * @return HeaderRefreshMode 顶部刷新模式
     */
    public HeaderRefreshMode getHeaderRefreshMode() {
        return headerRefreshMode;
    }

    /**
     * 获取底部刷新模式
     *
     * @return FooterRefreshMode 底部刷新模式
     */
    public FooterRefreshMode getFooterRefreshMode() {
        return footerRefreshMode;
    }

    /**
     * 设置顶部刷新模式
     *
     * @param headerRefreshMode 顶部刷新模式
     */
    public void setHeaderRefreshMode(HeaderRefreshMode headerRefreshMode) {
        this.headerRefreshMode = headerRefreshMode;
    }

    /**
     * 设置底部刷新模式
     *
     * @param footerRefreshMode 底部刷新模式
     */
    public void setFooterRefreshMode(FooterRefreshMode footerRefreshMode) {
        this.footerRefreshMode = footerRefreshMode;
    }

    protected void setHeaderEnable() {
        headerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, HeaderRefreshMode.PULL);
    }

    protected void setHeaderView(AbsListView.LayoutParams layoutParams) {
        setHeaderEnable(layoutParams, HeaderRefreshMode.PULL);
    }

    protected void setHeaderEnable(AbsListView.LayoutParams layoutParams, HeaderRefreshMode headerRefreshMode) {
        this.headerRefreshMode = headerRefreshMode;
        headerLayoutParams = layoutParams;
        createHeaderView();
        addHeaderView(headerView);
        measureView(headerView);
        headerView.setLayoutParams(layoutParams);
        headerHeight = headerView.getMeasuredHeight();
        setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
    }

    protected void setFooterEnable() {
        footerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, FooterRefreshMode.AUTO);
    }

    protected void setFooterEnable(AbsListView.LayoutParams layoutParams) {
        setFooterEnable(layoutParams, FooterRefreshMode.AUTO);
    }

    protected void setFooterEnable(AbsListView.LayoutParams layoutParams, FooterRefreshMode footerRefreshMode) {
        this.footerRefreshMode = footerRefreshMode;
        footerLayoutParams = layoutParams;
        createFooterView();
        addFooterView(footerView);
        measureView(footerView);
        footerView.setLayoutParams(footerLayoutParams);
        footerHeight = footerView.getMeasuredHeight();
        changeFooterView();
    }

    private void createHeaderView() {
        headerView = View.inflate(getContext(), R.layout.layout_header, null);
        header_text_1 = (TextView) headerView.findViewById(R.id.header_text_1);
        TextView header_text_2 = (TextView) headerView.findViewById(R.id.header_text_2);
        ProgressBar header_progressbar = (ProgressBar) headerView.findViewById(R.id.header_progressbar);
        ImageView header_image = (ImageView) headerView.findViewById(R.id.header_image);
    }

    private void createFooterView() {
        footerView = View.inflate(getContext(), R.layout.layout_header, null);
        footer_text_1 = (TextView) footerView.findViewById(R.id.header_text_1);
        TextView footer_text_2 = (TextView) footerView.findViewById(R.id.header_text_2);
        ProgressBar footer_progressbar = (ProgressBar) footerView.findViewById(R.id.header_progressbar);
        ImageView footer_image = (ImageView) footerView.findViewById(R.id.header_image);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(headerRefreshMode != HeaderRefreshMode.CLOSE){
            onTouchWhenHeaderRefreshEnable(event);
        }
        if(footerRefreshMode != FooterRefreshMode.CLOSE){
            onTouchWhenFooterRefreshEnable(event);
        }
        return super.onTouchEvent(event);
    }

    private void onTouchWhenHeaderRefreshEnable(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currY = (int) event.getRawY();
                int dY = currY - startY;
                if (firstVisibleItemPosition == 0 && dY > 0 && headerRefreshState != RefreshState.REFRESHING) {
//                    System.out.println("padd" + getPaddingTop() + "dY" + dY +"pos"+firstVisibleItemPosition);
                    setPadding(getPaddingLeft(), getPaddingTop() + dY/4 , getPaddingRight(), getPaddingBottom());
                    if (getPaddingTop() > 30 && headerRefreshState == RefreshState.PULL_2_REFRESH) {
                        headerRefreshState = RefreshState.RELEASE_2_REFRESH;
                    }
                } else if (dY < 0 && getPaddingTop()>-headerHeight) {
                    if (getPaddingTop() <= headerHeight && headerRefreshState == RefreshState.RELEASE_2_REFRESH) {
                        headerRefreshState = RefreshState.PULL_2_REFRESH;
                    }
                    if(getPaddingTop()+dY<-headerHeight) {
                        setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                    }else {
                        setPadding(getPaddingLeft(),getPaddingTop()+dY,getPaddingRight(),getPaddingBottom());
                    }
                }
                changeHeaderView();
                startY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (firstVisibleItemPosition == 0 && headerRefreshState == RefreshState.RELEASE_2_REFRESH) {
                    setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
                    headerRefreshState = RefreshState.REFRESHING;
                    changeHeaderView();
                    onHeaderRefreshListener.onHeaderRefresh(headerView);
                } else if (firstVisibleItemPosition == 0 &&headerRefreshState == RefreshState.PULL_2_REFRESH) {
                    setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                }
                break;
        }
    }

    private void onTouchWhenFooterRefreshEnable(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    private void changeHeaderView() {
        if (headerRefreshState == RefreshState.RELEASE_2_REFRESH) {
            header_text_1.setText("松开刷新");
        }
        else if (headerRefreshState == RefreshState.REFRESHING) {
            header_text_1.setText("正在刷新");
        }
        else if (headerRefreshState == RefreshState.PULL_2_REFRESH) {
            header_text_1.setText("下拉刷新");
        }
    }

    private void changeFooterView(){
        if(footerRefreshState == RefreshState.RELEASE_2_REFRESH) {
            footer_text_1.setText("松开刷新");
        } else if(footerRefreshState == RefreshState.REFRESHING){
            footer_text_1.setText("正在刷新");
        } else if(footerRefreshState == RefreshState.PULL_2_REFRESH){
            footer_text_1.setText("上拉刷新");
        } else if(footerRefreshState == RefreshState.CLICK_2_REFRESH){
            footer_text_1.setText("点击刷新");
        }
    }

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener onHeaderRefreshListener) {
        this.onHeaderRefreshListener = onHeaderRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener onFooterRefreshListener) {
        this.onFooterRefreshListener = onFooterRefreshListener;
    }

    public void notifyHeaderRefreshFinished() {
        headerRefreshState = RefreshState.PULL_2_REFRESH;
        changeHeaderView();
        setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
    }

    public void notifyFooterRefreshFinished(RefreshState refreshState){
        footerRefreshState = refreshState;
        changeFooterView();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(getLastVisiblePosition() == getCount()-1){
            if(footerRefreshMode == FooterRefreshMode.AUTO){
                footerRefreshState = RefreshState.REFRESHING;
                changeFooterView();
                onFooterRefreshListener.onFooterRefresh(footerView);
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItemPosition = firstVisibleItem;
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


}
