package com.thebluealliance.androidclient.binders;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.models.EventWeekTab;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventTabBinder extends AbstractDataBinder<List<EventWeekTab>> {

    private EventsByWeekFragment mFragment;
    private List<EventWeekTab> mTabs;
    private Unbinder unbinder;

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
        TbaLogger.e("Error fetching event years");
        throwable.printStackTrace();
    }
}
