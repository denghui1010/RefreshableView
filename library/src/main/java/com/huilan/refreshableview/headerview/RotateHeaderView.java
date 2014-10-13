package com.huilan.refreshableview.headerview;

import com.huilan.refreshableview.CustomView;
import com.huilan.refreshableview.R;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.animation.IPullAnimation;
import com.huilan.refreshableview.animation.RotatePullAnimation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 下拉刷新headerview
 * Created by liudenghui on 14-8-8.
 */
public class RotateHeaderView extends CustomView {

    private TextView header_text_1;
    private TextView header_text_2;
    private IPullAnimation mPullAnimation;

    public RotateHeaderView(Context context) {
        super(context);
        init();
    }

    public RotateHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotateHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void canRefresh() {
        header_text_1.setText("松开刷新");
    }

    @Override
    public void onPull(int d, int canRefresh) {
        mPullAnimation.onPull(d, canRefresh);
    }

    @Override
    public void originSate() {
        header_text_1.setText("下拉刷新");
        mPullAnimation.reset();
    }

    @Override
    public void refreshFinished(RefreshResult result) {
        switch (result) {
            case hasmore:
            case nomore:
                header_text_1.setText("刷新成功");
                mPullAnimation.reset();
                break;
            case failure:
                header_text_1.setText("刷新失败");
                mPullAnimation.reset();
        }
    }

    @Override
    public void refreshing() {
        header_text_1.setText("正在刷新");
        mPullAnimation.start();
    }

    @Override
    public void setLastUpdateTime(String time) {
        header_text_2.setVisibility(VISIBLE);
        header_text_2.setText(time);
    }

    private void init() {
        inflate(getContext(), R.layout.rotate_header, this);
        header_text_1 = (TextView) findViewById(R.id.header_text_1);
        header_text_2 = (TextView) findViewById(R.id.header_text_2);
        ImageView header_image = (ImageView) findViewById(R.id.header_image);
        mPullAnimation = new RotatePullAnimation(header_image);
    }

}
