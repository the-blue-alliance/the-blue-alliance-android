package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;

import java.util.Arrays;

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

        /*
        First sort the array, then reverse its order.
        This will put it in descending order (most recent year first).
         */
        Arrays.sort(years);
        for (int i = 0; i < years.length / 2; i++) {
            int temp = years[i];
            int index = years.length - i - 1;
            years[i] = years[index];
            years[index] = temp;
        }

        AndroidSchedulers.mainThread().createWorker()
                .schedule(() -> mCallback.updateYearsParticipated(years));
    }
}
