package com.huilan.refreshableview.sample;

import com.huilan.refreshableview.OnHeaderRefreshListener;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableWebView;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by liudenghui on 14-8-29.
 */
public class WebViewActivity extends Activity implements OnHeaderRefreshListener {
    private WebViewClient mClient;
    private RefreshableWebView mRefreshableWebView;
    private WebView mWebView;
    private boolean which;

    public void onHeaderRefresh() {
        if (which) {
            mWebView.loadUrl("http://www.baidu.com");
            which = !which;
        } else {
            mWebView.loadUrl("http://www.sina.com");
            which = !which;
        }
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
        mRefreshableWebView.setHeaderEnable();
        mRefreshableWebView.setOnHeaderRefreshListener(this);
        mWebView = mRefreshableWebView.getContentView();
        mClient = new WebViewClient() {
            public void onPageFinished(WebView webView, String paramAnonymousString) {
                super.onPageFinished(webView, paramAnonymousString);
                mRefreshableWebView.notifyHeaderRefreshFinished(RefreshResult.hasmore);
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String paramAnonymousString) {
                webView.loadUrl(paramAnonymousString);
                return true;
            }
        };
        mWebView.setWebViewClient(mClient);
        mRefreshableWebView.notifyHeaderRefreshStarted();
    }
}
