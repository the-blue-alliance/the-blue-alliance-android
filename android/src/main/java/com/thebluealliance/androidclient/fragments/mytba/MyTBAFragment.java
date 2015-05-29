package com.thebluealliance.androidclient.fragments.mytba;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.AuthenticatorActivity;
import com.thebluealliance.androidclient.adapters.MyTBAFragmentPagerAdapter;
import com.thebluealliance.androidclient.views.SlidingTabs;

/**
 * File created by phil on 8/2/14.
 */
public class MyTBAFragment extends Fragment {

    private ViewPager mViewPager;
    private SlidingTabs mTabs;

    @Override
    public void onResume() {
        super.onResume();
        if (!AccountHelper.isMyTBAEnabled(getActivity())) {
            //show a dialog to reenable myTBA
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final Intent authIntent = AuthenticatorActivity.newInstance(getActivity(), false);
            builder.setTitle("myTBA is Disabled");
            builder.setMessage("Do you want to enable myTBA?").
                    setPositiveButton("Yes", (dialog, which) -> {
                        getActivity().startActivity(authIntent);
                        getActivity().finish();
                        dialog.cancel();
                    }).
                    setNegativeButton("No", (dialog, which) -> {
                        dialog.cancel();
                    });
            builder.create().show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_tba, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.my_tba_pager);
        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));
        mTabs = (SlidingTabs) v.findViewById(R.id.my_tba_tabs);
        ViewCompat.setElevation(mTabs, getResources().getDimension(R.dimen.toolbar_elevation));

        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        mViewPager.post(() -> {
            mViewPager.setAdapter(new MyTBAFragmentPagerAdapter(getChildFragmentManager()));
            mTabs.setViewPager(mViewPager);
        });

        return v;
    }
}
