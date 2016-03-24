package com.huilan.refreshableview.smoothscroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 平滑滚动基类
 * Created by liudenghui on 14-10-9.
 */
public class SmoothScroller2 {
    private View mView;
    private Handler mHandler = new Handler();
    private boolean isRunning;
    private PropertyValuesHolder mScrollX;
    private PropertyValuesHolder mScrollY;
    private ObjectAnimator mObjectAnimator;
    private OnSmoothMoveListener mOnSmoothMoveListener;

    /**
     * 平滑滚动器
     *
     * @param view 目标View
     */
    public SmoothScroller2(View view) {
        mView = view;
        mScrollX = PropertyValuesHolder.ofInt("scrollX", mView.getScrollX());
        mScrollY = PropertyValuesHolder.ofInt("scrollY", mView.getScrollY());
        mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mView, mScrollX, mScrollY);
        Interpolator interpolator = new DecelerateInterpolator();
        mObjectAnimator.setInterpolator(interpolator);
        mObjectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                System.out.println("开始");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("结束");
                if (mOnSmoothMoveListener != null) {
                    mOnSmoothMoveListener.onSmoothScrollFinished();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                System.out.println("取消");
                if (mOnSmoothMoveListener != null) {
                    mOnSmoothMoveListener.onSmoothScrollFinished();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object scrollY = animation.getAnimatedValue("scrollY");
                float animatedFraction = animation.getAnimatedFraction();
                Object animatedValue = animation.getAnimatedValue();
                long currentPlayTime = animation.getCurrentPlayTime();
                System.out.println("animatedFraction=" + animatedFraction + ",scrollY=" + scrollY + ",currentPlayTime=" + currentPlayTime + ",duration=" + animation.getDuration());
            }
        });
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 停止滚动
     */
    public void stop() {
//        if (null != mSmoothScrollRunnable) {
//            mSmoothScrollRunnable.stop();
//        }
    }

    /**
     * 平滑滚动
     *
     * @param x           目标X轴坐标
     * @param y           目标Y轴坐标
     * @param duration    持续时间
     * @param delayMillis 延迟执行时间
     * @param listener    状态监听器
     */
    public void smoothScrollTo(int x, int y, int duration, long delayMillis, final OnSmoothMoveListener listener) {
        mOnSmoothMoveListener = listener;
        mScrollX.setIntValues(mView.getScrollX(), x);
        mScrollY.setIntValues(mView.getScrollY(), y);
        mObjectAnimator.setDuration(duration);
        mObjectAnimator.setStartDelay(delayMillis);
        mObjectAnimator.start();
    }

    /**
     * 平滑滚动
     *
     * @param x           目标X轴坐标
     * @param y           目标Y轴坐标
     * @param delayMillis 延迟执行时间
     * @param listener    状态监听器
     */
    public void smoothScrollTo(int x, int y, long delayMillis, OnSmoothMoveListener listener) {
        int duration = Math.abs((y - mView.getScrollY()) * 2);
        smoothScrollTo(x, y, duration, delayMillis, listener);
    }
}
