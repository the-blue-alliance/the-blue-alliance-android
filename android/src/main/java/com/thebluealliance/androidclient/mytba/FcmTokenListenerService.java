package com.thebluealliance.androidclient.mytba;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;
import com.thebluealliance.androidclient.gcm.GcmController;

import javax.inject.Inject;

/**
 * Service to send the newly registered user's GCM tokens to the backend
 */
public class FcmTokenListenerService extends FirebaseInstanceIdService {

    @Inject FirebaseInstanceId mFirebaseInstanceId;
    @Inject GcmController mGcmController;
    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public FcmTokenListenerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        String regid = mFirebaseInstanceId.getToken();

        TbaLogger.d("Device registered with FCM, ID: " + regid);
        mGcmController.storeRegistrationId(regid);

        boolean storeOnServer = mMyTbaDatafeed.register(regid);
        if (storeOnServer) {
            TbaLogger.d("Successfully sent FCM token to backend");
        } else {
            TbaLogger.d("Unable to send FCM token to backend");
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
