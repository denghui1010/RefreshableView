package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by liudenghui on 14-8-8.
 */
public abstract class CustomView extends RelativeLayout{
    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 回到初始状态时被回调
     */
    protected abstract void originSate();

    /**
     * 达到刷新条件时被回调
     */
    protected abstract void canRefresh();

    /**
     * 正在刷新时被回调
     */
    protected abstract void refreshing();

    /**
    protected abstract void refreshFailure();

    /**
     * 刷新结束后被回调
     */
    protected abstract void refreshFinished(RefreshResult result);
}
