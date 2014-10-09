package com.huilan.refreshableview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableExpandableListView extends RefreshableBase<ExpandableListView>
        implements AbsListView.OnScrollListener {
    private ExpandableListView mExpandableListView;
    private List<View> mHeaderViews = new ArrayList<View>();

    public RefreshableExpandableListView(Context context) {
        super(context);
    }

    public RefreshableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addFooterView(View view) {
        mExpandableListView.addFooterView(view);
    }

    public void addHeaderView(View view) {
        mExpandableListView.addHeaderView(view);
        mHeaderViews.add(view);
    }

    public void addHeaderView(View view, Object data, boolean isSelectable){
        mExpandableListView.addHeaderView(view,data,isSelectable);
        mHeaderViews.add(view);
    }

    public int getFooterViewsCount() {
        return mExpandableListView.getFooterViewsCount();
    }

    public int getHeaderViewsCount() {
        return mExpandableListView.getHeaderViewsCount();
    }

    public void setEmptyView(final View emptyView){
        addView(emptyView);
        mExpandableListView.setEmptyView(emptyView);
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = getMeasuredHeight();
                emptyView.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mExpandableListView.getLastVisiblePosition() == mExpandableListView.getCount() - 1) {
            if (footerRefreshState == RefreshState.ORIGIN_STATE && footerRefreshMode == FooterRefreshMode.AUTO) {
                footerRefreshState = RefreshState.REFRESHING;
                footerView.refreshing();
                onFooterRefreshListener.onFooterRefresh();
            }
        }
    }

    public void setAdapter(BaseExpandableListAdapter adapter) {
        mExpandableListView.setAdapter(adapter);
    }

    public void setDivider(Drawable divider) {
        mExpandableListView.setDivider(divider);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mExpandableListView.setOnItemClickListener(listener);
    }

    @Override
    protected ExpandableListView createContentView(AttributeSet attrs) {
        mExpandableListView = new ExpandableListView(getContext(), attrs);
        mExpandableListView.setId(R.id.refreshableexpandlistview);
        mExpandableListView.setOnScrollListener(this);
        return mExpandableListView;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return mExpandableListView.getLastVisiblePosition() == mExpandableListView.getCount() - 1;
    }

    @Override
    protected boolean isContentViewAtTop() {
//        if(mHeaderViews == null || mHeaderViews.size() == 0){
            return mExpandableListView.getFirstVisiblePosition() == 0;
//        }
//        View childAt = mExpandableListView.getChildAt(0);
//        System.out.println("top"+childAt.getTop() +"padt"+getPaddingTop() + mListView.getPaddingTop());
//        if(subHeaderView!=null) {
//            return childAt == headerView.getParent() && childAt.getTop() >= mExpandableListView.getPaddingTop();
//        } else {
//            return childAt == mHeaderViews.get(mHeaderViews.size() - 1) && childAt.getTop() >= mExpandableListView.getPaddingTop();
//        }
    }

    @Override
    protected Orientation getRefreshableViewScrollDirection() {
        return Orientation.VERTICAL;
    }
}
