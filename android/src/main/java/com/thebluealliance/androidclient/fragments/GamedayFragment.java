package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.GamedayFragmentPagerAdapter;
import com.thebluealliance.androidclient.databinding.FragmentPagerWithTabsBinding;

public class GamedayFragment extends Fragment {

    public static final String SELECTED_TAB = "selected_tab";

    private FragmentPagerWithTabsBinding mBinding;

    private int mInitialTab;

    public static GamedayFragment newInstance(int tab) {
        GamedayFragment f = new GamedayFragment();
        Bundle args = new Bundle();
        args.putInt(SELECTED_TAB, tab);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mInitialTab = savedInstanceState.getInt(SELECTED_TAB, 0);
        } else if (getArguments() != null) {
            mInitialTab = getArguments().getInt(SELECTED_TAB, 0);
        } else {
            mInitialTab = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPagerWithTabsBinding.inflate(inflater, container, false);
        View v = mBinding.getRoot();

        // Make this ridiculously big
        mBinding.pager.setOffscreenPageLimit(50);
        mBinding.pager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));

        ViewCompat.setElevation(mBinding.tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        mBinding.pager.post(() -> {
            mBinding.pager.setAdapter(new GamedayFragmentPagerAdapter(getChildFragmentManager()));
            mBinding.tabs.setViewPager(mBinding.pager);
            mBinding.pager.setCurrentItem(mInitialTab);
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBinding != null) {
            outState.putInt(SELECTED_TAB, mBinding.pager.getCurrentItem());
        }
    }
}
