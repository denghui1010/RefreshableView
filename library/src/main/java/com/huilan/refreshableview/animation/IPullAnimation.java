package com.huilan.refreshableview.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * headerview中的imageview,包含动画
 * Created by liudenghui on 14-10-13.
 */
public interface IPullAnimation {
    void onPull(int d, int canRefresh);

    void start();

    void reset();
}
