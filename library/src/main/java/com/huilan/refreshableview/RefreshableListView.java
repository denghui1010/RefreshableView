package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
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

    public class MyListView extends ListView {
        private float mFirstX;
        private float mFirstY;
        private int mTouchSlop;

        public MyListView(Context context) {
            super(context);
            init();
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public MyListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init(){
            ViewConfiguration config = ViewConfiguration.get(getContext());
            mTouchSlop = config.getScaledTouchSlop();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {

//            switch (ev.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    mFirstX = ev.getRawX();
//                    mFirstY = ev.getRawY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    float dx = ev.getRawX() - mFirstX;
//                    float dy = ev.getRawY() - mFirstY;
//                    if (dx > mTouchSlop && dx * 0.5 > dy) {
//                        return false;
//                    }
//                    break;
//
//            }
            return super.onInterceptTouchEvent(ev);
        }

    }
}
