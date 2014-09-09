package com.huilan.refreshableview.sample;

import com.huilan.imagepager.ImagePager;
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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by liudenghui on 14-9-3.
 */
public class ImagePagerActivity extends Activity implements OnHeaderRefreshListener {

    private ImagePager mImagePager;
    private RefreshableListView mListView;
    private ArrayList<String> mList;
    private ArrayList<String> mTitles;
    private int count;

    @Override
    public void onHeaderRefresh() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.notifyHeaderRefreshFinished(RefreshResult.hasmore, new NotifyListener() {
                    @Override
                    public void notifyDataSetChanged() {
//                listViewAdapter.notifyDataSetChanged();
//                mImagePager.notifyDataSetChanged();
                    }
                });
            }
        },2000);
    }

    public void update(View view) {
        mList.add("http://192.168.0.184:2012/eportal/fileDir/yxw/resource/cms/2014/08/2014081916203359718_small.jpg");
        mTitles.add("添加"+count++);
        mImagePager.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepager);
        initData();
        initView();
        mListView.notifyHeaderRefreshStarted();
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.notifyHeaderRefreshFinished(RefreshResult.hasmore, new NotifyListener() {
                    @Override
                    public void notifyDataSetChanged() {
//                listViewAdapter.notifyDataSetChanged();
//                mImagePager.notifyDataSetChanged();
                    }
                });
            }
        },2000);
    }

    private void initView() {
        mListView = (RefreshableListView) findViewById(R.id.test_listview);
        mListView.setHeaderEnable();
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        mImagePager = new ImagePager(this);
        mImagePager.setBitmapUtil(bitmapUtils);
        mImagePager.setImageUrls(mList);
        mImagePager.setImageTtiles(mTitles);
        mImagePager.notifyDataSetChanged();
        mImagePager.startScroll();

        mListView.addHeaderView(mImagePager);
        mListView.setAdapter(new MyAdpter());
    }

    private void initData(){
        mList = new ArrayList<String>();
        mList.add("http://192.168.0.184:2012/eportal/fileDir/yxw/resource/cms/2014/08/2014081916203359718_small.jpg");
        mList.add("http://192.168.0.184:2012/eportal/fileDir/yxw/resource/cms/2014/08/2014081916244242817.jpg");
        mList.add("http://192.168.0.184:2012/eportal/fileDir/yxw/resource/cms/2014/08/2014081916244242817.jpg");

        mTitles = new ArrayList<String>();
        mTitles.add("这是标题0");
        mTitles.add("这是标题1");
        mTitles.add("这是标题2");
    }

    private class MyAdpter extends BaseAdapter {
        @Override
        public int getCount() {
            return 10;
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
                tv = new TextView(ImagePagerActivity.this);
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
