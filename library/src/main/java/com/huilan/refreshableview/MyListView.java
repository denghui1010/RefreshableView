package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by liudenghui on 14-8-28.
 */
public class MyListView extends ListView{
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean bool = super.onTouchEvent(ev);
//        System.out.println("listview"+bool);
        return true;
    }
}
