package com.huilan.refreshableview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableListView;

import java.util.ArrayList;

/**
 * 在ViewPager中测试
 * Created by liudenghui on 14-9-23.
 */
public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_refreshable_listview, container, false);
        RefreshableLayout refreshableLayout = (RefreshableLayout) view.findViewById(R.id.rl_rl);
        RefreshableListView refreshableListview = (RefreshableListView) view.findViewById(R.id.rl_listview);

        refreshableLayout.setHeaderEnable();
        refreshableLayout.setFooterEnable();
        refreshableLayout.setContentViewShadowEnable(true, true);
        refreshableLayout.setContentViewShadowElevation(20, 20);
        refreshableLayout.setAutoRefresh(true);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            list.add("这是listView的数据" + i);
        }
        TestBaseAdapter testBaseAdapter = new TestBaseAdapter(list);

        refreshableListview.setAdapter(testBaseAdapter);
        refreshableLayout.setOnRefreshListener(new TestOnRefreshListener(testBaseAdapter, list, refreshableLayout));
        return view;
    }
}
