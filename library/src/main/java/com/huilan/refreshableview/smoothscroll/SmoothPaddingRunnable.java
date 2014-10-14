package com.huilan.refreshableview.smoothscroll;

import android.os.Build;
import android.view.View;

/**
 * 平滑滚动,改变的是padding的值
 * Created by liudenghui on 14-10-9.
 */
public class SmoothPaddingRunnable extends SmoothMoveRunnableBase {

    public SmoothPaddingRunnable(View view, int startX, int startY, int stopX, int stopY, int duration,
                                 OnSmoothMoveFinishedListener listener) {
        super(view, -startX, -startY, -stopX, -stopY, duration, listener);
    }

    @Override
    public void run() {
        /**
         * Only set mStartTime if this is the first time we're starting,
         * else actually calculate the Y delta
         */
        if (mStartTime == -1) {
            mStartTime = System.currentTimeMillis();
        } else {

            /**
             * We do do all calculations in long to reduce software float
             * calculations. We use 1000 as it gives us good accuracy and
             * small rounding errors
             */
            if (mDuration == 0) {
//                    mView.scrollTo(mStopX, mStopY);
                mView.setPadding(mStopX, mStopY, mView.getPaddingRight(), mView.getPaddingBottom());
                return;
            }
            long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
            normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
            int dX;
            int dY;
            if (mStartX - mStopX == 0) {
                dX = 0;
            } else {
                dX = Math.round((mStopX - mStartX) * mInterpolator.getInterpolation(normalizedTime / 1000f));
            }
            if (mStartY - mStopY == 0) {
                dY = 0;
            } else {
                dY = Math.round((mStopY - mStartY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
            }
            mCurrentX = mStartX + dX;
            mCurrentY = mStartY + dY;
//                mView.scrollTo(mCurrentX, mCurrentY);
            mView.setPadding(mCurrentX, mCurrentY, mView.getPaddingRight(), mView.getPaddingBottom());
        }

        // If we're not at the target Y, keep going...
        if (mContinueRunning && (mStopY != mCurrentY || mStopX != mCurrentX)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mView.postOnAnimation(this);
            } else {
                mView.postDelayed(this, 16);
            }
        } else {
//                mView.scrollTo(mStopX, mStopY);
            mView.setPadding(mStopX, mStopY, mView.getPaddingRight(), mView.getPaddingBottom());
            if (null != mListener) {
                mListener.onSmoothScrollFinished();
            }
        }
    }

}