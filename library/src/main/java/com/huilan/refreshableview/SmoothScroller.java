package com.huilan.refreshableview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.UUID;

/**
 * 平滑滚动基类
 * Created by liudenghui on 14-10-9.
 */
class SmoothScroller {
    private SmoothRunnable mSmoothRunnable;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 停止滚动
     */
    public void end() {
        if (mSmoothRunnable != null) {
            mSmoothRunnable.end();
        }
    }

    /**
     * 取消滚动
     */
    public void cancel() {
        if (mSmoothRunnable != null) {
            mSmoothRunnable.cancel(null);
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
    public void smoothScrollTo(final View view, final int x, final int y, final int duration, long delayMillis, final OnSmoothScrollListener listener) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSmoothRunnable != null) {
                    mSmoothRunnable.cancel(new onSmoothScrollListenerAdapter() {
                        @Override
                        public void onSmoothScrollCancel() {
                            mSmoothRunnable = new SmoothRunnable(view, x, y, duration, listener);
                            mHandler.post(mSmoothRunnable);
                        }
                    });
                } else {
                    mSmoothRunnable = new SmoothRunnable(view, x, y, duration, listener);
                    mHandler.post(mSmoothRunnable);
                }
            }
        }, delayMillis);

    }

    /**
     * 平滑滚动
     *
     * @param x           目标X轴坐标
     * @param y           目标Y轴坐标
     * @param delayMillis 延迟执行时间
     * @param listener    状态监听器
     */
    public void smoothScrollTo(View view, int x, int y, long delayMillis, OnSmoothScrollListener listener) {
        int duration = (int) (Math.abs((y - view.getScrollY())) * 2.5);
        duration = Math.min(300, duration);
        System.out.println("duration=" + duration);
        smoothScrollTo(view, x, y, duration, delayMillis, listener);
    }

    /**
     * 平滑滚动监听器
     * Created by liudenghui on 14-10-9.
     */
    interface OnSmoothScrollListener {
        void onSmoothScrollEnd();

        void onSmoothScrollStart();

        void onSmoothScrollCancel();

    }

    static abstract class onSmoothScrollListenerAdapter implements OnSmoothScrollListener {

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
        private int mDuration;
        private int mX;
        private int mY;
        private ObjectAnimator mObjectAnimator;
        private boolean mShouldCancel = false;
        private boolean mShouldEnd = false;
        private SmoothScroller.OnSmoothScrollListener mCancelListener;

        public SmoothRunnable(View view, int x, int y, int duration, SmoothScroller.OnSmoothScrollListener onSmoothScrollListener) {
            mX = x;
            mY = y;
            mDuration = duration;
            mView = view;
            mOnSmoothScrollListener = onSmoothScrollListener;
        }

        public void cancel(SmoothScroller.OnSmoothScrollListener cancelListener) {
            mShouldCancel = true;
            mCancelListener = cancelListener;
            if (mObjectAnimator != null && (mObjectAnimator.isStarted() || mObjectAnimator.isRunning())) {
//                System.out.println(TAG + ":滚动已经开始,执行取消");
                mObjectAnimator.cancel();
            } else {
//                System.out.println(TAG + ":滚动尚未开始,直接取消");
                if (cancelListener != null) {
                    cancelListener.onSmoothScrollCancel();
                }
            }
        }

        public void end() {
            mShouldEnd = true;
            if (mObjectAnimator != null) {
                mObjectAnimator.end();
            }
        }

        @Override
        public void run() {
            if (mShouldCancel || mShouldEnd) {
                return;
            }
//            System.out.println(TAG + ":run");
            PropertyValuesHolder scrollX = PropertyValuesHolder.ofInt("scrollX", mView.getScrollX(), mX);
            PropertyValuesHolder scrollY = PropertyValuesHolder.ofInt("scrollY", mView.getScrollY(), mY);
            mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mView, scrollX, scrollY);
            mObjectAnimator.setDuration(mDuration);
            mObjectAnimator.setInterpolator(new DecelerateInterpolator());
            mObjectAnimator.addListener(this);
            mObjectAnimator.addUpdateListener(this);
            mObjectAnimator.start();
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
//            System.out.println(TAG + "开始滚动");
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollStart();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
//            System.out.println(TAG + "结束滚动");
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollEnd();
            }
            mObjectAnimator = null;
            mSmoothRunnable = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
//            System.out.println(TAG + "取消滚动");
            if (mOnSmoothScrollListener != null) {
                mOnSmoothScrollListener.onSmoothScrollCancel();
            }
            if (mCancelListener != null) {
                mCancelListener.onSmoothScrollCancel();
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
