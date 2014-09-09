package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by liudenghui on 14-8-8.
 */
public interface ICustomView{
    /**
     * 回到初始状态时被回调
     */
    public void originSate();

    /**
     * 达到刷新条件时被回调
     */
    public void canRefresh();

    /**
     * 正在刷新时被回调
     */
    public void refreshing();

    /**
     * 刷新结束后被回调
     */
    public void refreshFinished(RefreshResult result);
}
