package com.huilan.refreshableview.footerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.huilan.refreshableview.CustomView;
import com.huilan.refreshableview.R;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.animation.IPullAnimation;
import com.huilan.refreshableview.animation.RotatePullAnimation;

/**
 * 自动加载footerview
 * Created by liudenghui on 14-8-8.
 */
public class AutoLoadFooterView extends CustomView {

    private TextView footer_text_1;
    private TextView footer_text_2;
    private IPullAnimation mPullAnimation;
    private ImageView mFooter_image;

    public AutoLoadFooterView(Context context) {
        super(context);
    }

    public AutoLoadFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLoadFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initView() {
        inflate(getContext(), R.layout.refreshableview_rotate_header, this);
        footer_text_1 = (TextView) findViewById(R.id.header_text_1);
        footer_text_2 = (TextView) findViewById(R.id.header_text_2);
        mFooter_image = (ImageView) findViewById(R.id.header_image);
        mPullAnimation = new RotatePullAnimation(mFooter_image);
    }

    @Override
    public void onStart() {
        onRefreshing();
    }

    @Override
    public void onPull(int d, int canRefresh) {

    }

    @Override
    public void onPrepare() {
        footer_text_1.setText("加载更多");
        mFooter_image.setVisibility(VISIBLE);
    }

    @Override
    public void onFinished(RefreshResult result) {
        switch (result) {
            case hasmore:
                onPrepare();
                break;
            case nomore:
                footer_text_1.setText("没有更多");
                mFooter_image.setVisibility(INVISIBLE);
                break;
            case failure:
                footer_text_1.setText("加载失败");
                break;
        }
        mPullAnimation.reset();
    }

    @Override
    public void onRefreshing() {
        footer_text_1.setText("正在加载");
        mPullAnimation.start();
    }

    public void setLastUpdateTime(String time) {
        footer_text_2.setVisibility(VISIBLE);
        footer_text_2.setText(time);
    }
}
