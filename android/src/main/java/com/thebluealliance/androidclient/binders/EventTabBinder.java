package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.models.EventWeekTab;

import android.support.annotation.Nullable;
import com.thebluealliance.androidclient.Log;

import java.util.List;

import butterknife.ButterKnife;

public class EventTabBinder extends AbstractDataBinder<List<EventWeekTab>> {

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
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            ButterKnife.unbind(this);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("Error fetching event years");
        throwable.printStackTrace();
    }
}
