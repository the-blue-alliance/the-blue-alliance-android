package com.thebluealliance.androidclient.binders;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.databinding.FragmentEventListFragmentPagerBinding;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.models.EventWeekTab;

import java.util.List;

public class EventTabBinder extends AbstractDataBinder<List<EventWeekTab>, FragmentEventListFragmentPagerBinding> {

    private EventsByWeekFragment mFragment;
    private List<EventWeekTab> mTabs;

    public void setFragment(EventsByWeekFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void updateData(@Nullable List<EventWeekTab> data) {
        if (data != null && !data.isEmpty() && !data.equals(mTabs)) {
            mTabs = data;
            mFragment.updateLabels(mTabs);
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void bindViews() {
        mBinding = FragmentEventListFragmentPagerBinding.bind(mRootView);
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            mBinding = null;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        TbaLogger.e("Error fetching event years");
        throwable.printStackTrace();
    }
}
