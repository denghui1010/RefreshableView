package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by liudenghui on 14-8-11.
 */
public class RefreshableGridView extends RefreshableBase<GridView> {
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
        GridView gridView = new GridView(getContext());
        return null;
    }

    @Override
    protected boolean isContentViewAtBottom() {
        return false;
    }

    @Override
    protected boolean isContentViewAtTop() {
        return false;
    }
}
