package com.huilan.refreshableview.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class RefreshableImageView extends ImageView implements IRefreshable {

    public RefreshableImageView(Context context) {
        super(context);
    }

    public RefreshableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableImageView(Context context, AttributeSet attrs, int defStyle) {
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
