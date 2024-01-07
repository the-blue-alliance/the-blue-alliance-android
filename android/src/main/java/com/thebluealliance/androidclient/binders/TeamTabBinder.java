package com.thebluealliance.androidclient.binders;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.thebluealliance.androidclient.adapters.TeamListFragmentPagerAdapter;
import com.thebluealliance.androidclient.databinding.FragmentTeamListFragmentPagerBinding;

import javax.inject.Inject;

public class TeamTabBinder extends AbstractDataBinder<Integer, FragmentTeamListFragmentPagerBinding> {

    public ViewPager2 viewPager;
    public TabLayout tabs;
    public Fragment parentFragment;

    private Integer oldData;
    private int initialTab;
    private TeamListFragmentPagerAdapter adapter;
    private TabLayoutMediator tabLayoutMediator;

    @Inject
    public TeamTabBinder() {
        super();
        initialTab = 0;
    }

    public void setInitialTab(int initialTab) {
        this.initialTab = initialTab;
    }

    public void setupAdapter() {
        adapter = new TeamListFragmentPagerAdapter(parentFragment);
        viewPager.setAdapter(adapter);

        tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("1-999");
                    break;
                default:
                    String title = (position * 1000) + "-" + ((position * 1000) + 999);
                    tab.setText(title);
                break;
        }});
        tabLayoutMediator.attach();
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
            viewPager.setCurrentItem(initialTab);
        });

        oldData = data;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void bindViews() {
        mBinding = FragmentTeamListFragmentPagerBinding.bind(mRootView);
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews && mBinding != null) {
            tabLayoutMediator.detach();
            mBinding = null;
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
