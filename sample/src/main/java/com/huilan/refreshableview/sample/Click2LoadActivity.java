package com.huilan.refreshableview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableListView;

import java.util.ArrayList;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Click2LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable_listview);
        RefreshableLayout refreshableLayout = (RefreshableLayout) findViewById(R.id.rl_rl);
        RefreshableListView refreshableListview = (RefreshableListView) findViewById(R.id.rl_listview);

        refreshableLayout.setHeaderEnable();
        refreshableLayout.setFooterEnable();

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            list.add("这是listView的数据" + i);
        }
        TestBaseAdapter testBaseAdapter = new TestBaseAdapter(list);

        refreshableListview.setAdapter(testBaseAdapter);
        refreshableLayout.setOnRefreshListener(new TestOnRefreshListener(testBaseAdapter, list, refreshableLayout));

    }
}
