/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.huilan.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public abstract class RefreshableBase<T extends View> extends LinearLayout {

    public static final String LOG_TAG = "RefreshableView";
    public final int SMOOTH_SCROLL_DURATION = 200;
    private T refreshableView;
    private View headerView;
    private View footerView;
    private LayoutParams headerLayoutParams;
    private LayoutParams footerLayoutParams;

    private OnHeaderRefreshListener onHeaderRefreshListener;
    private OnFooterRefreshListener onFooterRefreshListener;
    private HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    private FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;

    public RefreshableBase(Context context) {
        super(context);
        refreshableView = createRefreshableView(context, null);
        init();
    }

    public RefreshableBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshableView = createRefreshableView(context, attrs);
        init();
    }

    /**
     * 获取view
     *
     * @return 核心view
     */
    public T getRefreshableView() {
        return refreshableView;
    }

    /**
     * 获取顶部刷新模式
     *
     * @return HeaderRefreshMode 顶部刷新模式
     */
    public HeaderRefreshMode getHeaderRefreshMode() {
        return headerRefreshMode;
    }

    /**
     * 获取底部刷新模式
     *
     * @return FooterRefreshMode 底部刷新模式
     */
    public FooterRefreshMode getFooterRefreshMode() {
        return footerRefreshMode;
    }

    /**
     * 设置顶部刷新模式
     *
     * @param headerRefreshMode 顶部刷新模式
     */
    public void setHeaderRefreshMode(HeaderRefreshMode headerRefreshMode) {
        this.headerRefreshMode = headerRefreshMode;
    }

    /**
     * 设置底部刷新模式
     *
     * @param footerRefreshMode 底部刷新模式
     */
    public void setFooterRefreshMode(FooterRefreshMode footerRefreshMode) {
        this.footerRefreshMode = footerRefreshMode;
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(refreshableView, layoutParams);
    }

    protected void setHeaderEnable() {
        headerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_header_height));
        setHeaderEnable(headerLayoutParams, HeaderRefreshMode.PULL);
    }

    protected void setHeaderView(LayoutParams layoutParams){
        setHeaderEnable(layoutParams, HeaderRefreshMode.PULL);
    }

    protected void setHeaderEnable(LayoutParams layoutParams, HeaderRefreshMode headerRefreshMode) {
        this.headerRefreshMode = headerRefreshMode;
        headerLayoutParams = layoutParams;
        createHeaderView();
        addView(headerView,0,layoutParams);
    }

    protected void setFooterEnable(){
        footerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_footer_height));
        setFooterEnable(footerLayoutParams, FooterRefreshMode.AUTO);
    }

    protected void setFooterEnable(LayoutParams layoutParams){
        setFooterEnable(layoutParams, FooterRefreshMode.AUTO);
    }

    protected void setFooterEnable(LayoutParams layoutParams, FooterRefreshMode footerRefreshMode){
        this.footerRefreshMode = footerRefreshMode;
        footerLayoutParams = layoutParams;
        createFooterView();
        addView(footerView,2,layoutParams);
    }

    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    private void createHeaderView() {
        headerView = View.inflate(getContext(), R.layout.layout_header,null);
    }

    private void createFooterView() {
        footerView = View.inflate(getContext(),R.layout.layout_header,null);
    }

    private void setPadding(){

    }

}
