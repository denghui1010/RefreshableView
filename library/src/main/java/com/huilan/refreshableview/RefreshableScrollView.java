package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableScrollView extends RefreshableBase<ScrollView> {

    private ScrollView mScrollView;

    public RefreshableScrollView(Context context) {
        super(context);
    }

    public RefreshableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ScrollView createContentView(AttributeSet attrs) {
        mScrollView = new ScrollView(getContext(), attrs);
        mScrollView.setId(R.id.refreshablescrollview);
        return mScrollView;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return mScrollView.getScrollY() == mScrollView.getMeasuredHeight();
    }

    @Override
    protected boolean isContentViewAtTop() {
        return mScrollView.getScrollY() == 0;
    }

    @Override
    protected Orientation getRefreshableViewScrollDirection() {
        return Orientation.VERTICAL;
    }

}
