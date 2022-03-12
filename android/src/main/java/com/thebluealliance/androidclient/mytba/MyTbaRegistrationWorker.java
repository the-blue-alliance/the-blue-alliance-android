package com.thebluealliance.androidclient.mytba;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.gcm.GcmController;

import javax.annotation.Nullable;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

/**
 * Service to send the newly registered user's GCM tokens to the backend
 */
@HiltWorker
public class MyTbaRegistrationWorker extends Worker {

    @Nullable
    final FirebaseMessaging mFirebaseMessaging;
    final GcmController mGcmController;
    final MyTbaDatafeed mMyTbaDatafeed;

    public static void run(Context context) {
       OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyTbaUpdateWorker.class)
                .addTag("register-mytba")
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build();
        WorkManager.getInstance(context)
                .enqueue(workRequest);
    }

    @AssistedInject
    public MyTbaRegistrationWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters params,
            @Nullable FirebaseMessaging firebaseMessaging,
            GcmController gcmController,
            MyTbaDatafeed myTbaDatafeed) {
        super(context, params);
        this.mFirebaseMessaging = firebaseMessaging;
        this.mGcmController = gcmController;
        this.mMyTbaDatafeed = myTbaDatafeed;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (mFirebaseMessaging == null) {
            TbaLogger.w("Can't load FirebaseMessaging, skipping registration");
            return Result.success();
        }
        try {
            Task<String> tokenTask = mFirebaseMessaging.getToken();
            String regId = Tasks.await(tokenTask);

            TbaLogger.d("Device registered with GCM, ID: " + regId);

            boolean storeOnServer = mMyTbaDatafeed.register(regId);
            if (storeOnServer) {
                TbaLogger.d("Storing registration ID");
                // we had success on the server. Now store locally
                // Store the registration ID locally, so we don't have to do this again
                mGcmController.storeRegistrationId(regId);
            }
        } catch (Exception ex) {
            TbaLogger.e("Error registering gcm:" + ex.getMessage());
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return Result.success();
    }
}
