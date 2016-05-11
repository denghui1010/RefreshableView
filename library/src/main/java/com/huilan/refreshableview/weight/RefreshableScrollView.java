package com.huilan.refreshableview.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * 可刷新的ScrollView
 * Created by liudenghui on 14-8-29.
 */
public class RefreshableScrollView extends ScrollView implements IRefreshable {
    private OnOverScrollListener mOnOverScrollListener;
    private final OverScroller mOverScroller;

    public RefreshableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_NEVER);
        mOverScroller = new OverScroller(getContext(), null);
    }

    public void setOnOverScrollListener(OnOverScrollListener onOverScrollListener) {
        mOnOverScrollListener = onOverScrollListener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
//        System.out.println("过滑:" + "scrollY=" + scrollY + ",clampedY=" + clampedY);
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (clampedY) {
            float currVelocity = mOverScroller.getCurrVelocity();
            mOverScroller.abortAnimation();
            int finalY = mOverScroller.getFinalY();
            if (finalY < 0) {
                currVelocity = -currVelocity;
            }
            if (mOnOverScrollListener != null) {
                mOnOverScrollListener.onOverScroll((int) currVelocity);
            }
        }
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        System.out.println("速度:" + velocityY + ", scrollY=" + getScrollY());
        velocityY = velocityY / 2;
        if (velocityY > 0) {//上滑,下边界回弹
            mOverScroller.fling(0, 0, (int) velocityX, (int) velocityY, 0, 0, -getHeight(), getHeight());
        } else if (velocityY < 0) {//下滑,上边界回弹
            mOverScroller.fling(0, 0, (int) velocityX, (int) velocityY, 0, 0, -getHeight(), getHeight());
        }
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        mOverScroller.computeScrollOffset();
//        System.out.println("我要计算:" + mOverScroller.getCurrY() + ",finally:" + mOverScroller.getFinalY() + ",scrollY:" + getScrollY() + ",velocity:" + mOverScroller.getCurrVelocity());
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
