package com.thebluealliance.androidclient.adapters;

import android.content.res.Resources;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.match.MatchBreakdownFragment;
import com.thebluealliance.androidclient.fragments.match.MatchInfoFragment;

public class ViewMatchFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final @StringRes int[] TITLE_IDS = {R.string.match_tab_results,
            R.string.match_tab_breakdown};
    public static final int TAB_RESULT = 0,
            TAB_BREAKDOWN = 1;
    private final String[] mTitles;

    private String mMatchKey;

    public ViewMatchFragmentPagerAdapter(Resources resources, FragmentManager fm, String matchKey) {
        super(fm);
        mMatchKey = matchKey;

        mTitles = new String[TITLE_IDS.length];
        for (int i = 0; i < TITLE_IDS.length; i++) {
            mTitles[i] = resources.getString(TITLE_IDS[i]);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case TAB_RESULT:
                fragment = MatchInfoFragment.newInstance(mMatchKey);
                break;
            case TAB_BREAKDOWN:
                fragment = MatchBreakdownFragment.newInstance(mMatchKey);
                break;
            default:
                fragment = new Fragment();
                break;
        }
        return fragment;
    }
}
