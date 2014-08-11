package com.huilan.refreshableview.footerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huilan.refreshableview.CustomView;
import com.huilan.refreshableview.R;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Click2LoadFooterView extends CustomView {

    private ProgressBar footer_progressbar;
    private ImageView footer_image;
    private TextView footer_text_1;
    private TextView footer_text_2;


    public Click2LoadFooterView(Context context) {
        super(context);
        init();
    }

    public Click2LoadFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Click2LoadFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.layout_header, this);
        footer_text_1 = (TextView) findViewById(R.id.header_text_1);
        footer_text_2 = (TextView) findViewById(R.id.header_text_2);
        footer_progressbar = (ProgressBar) findViewById(R.id.header_progressbar);
        footer_image = (ImageView) findViewById(R.id.header_image);
    }

    @Override
    public void originSate() {
        footer_text_1.setText("点击加载");
        footer_image.setVisibility(GONE);
        footer_progressbar.setVisibility(GONE);
    }

    @Override
    public void canRefresh() {
        refreshing();
    }

    @Override
    public void refreshing() {
        footer_text_1.setText("正在加载");
        footer_image.setVisibility(GONE);
        footer_progressbar.setVisibility(VISIBLE);
    }

    @Override
    public void refreshFinished(boolean hasMore) {
        if(hasMore){
            originSate();
        } else {
            footer_text_1.setText("没有更多");
            footer_image.setVisibility(GONE);
            footer_progressbar.setVisibility(GONE);
        }
    }
}
