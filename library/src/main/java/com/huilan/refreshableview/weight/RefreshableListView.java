package com.huilan.refreshableview.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListView;

/**
 * 下拉刷新listview
 * Created by liudenghui on 14-10-10.
 */
public class RefreshableListView extends ListView implements IRefreshable {

    private int mDeltaX;
    private int mDeltaY;
    private OnOverScrollListener mOnOverScrollListener;

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setOnOverScrollListener(OnOverScrollListener onOverScrollListener) {
        mOnOverScrollListener = onOverScrollListener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        System.out.println("过滑:" + "scrollY=" + scrollY + ",clampedY=" + clampedY);
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (clampedY) {
            if(mOnOverScrollListener!= null) {
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
        if (getCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else // 滑到ListView的顶部了
            return getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0;
    }

    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null && getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }

    @Override
    public boolean performItemClick(final View view, final int position, final long id) {
        final OnItemClickListener onItemClickListener = getOnItemClickListener();
        if (onItemClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(RefreshableListView.this, view, position, id);
                }
            }, 150);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            return true;
        }
        return false;
    }
}
