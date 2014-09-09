package com.huilan.refreshableview.headerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huilan.refreshableview.CustomView;
import com.huilan.refreshableview.ICustomView;
import com.huilan.refreshableview.R;
import com.huilan.refreshableview.RefreshResult;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Pull2RefreshHeaderViewI extends CustomView {

    private TextView header_text_1;
    private TextView header_text_2;
    private ImageView header_image;
    private ProgressBar header_progressbar;
    private RotateAnimation rotateAnimation0_180;
    private RotateAnimation rotateAnimation180_360;

    public Pull2RefreshHeaderViewI(Context context) {
        super(context);
        init();
    }

    public Pull2RefreshHeaderViewI(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Pull2RefreshHeaderViewI(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_header, this);
        header_text_1 = (TextView) findViewById(R.id.header_text_1);
        header_text_2 = (TextView) findViewById(R.id.header_text_2);
        header_progressbar = (ProgressBar) findViewById(R.id.header_progressbar);
        header_image = (ImageView) findViewById(R.id.header_image);
        initAnimation();
    }

    @Override
    public void originSate() {
        header_text_1.setText("下拉刷新");
        header_image.startAnimation(rotateAnimation180_360);
        header_image.setVisibility(VISIBLE);
        header_progressbar.setVisibility(INVISIBLE);
    }

    @Override
    public void canRefresh() {
        header_text_1.setText("松开刷新");
        header_image.startAnimation(rotateAnimation0_180);
        header_image.setVisibility(VISIBLE);
        header_progressbar.setVisibility(INVISIBLE);
    }

    @Override
    public void refreshing() {
        header_text_1.setText("正在刷新");
        header_image.clearAnimation();
        header_image.setVisibility(GONE);
        header_progressbar.setVisibility(VISIBLE);
    }

    @Override
    public void refreshFinished(RefreshResult result) {
        switch (result){
            case hasmore:
            case nomore:
                header_text_1.setText("刷新成功");
                header_image.setVisibility(GONE);
                header_progressbar.setVisibility(GONE);
                break;
            case failure:
                header_text_1.setText("刷新失败");
                header_image.setVisibility(GONE);
                header_progressbar.setVisibility(GONE);
        }
    }

    private void initAnimation() {
        rotateAnimation0_180 = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation0_180.setDuration(150);
        rotateAnimation0_180.setFillAfter(true);
        rotateAnimation180_360 = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation180_360.setDuration(150);
        rotateAnimation180_360.setFillAfter(true);
    }

}
