package com.huilan.refreshableview.weight;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 可刷新的ScrollView
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableScrollView extends ScrollView implements IRefreshable {

    public RefreshableScrollView(Context context) {
        super(context);
    }

    public RefreshableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight())) {
            return true;
        } else {
            return false;
        }
    }

}
