package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 下拉刷新listview
 * Created by liudenghui on 14-10-10.
 */
public class RefreshableListView extends RefreshableListViewBase<ListView> {

    public RefreshableListView(Context context) {
        super(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(ListAdapter adapter) {
        contentView.setAdapter(adapter);
    }

    @Override
    protected ListView createContentView(AttributeSet attrs) {
        ListView myListView = new MyListView(getContext(), attrs);
        myListView.setId(R.id.refreshablelistview);
        return myListView;
    }

    @Override
    protected void init() {
        super.init();
        contentView.setHeaderDividersEnabled(false);
        contentView.setFooterDividersEnabled(false);
        contentView.setPadding(getResources().getDimensionPixelSize(R.dimen.default_paddingleft), 0,
                               getResources().getDimensionPixelOffset(R.dimen.default_paddingright), 0);
    }

    private class MyListView extends ListView {
        private GestureDetector mGestureDetector;

        public MyListView(Context context) {
            super(context);
            init();
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
        }

        private void init() {
            mGestureDetector = new GestureDetector(new YScrollDetector());
        }

        private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return Math.abs(distanceY) >= Math.abs(distanceX);
            }
        }
    }
}
