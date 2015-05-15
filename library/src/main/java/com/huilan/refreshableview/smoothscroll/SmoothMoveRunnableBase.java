package com.huilan.refreshableview.smoothscroll;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 平滑滚动基类
 * Created by liudenghui on 14-10-9.
 */
public class SmoothMoveRunnableBase implements Runnable {
    protected final Interpolator mInterpolator;
    protected final int mStartX;
    protected final int mStartY;
    protected final int mStopX;
    protected final int mStopY;
    protected OnSmoothMoveFinishedListener mListener;
    protected int mDuration;
    protected boolean mContinueRunning = true;
    protected long mStartTime = -1;
    protected int mCurrentX = -1;
    protected int mCurrentY = -1;
    protected View mView;

    public SmoothMoveRunnableBase(View view, int startX, int startY, int stopX, int stopY, int duration,
                                  OnSmoothMoveFinishedListener listener) {
        mStartX = startX;
        mStartY = startY;
        mStopX = stopX;
        mStopY = stopY;
        mDuration = duration;
        mInterpolator = new DecelerateInterpolator();
        mListener = listener;
        mView = view;
    }

    @Override
    public void run() {

    }

    public void stop() {
        mContinueRunning = false;
        mListener = null;
    }
}
