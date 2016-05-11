package com.huilan.refreshableview.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.OverScroller;

/**
 * 下拉刷新ListView
 * Created by liudenghui on 14-10-10.
 */
public class RefreshableListView extends ListView implements IRefreshable {

    private final OverScroller mOverScroller;
    private OnOverScrollListener mOnOverScrollListener;

    public RefreshableListView(Context context, AttributeSet attrs) {
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
        if (getCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else { // 滑到ListView的顶部了
            return getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0;
        }
    }

    @Override
    public boolean canPullUp() {
//        if (getCount() == 0) {
//            // 没有item的时候也可以上拉加载
//            return true;
//        } else
        if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null && getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight()) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public boolean performItemClick(final View view, final int position, final long id) {
//        final OnItemClickListener onItemClickListener = getOnItemClickListener();
//        if (onItemClickListener != null) {
//            playSoundEffect(SoundEffectConstants.CLICK);
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    onItemClickListener.onItemClick(RefreshableListView.this, view, position, id);
//                }
//            }, 150);
//            if (view != null) {
//                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
//            }
//            return true;
//        }
//        return false;
//    }
}
