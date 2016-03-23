package com.huilan.refreshableview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableWebView;

/**
 * Created by liudenghui on 14-8-29.
 */
public class WebViewActivity extends Activity implements RefreshableLayout.OnRefreshListener {
    private WebViewClient mClient;
    private RefreshableWebView mRefreshableWebView;
    private WebView mWebView;
    private boolean which;
    private RefreshableLayout mRefreshableLayout;


    public void onHeaderRefresh() {
        if (which) {
            mWebView.loadUrl("http://www.baidu.com");
            which = !which;
        } else {
            mWebView.loadUrl("http://www.sina.com");
            which = !which;
        }
    }

    @Override
    public void onFooterRefresh() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack() && (keyCode == KeyEvent.KEYCODE_BACK)) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_webview);
        init();
    }

    private void init() {
        mRefreshableWebView = ((RefreshableWebView) findViewById(R.id.webview));
        mRefreshableLayout.setHeaderEnable();
        mRefreshableLayout.setOnRefreshListener(this);
        mWebView = mRefreshableWebView;
        mClient = new WebViewClient() {
            public void onPageFinished(WebView webView, String paramAnonymousString) {
                super.onPageFinished(webView, paramAnonymousString);
                mRefreshableLayout.notifyHeaderRefreshFinished(RefreshResult.hasmore);
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String paramAnonymousString) {
                webView.loadUrl(paramAnonymousString);
                return true;
            }
        };
        mWebView.setWebViewClient(mClient);
        mRefreshableLayout.notifyHeaderRefreshStarted();
    }
}
