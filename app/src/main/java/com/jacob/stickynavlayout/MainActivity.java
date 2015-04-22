package com.jacob.stickynavlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ViewPagerIndicator mViewPagerIndicator;
    private List<String> mListTabs = Arrays.asList("简介", "评论", "关于");
    private TabFragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.id_layout_viewpager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mFragments = new TabFragment[mListTabs.size()];

        for (int i = 0; i < mListTabs.size(); i++)
        {
            mFragments[i] = TabFragment.newInstance();
        }

        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.id_layout_indicator);
        mViewPagerIndicator.setTabItems(mListTabs);
        mViewPagerIndicator.setViewPager(mViewPager, 0);
        mViewPagerIndicator.setOnPageChangeListener(new ViewPagerIndicator.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments[i];
        }

        @Override
        public int getCount() {
            return mListTabs.size();
        }
    }
}
