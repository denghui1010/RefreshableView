package com.huilan.refreshableview.animation;

import com.huilan.refreshableview.R;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * 下拉旋转动画
 * Created by liudenghui on 14-10-13.
 */
public class RotatePullAnimation implements IPullAnimation {
    private static final int ROTATION_ANIMATION_DURATION = 600;
    private ImageView mImageView;
    private android.view.animation.RotateAnimation mRotateAnimation;
    private Matrix mHeaderImageMatrix;

    public RotatePullAnimation(ImageView imageView) {
        mImageView = imageView;
        init();
    }

    @Override
    public void onPull(int dx, int canRefresh) {
        mHeaderImageMatrix.setRotate((float) dx / canRefresh * 180, mImageView.getWidth() / 2, mImageView.getHeight() / 2);
        mImageView.setImageMatrix(mHeaderImageMatrix);
    }

    @Override
    public void reset() {
        mImageView.clearAnimation();
        mHeaderImageMatrix.reset();
        mImageView.setImageMatrix(mHeaderImageMatrix);
    }

    @Override
    public void start() {
        mImageView.startAnimation(mRotateAnimation);
    }

    protected void init() {
        mImageView.setImageResource(R.drawable.refresh);
        mImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        mImageView.setImageMatrix(mHeaderImageMatrix);
        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }
}
