package com.huilan.refreshableview.weight;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RefreshableTextView extends TextView implements IRefreshable {

    public RefreshableTextView(Context context) {
        super(context);
    }

    public RefreshableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        return true;
    }

    @Override
    public boolean canPullUp() {
        return true;
    }

    @Override
    public void setOnOverScrollListener(OnOverScrollListener onOverScrollListener) {

    }

}
