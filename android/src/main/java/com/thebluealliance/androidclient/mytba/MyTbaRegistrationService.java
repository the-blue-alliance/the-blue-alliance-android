package com.thebluealliance.androidclient.mytba;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;
import com.thebluealliance.androidclient.gcm.GcmController;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Service to send the newly registered user's GCM tokens to the backend
 */
public class MyTbaRegistrationService extends IntentService {

    @Inject GoogleCloudMessaging mGoogleCloudMessaging;
    @Inject GcmController mGcmController;
    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public MyTbaRegistrationService() {
        super("Register MyTBA");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String senderId = mGcmController.getSenderId();
            String regid = mGoogleCloudMessaging.register(senderId);

            Log.d(Constants.LOG_TAG, "Device registered with GCM, ID: " + regid);

            boolean storeOnServer = mMyTbaDatafeed.register(regid);
            if (storeOnServer) {
                Log.d(Constants.LOG_TAG, "Storing registration ID");
                // we had success on the server. Now store locally
                // Store the registration ID locally, so we don't have to do this again
                mGcmController.storeRegistrationId(regid);
            }
        } catch (IOException ex) {
            Log.e(Constants.LOG_TAG, "Error registering gcm:" + ex.getMessage());
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
    }

    private MyTbaComponent getComponenet() {
        TBAAndroid application = ((TBAAndroid) getApplication());
        return DaggerMyTbaComponent.builder()
                                   .applicationComponent(application.getComponent())
                                   .gceModule(application.getGceModule())
                                   .authModule(application.getAuthModule())
                                   .gcmModule(application.getGcmModule())
                                   .build();
    }
}
