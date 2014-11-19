package com.huilan.refreshableview.sample;

import com.huilan.imagepager.ImagePager;
import com.huilan.imagepager.OnImagePagerClickListener;
import com.huilan.refreshableview.FooterRefreshMode;
import com.huilan.refreshableview.NotifyListener;
import com.huilan.refreshableview.OnHeaderRefreshListener;
import com.huilan.refreshableview.RefreshResult;
import com.huilan.refreshableview.RefreshableListView;
import com.lidroid.xutils.BitmapUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by liudenghui on 14-9-3.
 */
public class CommonListViewActivity extends Activity {

    private ImagePager mImagePager;
    private ListView mListView;
    private ArrayList<String> mList;
    private ArrayList<String> mTitles;
    private int count;



    public void update(View view) {
        mList.add("http://c.hiphotos.baidu.com/image/pic/item/b812c8fcc3cec3fd6efda57ed488d43f869427fd.jpg");
        mTitles.add("添加" + count++);
        mImagePager.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonlistview);
        initData();
        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.test_listview);

        BitmapUtils bitmapUtils = new BitmapUtils(this);
        mImagePager = new ImagePager(this);
        mImagePager.setBitmapUtil(bitmapUtils);
        mImagePager.setImageUrls(mList);
        mImagePager.setImageTtiles(mTitles);
        mImagePager.notifyDataSetChanged();
        mImagePager.startScroll();
        mImagePager.setOnImagePagerClickListener(new OnImagePagerClickListener() {
            @Override
            public void onClick(int i) {
                Toast.makeText(CommonListViewActivity.this, i+"",Toast.LENGTH_SHORT).show();
            }
        });

        mListView.addHeaderView(mImagePager);
        mListView.setAdapter(new MyAdpter());
    }

    private void initData(){
        mList = new ArrayList<String>();
        mList.add("http://d.hiphotos.baidu.com/image/pic/item/29381f30e924b899aae35b396c061d950b7bf6d7.jpg");
        mList.add("http://d.hiphotos.baidu.com/image/pic/item/cf1b9d16fdfaaf51687447b68e5494eef01f7a20.jpg");
        mList.add("http://b.hiphotos.baidu.com/image/pic/item/b7fd5266d0160924425b6412d60735fae6cd34b9.jpg");

        mTitles = new ArrayList<String>();
        mTitles.add("这是标题0");
        mTitles.add("这是标题1");
        mTitles.add("这是标题2");
    }

    private class MyAdpter extends BaseAdapter {
        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(CommonListViewActivity.this);
            } else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(25);
            tv.setPadding(10, 10, 10, 10);
            tv.setText("这是"+position);
            return tv;
        }

    }

}
