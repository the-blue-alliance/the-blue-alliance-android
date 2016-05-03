package com.thebluealliance.androidclient.background.mytba;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.gcm.GCMHelper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class RegisterGCM extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final MyTbaDatafeed datafeed;

    public RegisterGCM(Context context, MyTbaDatafeed datafeed) {
        this.context = context;
        this.datafeed = datafeed;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            GoogleCloudMessaging gcm = GCMHelper.getGcm(context);

            String senderId = GCMHelper.getSenderId(context);
            String regid = gcm.register(senderId);

            Log.d(Constants.LOG_TAG, "Device registered with GCM, ID: " + regid);

            boolean storeOnServer = datafeed.register(regid);
            if (storeOnServer) {
                Log.d(Constants.LOG_TAG, "Storing registration ID");
                // we had success on the server. Now store locally
                // Store the registration ID locally, so we don't have to do this again
                GCMAuthHelper.storeRegistrationId(context, regid);
            }
        } catch (IOException ex) {
            Log.e(Constants.LOG_TAG, "Error registering gcm:" + ex.getMessage());
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return null;
    }
}
