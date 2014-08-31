package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableWebView extends RefreshableBase<WebView> {

    private WebView mWebView;

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
    protected WebView createContentView() {
        mWebView = new WebView(getContext());
        return mWebView;
    }

    @Override
    protected boolean isContentViewAtTop() {
        return mWebView.getScrollY() == 0;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return false;
    }
}
