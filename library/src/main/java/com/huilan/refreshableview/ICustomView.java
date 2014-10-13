package com.huilan.refreshableview;

/**
 * 自定义刷新控件的接口
 * Created by liudenghui on 14-8-8.
 */
public interface ICustomView {
    /**
     * 达到刷新条件时被回调
     */
    void canRefresh();

    /**
     * 正在拉动时被调用
     *
     * @param d          拉动的距离
     * @param canRefresh 可刷新的阀值
     */
    void onPull(int d, int canRefresh);

    /**
     * 回到初始状态时被回调
     */
    void originSate();

    /**
     * 刷新结束后被回调
     */
    void refreshFinished(RefreshResult result);

    /**
     * 正在刷新时被回调
     */
    void refreshing();

    /**
     * 设置最后刷新时间
     * @param time 时间
     */
    void setLastUpdateTime(String time);
}
