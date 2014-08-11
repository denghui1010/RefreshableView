package com.huilan.refreshableview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.huilan.refreshableview.footerview.AutoLoadFooterView;
import com.huilan.refreshableview.footerview.Click2LoadFooterView;
import com.huilan.refreshableview.headerview.Pull2RefreshHeaderView;

/**
 * Created by liudenghui on 14-7-29.
 */
public class RefreshableListView extends RefreshableBase<ListView> implements ListView.OnScrollListener {
    private ListView listView;

    public RefreshableListView(Context context) {
        super(context);
        init();
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected ListView createContentView() {
        listView = new ListView(getContext());
        return listView;
    }

    private void init(){
        listView.setOnScrollListener(this);
        listView.setFadingEdgeLength(0);
        listView.setBackgroundColor(Color.WHITE);
//        setBackgroundDrawable(new BitmapDrawable());
//        setBackgroundColor(Color.LTGRAY);
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return false;
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
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
        if(firstVisibleItemPosition==0){

        }
    }

    public void setAdapter(ListAdapter adapter){
        listView.setAdapter(adapter);
    }
}
