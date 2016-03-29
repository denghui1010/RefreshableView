package com.huilan.refreshableview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 平滑滚动基类
 * Created by liudenghui on 14-10-9.
 */
class SmoothScroller {
    private View mView;
    private PropertyValuesHolder mScrollX;
    private PropertyValuesHolder mScrollY;
    private ObjectAnimator mObjectAnimator;
    private OnSmoothScrollListener mOnSmoothScrollListener;
    private boolean isRunning;

    /**
     * 平滑滚动器
     *
     * @param view 目标View
     */
    public SmoothScroller(View view) {
        mView = view;
        mScrollX = PropertyValuesHolder.ofInt("scrollX", mView.getScrollX());
        mScrollY = PropertyValuesHolder.ofInt("scrollY", mView.getScrollY());
        mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mView, mScrollX, mScrollY);
        Interpolator interpolator = new DecelerateInterpolator();
        mObjectAnimator.setInterpolator(interpolator);
        mObjectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                System.out.println("开始动画");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
//                System.out.println("结束动画");
                if (mOnSmoothScrollListener != null) {
                    mOnSmoothScrollListener.onSmoothScrollFinished();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isRunning = false;
//                System.out.println("取消动画");
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
//                System.out.println("animatedFraction=" + animatedFraction + ",scrollY=" + scrollY + ",currentPlayTime=" + currentPlayTime + ",duration=" + animation.getDuration());
            }
        });
    }

    /**
     * 停止滚动
     */
    public void end() {
        if (null != mObjectAnimator) {
            mObjectAnimator.end();
        }
    }

    /**
     * 取消滚动
     */
    public void cancel() {
        if (null != mObjectAnimator) {
            mObjectAnimator.cancel();
        }
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
    public void smoothScrollTo(int x, int y, int duration, long delayMillis, final OnSmoothScrollListener listener) {
        if (isRunning) {
            mObjectAnimator.cancel();
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollFinished();
            }
        }
        mOnSmoothScrollListener = listener;
        mScrollX.setIntValues(mView.getScrollX(), x);
        mScrollY.setIntValues(mView.getScrollY(), y);
        mObjectAnimator.setDuration(duration);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mObjectAnimator.start();
            }
        }, delayMillis);
        isRunning = true;
    }

    /**
     * 平滑滚动
     *
     * @param x           目标X轴坐标
     * @param y           目标Y轴坐标
     * @param delayMillis 延迟执行时间
     * @param listener    状态监听器
     */
    public void smoothScrollTo(int x, int y, long delayMillis, OnSmoothScrollListener listener) {
        int duration = Math.abs((y - mView.getScrollY()) * 2);
        duration = Math.min(200, duration);
        smoothScrollTo(x, y, duration, delayMillis, listener);
    }

    /**
     * 平滑滚动监听器
     * Created by liudenghui on 14-10-9.
     */
    public interface OnSmoothScrollListener {
        void onSmoothScrollFinished();

        void onSmoothScrollStart();
    }

    public class onSmoothScrollListenerAdapter implements OnSmoothScrollListener {

        @Override
        public void onSmoothScrollFinished() {

        }

        @Override
        public void onSmoothScrollStart() {

        }
    }
}
