package com.huilan.refreshableview;

/**
 * Created by liudenghui on 14-7-29.
 */
public enum FooterRefreshMode {
    CLOSE, //关闭上拉刷新
    PULL, //处于底部时不显示刷新view,继续滑动才会显示刷新view,继续滑动到一定距离才会刷新,类似于经典下拉刷新
    AUTO,//处于底部时自动显示刷新view并自动刷新
    CLICK//处于底部时自动显示刷新view,点击刷新view刷新
}
