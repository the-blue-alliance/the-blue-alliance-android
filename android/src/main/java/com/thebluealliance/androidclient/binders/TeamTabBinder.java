package com.thebluealliance.androidclient.binders;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.thebluealliance.androidclient.adapters.TeamListFragmentPagerAdapter;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TeamTabBinder extends AbstractDataBinder<Integer> {

    public ViewPager2 viewPager;
    public TabLayout tabs;
    public FragmentActivity fragmentActivity;

    private Integer oldData;
    private Unbinder unbinder;
    private int mInitialTab;
    private TeamListFragmentPagerAdapter adapter;

    @Inject
    public TeamTabBinder() {
        super();
        mInitialTab = 0;
    }

    public void setInitialTab(int initialTab) {
        mInitialTab = initialTab;
    }

    public void setupAdapter() {
        adapter = new TeamListFragmentPagerAdapter(fragmentActivity);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("1-999");
                default: {
                    String title = (position * 1000) + "-" + ((position * 1000) + 999);
                    tab.setText(title);
                }
        }}).attach();
    }

    @Override
    public void updateData(@Nullable Integer data) {
        if (data != null && oldData != null && data.equals(oldData)) {
            // No need to update anything
            return;
        }
        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        viewPager.post(() -> {
            adapter.setMaxTeamNumber(data == null ? 0 : data);
            viewPager.setCurrentItem(mInitialTab);
        });

        oldData = data;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void bindViews() {
        unbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews && unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
