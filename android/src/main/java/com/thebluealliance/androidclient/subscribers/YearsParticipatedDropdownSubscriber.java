package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class YearsParticipatedDropdownSubscriber implements Action1<JsonArray> {

    private final YearsParticipatedUpdate mCallback;

    @Inject
    public YearsParticipatedDropdownSubscriber(YearsParticipatedUpdate callback) {
        mCallback = callback;
    }

    @Override
    public void call(JsonArray apiYears) {
        if (apiYears == null) {
            return;
        }
        int[] years = new int[apiYears.size()];
        for (int i = apiYears.size() - 1; i >= 0; i--) {
            years[i] = apiYears.get(i).getAsInt();
        }

        AndroidSchedulers.mainThread().createWorker()
          .schedule(() -> mCallback.updateYearsParticipated(years));
    }
}
