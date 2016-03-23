package com.huilan.refreshableview.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 可刷新的ScrollView
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableScrollView extends ScrollView implements IRefreshable {
    private int mDeltaX;
    private int mDeltaY;
    private OnOverScrollListener mOnOverScrollListener;

    public RefreshableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setOnOverScrollListener(OnOverScrollListener onOverScrollListener) {
        mOnOverScrollListener = onOverScrollListener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
//        System.out.println("过滑:" + "scrollY=" + scrollY + ",clampedY=" + clampedY);
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (clampedY) {
            if (mOnOverScrollListener != null) {
                mOnOverScrollListener.onOverScroll(mDeltaX, mDeltaY);
                System.out.println("边界回弹:开始");
            }
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//        System.out.println("边界回弹:" + "deY=" + deltaY + ",scrollY=" + scrollY + ",scrollRangeY=" + scrollRangeY + ",maxOverScrollY=" + maxOverScrollY + ",isTouch=" + isTouchEvent);
        mDeltaX = deltaX;
        mDeltaY = deltaY;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
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
