package com.thebluealliance.androidclient.mytba;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;
import com.thebluealliance.androidclient.gcm.GcmController;

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

public class SendFcmTokenToBackend extends IntentService {

    @Inject GcmController mGcmController;
    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public SendFcmTokenToBackend() {
        super("send-fcm-token");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String regid = mGcmController.getRegistrationId();
        if (regid.isEmpty()) {
            TbaLogger.w("No token available to send to backend");
            return;
        }

        TbaLogger.d("Found FCM token: " + regid);

        boolean storeOnServer = mMyTbaDatafeed.register(regid);
        if (storeOnServer) {
            TbaLogger.d("Storing registration ID");
            // we had success on the server. Now store locally
            // Store the registration ID locally, so we don't have to do this again
            mGcmController.storeRegistrationId(regid);
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
