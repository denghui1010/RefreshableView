package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by liudenghui on 14-8-11.
 */
public class RefreshableGridView extends RefreshableBase<GridView> {

    private GridView mGridView;

    public RefreshableGridView(Context context) {
        super(context);
    }

    public RefreshableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected GridView createContentView() {
        mGridView = new GridView(getContext());
        return mGridView;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return false;
    }

    @Override
    protected boolean isContentViewAtTop() {
        return false;
    }

    public void setEmptyView(final View emptyView){
        addView(emptyView);
        mGridView.setEmptyView(emptyView);
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = getMeasuredHeight();
                emptyView.setLayoutParams(layoutParams);
            }
        });
    }
}
