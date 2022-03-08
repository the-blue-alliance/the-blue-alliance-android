package com.thebluealliance.androidclient.mytba;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class MyTbaUpdateWorker extends Worker {

    private static final String UPDATE_FAVORITES = "favorites";
    private static final String UPDATE_SUBSCRIPTIONS = "subscriptions";

    final MyTbaDatafeed mDatafeed;

    public static void run(Context context,
                           boolean updateFavorites,
                           boolean updateSubscriptions) {
        Data jobData = new Data.Builder()
                .putBoolean(UPDATE_FAVORITES, updateFavorites)
                .putBoolean(UPDATE_SUBSCRIPTIONS, updateSubscriptions)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyTbaUpdateWorker.class)
                .addTag("update-mytba")
                .setInputData(jobData)
                .build();
        WorkManager.getInstance(context)
                .enqueue(workRequest);
    }

    @AssistedInject
    public MyTbaUpdateWorker(
            @NonNull @Assisted Context context,
            @NonNull @Assisted WorkerParameters workerParams,
            MyTbaDatafeed datafeed) {
        super(context, workerParams);
        mDatafeed = datafeed;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        boolean updateFavorites = inputData.getBoolean(UPDATE_FAVORITES, true);
        boolean updateSubscriptions = inputData.getBoolean(UPDATE_SUBSCRIPTIONS, true);
        if (updateFavorites) {
            mDatafeed.updateUserFavorites();
        }
        if (updateSubscriptions) {
            mDatafeed.updateUserSubscriptions();
        }

        return Result.success();
    }
}
