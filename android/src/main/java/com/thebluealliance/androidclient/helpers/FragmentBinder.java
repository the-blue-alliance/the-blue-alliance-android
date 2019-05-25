package com.thebluealliance.androidclient.helpers;

import androidx.viewpager.widget.ViewPager;

import com.thebluealliance.androidclient.interfaces.BindableAdapter;

import javax.inject.Inject;

public class FragmentBinder implements ViewPager.OnPageChangeListener {

    int mSelectedPage;
    int mLastSelectedPage;
    private BindableAdapter mFragmentAdapter;

    @Inject
    public FragmentBinder() {
        mSelectedPage = 0;
        mLastSelectedPage = -1;
    }

    public void setAdapter(BindableAdapter adapter) {
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
        mLastSelectedPage = mSelectedPage;
        mSelectedPage = position;

        // Tell the current fragment it's visible
        mFragmentAdapter.setFragmentVisibleAtPosition(mLastSelectedPage, false);
        mFragmentAdapter.setFragmentVisibleAtPosition(mSelectedPage, true);

        // Bind current page if it hasn't been bound yet
        if (!mFragmentAdapter.isFragmentAtPositionBound(mSelectedPage)) {
            mFragmentAdapter.bindFragmentAtPosition(mSelectedPage);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mFragmentAdapter == null) {
            return;
        }
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (mSelectedPage - 1 >= 0
              && !mFragmentAdapter.isFragmentAtPositionBound(mSelectedPage - 1)) {
                mFragmentAdapter.bindFragmentAtPosition(mSelectedPage - 1);
            }
            if (mSelectedPage + 1 < mFragmentAdapter.getCount()
              && !mFragmentAdapter.isFragmentAtPositionBound(mSelectedPage + 1)) {
                mFragmentAdapter.bindFragmentAtPosition(mSelectedPage + 1);
            }
        }
    }
}
