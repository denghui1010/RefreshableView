package com.huilan.refreshableview.smoothscroll;

import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 平滑滚动基类
 * Created by liudenghui on 14-10-9.
 */
public class SmoothScroller {
    private SmoothScrollRunnable mSmoothScrollRunnable;
    private View mView;
    private Handler mHandler = new Handler();

    public boolean isRunning() {
        return isRunning;
    }

    private boolean isRunning;

    /**
     * 平滑滚动器
     *
     * @param view 目标View
     */
    public SmoothScroller(View view) {
        mView = view;
    }

    /**
     * 停止滚动
     */
    public void stop() {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
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
    public void smoothScrollTo(int x, int y, int duration, long delayMillis, OnSmoothMoveListener listener) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }
        if (mView.getScrollX() != x || mView.getScrollY() != y) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(x, y, duration, listener);
            if (delayMillis > 0) {
                mHandler.postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                mHandler.post(mSmoothScrollRunnable);
            }
        }
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
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }
        int duration = Math.abs((y - mView.getScrollY()) * 2);
        if (mView.getScrollX() != x || mView.getScrollY() != y) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(x, y, duration, listener);
            if (delayMillis > 0) {
                mView.postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                mView.post(mSmoothScrollRunnable);
            }
        }
    }

    /**
     * 根据时间进行滚动的Runnable
     */
    private class SmoothScrollRunnable implements Runnable {
        protected final Interpolator mInterpolator;
        protected final int mStartX;
        protected final int mStartY;
        protected final int mStopX;
        protected final int mStopY;
        protected int mDuration;
        protected boolean mContinueRunning = true;
        protected long mStartTime = -1;
        protected int mCurrentX = -1;
        protected int mCurrentY = -1;
        protected OnSmoothMoveListener mListener;

        public SmoothScrollRunnable(int stopX, int stopY, int duration, OnSmoothMoveListener listener) {
            mStartX = mView.getScrollX();
            mStartY = mView.getScrollY();
            mStopX = stopX;
            mStopY = stopY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
            mListener = listener;
        }

        @Override
        public void run() {
            isRunning = true;
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {
                if (mDuration == 0) {
                    mView.scrollTo(mStopX, mStopY);
                    return;
                }
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
                int dX;
                int dY;
                if (mStopX - mStartX == 0) {
                    dX = 0;
                } else {
                    dX = Math.round((mStopX - mStartX) * mInterpolator.getInterpolation(normalizedTime / 1000f));
                }
                if (mStopY - mStartY == 0) {
                    dY = 0;
                } else {
                    dY = Math.round((mStopY - mStartY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
                }
                mCurrentX = mStartX + dX;
                mCurrentY = mStartY + dY;
                mView.scrollTo(mCurrentX, mCurrentY);
            }

            if (mContinueRunning && (mStopY != mCurrentY || mStopX != mCurrentX)) {
                mHandler.postDelayed(this, 16);
            } else {
                isRunning = false;
//                mView.scrollTo(mStopX, mStopY);
                if (null != mListener) {
                    mListener.onSmoothScrollFinished();
                }
            }
        }

        /**
         * 停止滚动
         */
        public void stop() {
            mContinueRunning = false;
            mListener = null;
        }
    }

}
