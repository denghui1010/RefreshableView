package com.huilan.refreshableview.animation;

/**
 * 控制headerview中下拉动画的接口
 * Created by liudenghui on 14-10-13.
 */
public interface IPullAnimation {
    /**
     * 正在拉动时被调用
     *
     * @param d          拉动距离
     * @param canRefresh 可刷新的阀值,拉动距离d>canRefresh时才可以执行刷新
     */
    void onPull(int d, int canRefresh);

    /**
     * 还原到未启动动画的状态
     */
    void reset();

    /**
     * 开始动画
     */
    void start();

    /**
     * 停止动画
     */
    void stop();
}
