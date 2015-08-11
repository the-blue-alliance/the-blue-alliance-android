package com.thebluealliance.androidclient.helpers;

import android.support.v4.view.ViewPager;

import com.thebluealliance.androidclient.interfaces.BindableFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentBinder implements ViewPager.OnPageChangeListener {

    int mSelectedPage = 0;
    // TODO: fragments should track bound state internally
    List<Integer> boundPages = new ArrayList<>();
    private BindableFragmentPagerAdapter mFragmentAdapter;

    public void setAdapter(BindableFragmentPagerAdapter adapter) {
        mFragmentAdapter = adapter;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mFragmentAdapter == null) {
            return;
        }
        mSelectedPage = position;
        if (!boundPages.contains(position)) {
            mFragmentAdapter.bindFragmentAtPosition(mSelectedPage);
            boundPages.add(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mFragmentAdapter == null) {
            return;
        }
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (mSelectedPage - 1 >= 0 && !boundPages.contains(mSelectedPage - 1)) {
                mFragmentAdapter.bindFragmentAtPosition(mSelectedPage - 1);
                boundPages.add(mSelectedPage - 1);
            }
            if (mSelectedPage + 1 < mFragmentAdapter.getCount() && !boundPages.contains(mSelectedPage + 1)) {
                mFragmentAdapter.bindFragmentAtPosition(mSelectedPage + 1);
                boundPages.add(mSelectedPage + 1);
            }
        }
    }
}
