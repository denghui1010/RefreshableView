package com.huilan.refreshableview;

/**
 * Created by liudenghui on 14-8-28.
 */
public enum RefreshResult {
    /**
     * 刷新成功,还有更多结果
     */
    hasmore(1),
    /**
     * 刷新成功,没有更多结果
     */
    nomore(2),
    /**
     * 刷新失败
     */
    failure(0);
    final int rr_int;
    RefreshResult(int i){
        rr_int = i;
    }
}
