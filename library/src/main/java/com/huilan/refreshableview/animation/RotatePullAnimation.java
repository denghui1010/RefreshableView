package com.huilan.refreshableview.animation;

import com.huilan.refreshableview.R;

import android.graphics.Matrix;
import android.os.Build;
import android.widget.ImageView;

/**
 * 下拉旋转动画
 * Created by liudenghui on 14-10-13.
 */
public class RotatePullAnimation implements IPullAnimation {
    private static final int ROTATION_ANIMATION_DURATION = 600;
    private ImageView mImageView;
    private Matrix mHeaderImageMatrix;
    private RotateRunnable mRotateRunnable;
    private float mAngle;

    public RotatePullAnimation(ImageView imageView) {
        mImageView = imageView;
        init();
    }

    @Override
    public void onPull(int dx, int canRefresh) {
        mAngle = (float) dx / canRefresh * 180;
        mHeaderImageMatrix.setRotate(mAngle, mImageView.getWidth() / 2, mImageView.getHeight() / 2);
        mImageView.setImageMatrix(mHeaderImageMatrix);
    }

    @Override
    public void reset() {
        stop();
        mHeaderImageMatrix.reset();
        mImageView.setImageMatrix(mHeaderImageMatrix);
    }

    @Override
    public void start() {
        mRotateRunnable = new RotateRunnable();
        mImageView.post(mRotateRunnable);
    }

    @Override
    public void stop() {
        if (mRotateRunnable != null) {
            mRotateRunnable.stop();
        }
    }

    protected void init() {
        mImageView.setImageResource(R.drawable.refresh);
        mImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        mImageView.setImageMatrix(mHeaderImageMatrix);
    }

    class RotateRunnable implements Runnable {
        private boolean continueRun = true;

        @Override
        public void run() {
            if (!continueRun) {
                return;
            }
            mAngle += 10;
            mHeaderImageMatrix.setRotate(mAngle, mImageView.getWidth() / 2, mImageView.getHeight() / 2);
            mImageView.setImageMatrix(mHeaderImageMatrix);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mImageView.postOnAnimation(this);
            } else {
                mImageView.postDelayed(this, 16);
            }
        }

        public void stop() {
            continueRun = false;
        }
    }
}
