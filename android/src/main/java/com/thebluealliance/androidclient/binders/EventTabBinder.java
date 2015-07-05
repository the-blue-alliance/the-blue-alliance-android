package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;

import java.util.List;

public class EventTabBinder extends AbstractDataBinder<List<String>> {

    private EventsByWeekFragment mFragment;
    private List<String> mLabels;

    public void setFragment(EventsByWeekFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void updateData(@Nullable List<String> data) {
        if (data != null && !data.equals(mLabels)) {
            mLabels = data;
            mFragment.updateLabels(mLabels);
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
