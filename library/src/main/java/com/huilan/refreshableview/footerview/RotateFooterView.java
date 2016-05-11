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
 * 下拉刷新headerview
 * Created by liudenghui on 14-8-8.
 */
public class RotateFooterView extends CustomView {

    private TextView header_text_1;
    private TextView header_text_2;
    private IPullAnimation mPullAnimation;

    public RotateFooterView(Context context) {
        super(context);
    }

    public RotateFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initView() {
        inflate(getContext(), R.layout.refreshableview_rotate_header, this);
        header_text_1 = (TextView) findViewById(R.id.header_text_1);
        header_text_2 = (TextView) findViewById(R.id.header_text_2);
        ImageView header_image = (ImageView) findViewById(R.id.header_image);
        mPullAnimation = new RotatePullAnimation(header_image);
    }

    @Override
    public void onStart() {
        header_text_1.setText("松开加载");
    }

    @Override
    public void onPull(int d, int canRefresh) {
        mPullAnimation.onPull(d, canRefresh);
    }

    @Override
    public void onPrepare() {
        header_text_1.setText("上拉加载");
    }

    @Override
    public void onFinished(RefreshResult result) {
        header_text_1.setText(result.getMessage());
        mPullAnimation.stop();
    }

    @Override
    public void onRefreshing() {
        header_text_1.setText("正在加载");
        mPullAnimation.start();
    }

    public void setLastUpdateTime(String time) {
        header_text_2.setVisibility(VISIBLE);
        header_text_2.setText(time);
    }
}
