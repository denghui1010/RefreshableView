package com.huilan.refreshableview.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

/**
 * Created by liudenghui on 14-9-23.
 */
public class ViewPagerPull2RActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_p2r);
        initView();
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.test_viewpager);
        viewPager.setAdapter(new TabPageIndicatorViewPageAdapter(getSupportFragmentManager()));
    }

    public class TabPageIndicatorViewPageAdapter extends FragmentStatePagerAdapter {

        public TabPageIndicatorViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            return new TestFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

    }

}
