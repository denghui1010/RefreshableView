package com.huilan.refreshableview.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * 下拉旋转动画
 * Created by liudenghui on 14-10-13.
 */
public class RotatePullAnimation implements IPullAnimation {
    private ImageView mImageView;
    private ObjectAnimator mRotateAnim;

    public RotatePullAnimation(ImageView imageView) {
        mImageView = imageView;
        initAnim();
    }

    @Override
    public void onPull(int dx, int canRefresh) {
        float angle = (float) dx / canRefresh * 180;
        System.out.println("angle=" + angle);
        mImageView.setRotation(angle);
    }

    @Override
    public void reset() {
        mRotateAnim.end();
    }

    @Override
    public void start() {
        mRotateAnim.start();
    }

    @Override
    public void stop() {
        mRotateAnim.cancel();
    }

    private void initAnim() {
        mRotateAnim = ObjectAnimator.ofFloat(mImageView, "rotation", 0f, 360f);
        mRotateAnim.setInterpolator(new LinearInterpolator());
        mRotateAnim.setRepeatMode(ValueAnimator.INFINITE);
        mRotateAnim.setRepeatCount(Integer.MAX_VALUE);
        mRotateAnim.setDuration(800);
    }
}
