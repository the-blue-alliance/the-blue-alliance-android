package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;

import com.thebluealliance.androidclient.Log;

import java.util.Arrays;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class YearsParticipatedDropdownSubscriber extends Subscriber<JsonArray> {

    private final YearsParticipatedUpdate mCallback;

    @Inject
    public YearsParticipatedDropdownSubscriber(YearsParticipatedUpdate callback) {
        mCallback = callback;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.e("Error fetching team years");
        e.printStackTrace();
    }

    @Override
    public void onNext(JsonArray apiYears) {
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
