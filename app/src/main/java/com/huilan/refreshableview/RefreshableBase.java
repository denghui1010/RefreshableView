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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public abstract class RefreshableBase<T extends View> extends LinearLayout {

	public static final String LOG_TAG = "RefreshableView";
	public final int SMOOTH_SCROLL_DURATION = 200;
    private T refreshableView;
    private View headerView;
    private View footerView;

	private OnHeaderRefreshListener onHeaderRefreshListener;
	private OnFooterRefreshListener onFooterRefreshListener;
    private HeaderRefreshMode headerRefreshMode = HeaderRefreshMode.CLOSE;
    private FooterRefreshMode footerRefreshMode = FooterRefreshMode.CLOSE;

	public RefreshableBase(Context context) {
		super(context);
		init();
        createRefreshableView(context,null);
	}

	public RefreshableBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
        createRefreshableView(context,attrs);
	}

    /**
     *
     * @param child 添加的子view
     * @param index 添加的位置
     * @param params layoutparams
     */
    @Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		T coreView = getRefreshableView();
		if (coreView instanceof ViewGroup) {
			((ViewGroup) coreView).addView(child, index, params);
		} else {
			throw new UnsupportedOperationException("Core View is not a ViewGroup so can't addView");
		}
	}

    /**
     * 获取view
     * @return 核心view
     */
	public T getRefreshableView() {
		return refreshableView;
	}

    /**
     * 获取顶部刷新模式
     * @return HeaderRefreshMode 顶部刷新模式
     */
    public HeaderRefreshMode getHeaderRefreshMode() {
        return headerRefreshMode;
    }

    /**
     * 获取底部刷新模式
     * @return FooterRefreshMode 底部刷新模式
     */
    public FooterRefreshMode getFooterRefreshMode() {
        return footerRefreshMode;
    }

    /**
     * 设置顶部刷新模式
     * @param headerRefreshMode 顶部刷新模式
     */
    public void setHeaderRefreshMode(HeaderRefreshMode headerRefreshMode) {
        this.headerRefreshMode = headerRefreshMode;
    }

    /**
     * 设置底部刷新模式
     * @param footerRefreshMode 底部刷新模式
     */
    public void setFooterRefreshMode(FooterRefreshMode footerRefreshMode) {
        this.footerRefreshMode = footerRefreshMode;
    }

    private void init(){
    }

    protected void addHeaderView(View headerView){
        addHeaderView(headerView,HeaderRefreshMode.REQUIRE_PULL);
    }

    protected void addHeaderView(View headerView, HeaderRefreshMode headerRefreshMode){
        this.headerView = headerView;
        this.headerRefreshMode = headerRefreshMode;
        addView(headerView,0);
    }

    protected void addFooderView(View footerView){
        addFooterView(footerView, FooterRefreshMode.REQUIRE_PULL);
    }

    protected void addFooterView(View footerView, FooterRefreshMode footerRefreshMode){
        this.footerView = footerView;
        this.footerRefreshMode = footerRefreshMode;
        addView(footerView,2);
    }

    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

}
