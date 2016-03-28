package com.huilan.refreshableview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.huilan.refreshableview.RefreshableLayout;
import com.huilan.refreshableview.weight.RefreshableListView;

import java.util.ArrayList;

/**
 * Created by liudenghui on 14-8-8.
 */
public class Pull2RefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable_listview);
        RefreshableLayout refreshableLayout = (RefreshableLayout) findViewById(R.id.rl_rl);
        RefreshableListView refreshableListview = (RefreshableListView) findViewById(R.id.rl_listview);
        refreshableListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "点击了"+position, Toast.LENGTH_SHORT).show();
            }
        });

        refreshableLayout.setHeaderEnable();
        refreshableLayout.setFooterEnable();
        refreshableLayout.setContentViewShadowEnable(true, true);
        refreshableLayout.setContentViewShadowElevation(20, 20);
//        refreshableLayout.setAutoRefresh(true);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            list.add("这是listView的数据" + i);
        }
        TestBaseAdapter testBaseAdapter = new TestBaseAdapter(list);

        refreshableListview.setAdapter(testBaseAdapter);
        refreshableLayout.setOnRefreshListener(new TestOnRefreshListener(testBaseAdapter, list, refreshableLayout));
    }
}
