package com.thebluealliance.androidclient.accounts;

import static com.thebluealliance.androidclient.gcm.notifications.BaseNotification.NOTIFICATION_CHANNEL;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ForegroundInfo;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.helpers.ModelNotificationFavoriteSettings;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class UpdateUserModelSettingsWorker extends Worker {

    private static final String WORK_TAG = "update_mytba_settings";

    private final MyTbaDatafeed mMyTbaDatafeed;

    @AssistedInject
    public UpdateUserModelSettingsWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            MyTbaDatafeed datafeed) {
        super(context, workerParams);
        mMyTbaDatafeed = datafeed;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            ModelNotificationFavoriteSettings settings = ModelNotificationFavoriteSettings.fromWorkData(getInputData());
            @MyTbaDatafeed.ModelPrefsResult int result = mMyTbaDatafeed.updateModelSettings(settings);
            return Result.success(new Data.Builder().putInt("result", result).build());
        } catch (Exception ex) {
            TbaLogger.e("Error updating mytba settings: " + ex.getMessage(), ex);
            return Result.failure();
        }
    }

    @NonNull
    @Override
    public ListenableFuture<ForegroundInfo> getForegroundInfoAsync() {
        Context context = getApplicationContext();

        String title = context.getString(R.string.notification_mytba_prefs_update);
        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentTitle(title)
                .setTicker(title)
                .setOngoing(true)
                .build();

        ForegroundInfo info = new ForegroundInfo(title.hashCode(), notification);
        return Futures.immediateFuture(info);
    }

    public static void runWithCallbacks(AppCompatActivity activity, ModelNotificationFavoriteSettings settings, ModelSettingsCallbacks callbacks) {
        OneTimeWorkRequest updateRequest =
                new OneTimeWorkRequest.Builder(UpdateUserModelSettingsWorker.class)
                        .setInputData(new Data.Builder().putAll(settings.toWorkData()).build())
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build();

        WorkManager workManager = WorkManager.getInstance(activity);
        workManager.enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.KEEP, updateRequest);
        workManager.getWorkInfoByIdLiveData(updateRequest.getId()).observe(activity, info -> {
            if (info != null && info.getState().isFinished()) {
                @MyTbaDatafeed.ModelPrefsResult int result = info.getOutputData().getInt("result", -1);
                switch (result) {
                    case MyTbaDatafeed.MODEL_PREF_SUCCESS:
                        callbacks.onSuccess();
                        break;
                    case MyTbaDatafeed.MODEL_PREF_FAIL:
                        callbacks.onError();
                        break;
                    case MyTbaDatafeed.MODEL_PREF_NOOP:
                        callbacks.onNoOp();
                        break;
                }
            }
        });
    }
}