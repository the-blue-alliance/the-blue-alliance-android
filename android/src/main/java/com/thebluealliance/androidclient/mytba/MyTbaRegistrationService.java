package com.thebluealliance.androidclient.mytba;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.messaging.FirebaseMessaging;
import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;
import com.thebluealliance.androidclient.gcm.GcmController;


import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Service to send the newly registered user's GCM tokens to the backend
 */
public class MyTbaRegistrationWorker extends Worker {

    public static final int JOB_ID = 125;

    @Inject @Nullable
    FirebaseMessaging mFirebaseMessaging;
    @Inject GcmController mGcmController;
    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public MyTbaRegistrationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mFirebaseMessaging == null) {
            TbaLogger.w("Can't load FirebaseMessaging, skipping registration");
            return;
        }
        try {
            String regid = mFirebaseMessaging.getToken().getResult();

            TbaLogger.d("Device registered with GCM, ID: " + regid);

            boolean storeOnServer = mMyTbaDatafeed.register(regid);
            if (storeOnServer) {
                TbaLogger.d("Storing registration ID");
                // we had success on the server. Now store locally
                // Store the registration ID locally, so we don't have to do this again
                mGcmController.storeRegistrationId(regid);
            }
        } catch (Exception ex) {
            TbaLogger.e("Error registering gcm:" + ex.getMessage());
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
    }

    private MyTbaComponent getComponenet() {
        TbaAndroid application = ((TbaAndroid) getApplication());
        return DaggerMyTbaComponent.builder()
                                   .applicationComponent(application.getComponent())
                                   .gceModule(application.getGceModule())
                                   .authModule(application.getAuthModule())
                                   .gcmModule(application.getGcmModule())
                                   .build();
    }
}
