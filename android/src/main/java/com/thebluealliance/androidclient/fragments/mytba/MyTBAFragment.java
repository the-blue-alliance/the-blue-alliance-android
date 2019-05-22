package com.thebluealliance.androidclient.fragments.mytba;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.activities.MyTBAOnboardingActivity;
import com.thebluealliance.androidclient.adapters.MyTBAFragmentPagerAdapter;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.views.SlidingTabs;

import javax.inject.Inject;

public class MyTBAFragment extends Fragment {

    private ViewPager mViewPager;
    private SlidingTabs mTabs;

    @Inject AccountController mAccountController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TbaAndroid application = (TbaAndroid) getActivity().getApplication();
        DaggerMyTbaComponent.builder()
                .tBAAndroidModule(application.getModule())
                .accountModule(application.getAccountModule())
                .authModule(application.getAuthModule())
                .applicationComponent(application.getComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mAccountController.isMyTbaEnabled()) {
            //show a dialog to reenable myTBA
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final Intent loginIntent = new Intent(getActivity(), MyTBAOnboardingActivity.class);
            builder.setTitle("myTBA is Disabled");
            builder.setMessage("Do you want to enable myTBA?").
                    setPositiveButton("Yes", (dialog, which) -> {
                        getActivity().startActivity(loginIntent);
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
