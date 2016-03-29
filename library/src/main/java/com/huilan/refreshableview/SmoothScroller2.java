package com.huilan.refreshableview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.UUID;

/**
 * 平滑滚动基类
 * Created by liudenghui on 14-10-9.
 */
class SmoothScroller2 {
    

    /**
     * 平滑滚动器
     *
     * @param view 目标View
     */
    public SmoothScroller2(View view) {
        mView = view;

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
        mOnSmoothScrollListener = listener;

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mObjectAnimator.cancel();
//                mObjectAnimator.start();
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
        void onSmoothScrollEnd();

        void onSmoothScrollStart();

        void onSmoothScrollCancel();

    }

    public static abstract class onSmoothScrollListenerAdapter implements OnSmoothScrollListener {

        @Override
        public void onSmoothScrollEnd() {

        }

        @Override
        public void onSmoothScrollStart() {

        }

        @Override
        public void onSmoothScrollCancel() {

        }
    }

    private class SmoothRunnable implements Runnable, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        private final String TAG = UUID.randomUUID().toString();
        private View mView;
        private OnSmoothScrollListener mOnSmoothScrollListener;
        private boolean isRunning;
        private int mDuration;
        private int mX;
        private int mY;

        public SmoothRunnable(View view, int x, int y, int duration, SmoothScroller2.OnSmoothScrollListener onSmoothScrollListener) {
            mX = x;
            mY = y;
            mDuration = duration;
            mView = view;
            mOnSmoothScrollListener = onSmoothScrollListener;
        }

        @Override
        public void run() {
            PropertyValuesHolder scrollX = PropertyValuesHolder.ofInt("scrollX", mView.getScrollX(), mX);
            PropertyValuesHolder scrollY = PropertyValuesHolder.ofInt("scrollY", mView.getScrollY(), mY);
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mView, scrollX, scrollY);
            objectAnimator.setDuration(mDuration);
            objectAnimator.setInterpolator(new DecelerateInterpolator());
            objectAnimator.addListener(this);
            objectAnimator.addUpdateListener(this);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Object scrollY = animation.getAnimatedValue("scrollY");
            float animatedFraction = animation.getAnimatedFraction();
            Object animatedValue = animation.getAnimatedValue();
            long currentPlayTime = animation.getCurrentPlayTime();
//          System.out.println("animatedFraction=" + animatedFraction + ",scrollY=" + scrollY + ",currentPlayTime=" + currentPlayTime + ",mDuration=" + animation.getDuration());
        }

        @Override
        public void onAnimationStart(Animator animation) {
            System.out.println(TAG + "开始动画");
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollStart();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            System.out.println(TAG + "结束动画");
            isRunning = false;
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollEnd();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            System.out.println(TAG + "取消动画");
            isRunning = false;
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollCancel();
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
