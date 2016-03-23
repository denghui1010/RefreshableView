package com.huilan.refreshableview.weight;

/**
 * 可刷新View接口
 * Created by Liu Denghui on 16/3/21.
 */
public interface IRefreshable {
    /**
     * 是否可以下拉
     *
     * @return 是否可以下拉
     */
    boolean canPullDown();

    /**
     * 是否可以上拉
     *
     * @return 是否可以上拉
     */
    boolean canPullUp();

    interface OnOverScrollListener{
        void onOverScroll();
    }
}
