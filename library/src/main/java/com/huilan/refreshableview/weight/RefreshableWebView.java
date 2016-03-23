package com.huilan.refreshableview.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 可刷新的WebView
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableWebView extends WebView implements IRefreshable {

    public RefreshableWebView(Context context) {
        super(context);
    }

    public RefreshableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        if (getScrollY() >= getContentHeight() * getScale() - getMeasuredHeight()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setOnOverScrollListener(OnOverScrollListener onOverScrollListener) {

    }
}
