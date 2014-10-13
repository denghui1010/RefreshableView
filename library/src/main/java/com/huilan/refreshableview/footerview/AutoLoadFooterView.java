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
 * Created by liudenghui on 14-8-8.
 */
public class AutoLoadFooterView extends CustomView {

    private TextView footer_text_1;
    private TextView footer_text_2;
    private IPullAnimation mPullAnimation;

    public AutoLoadFooterView(Context context) {
        super(context);
        init();
    }

    public AutoLoadFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoLoadFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.rotate_header, this);
        footer_text_1 = (TextView) findViewById(R.id.header_text_1);
        footer_text_2 = (TextView) findViewById(R.id.header_text_2);
        ImageView footer_image = (ImageView) findViewById(R.id.header_image);
        mPullAnimation = new RotatePullAnimation(footer_image);
    }

    @Override
    public void originSate() {
        footer_text_1.setText("加载更多");
        mPullAnimation.reset();
    }

    @Override
    public void canRefresh() {
        refreshing();
    }

    @Override
    public void onPull(int d, int canRefresh) {

    }

    @Override
    public void refreshing() {
        footer_text_1.setText("正在加载");
        mPullAnimation.start();
    }

    @Override
    public void setLastUpdateTime(String time) {
        footer_text_2.setVisibility(VISIBLE);
        footer_text_2.setText(time);
    }

    @Override
    public void refreshFinished(RefreshResult result) {
        switch (result){
            case hasmore:
                originSate();
                break;
            case nomore:
                footer_text_1.setText("没有更多");
                mPullAnimation.reset();
                break;
            case failure:
                footer_text_1.setText("加载失败");
                mPullAnimation.reset();
                break;
        }
    }
}
