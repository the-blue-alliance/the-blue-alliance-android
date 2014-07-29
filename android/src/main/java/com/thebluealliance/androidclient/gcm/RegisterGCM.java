package com.thebluealliance.androidclient.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.Constants;

import java.io.IOException;

/**
 * File created by phil on 7/27/14.
 */
public class RegisterGCM extends AsyncTask<Void, Void, Void> {

    private Context context;

    public RegisterGCM(Context c) {
        context = c;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            GoogleCloudMessaging gcm = GCMHelper.getGcm(context);

            String senderId = GCMHelper.getSenderId(context);
            String regid = gcm.register(senderId);

            Log.d(Constants.LOG_TAG, "Device registered with GCM, ID: "+regid);

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            GCMHelper.sendRegistrationIdToBackend(context, regid);

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            GCMHelper.storeRegistrationId(context, regid);
        } catch (IOException ex) {
            Log.e(Constants.LOG_TAG, "Error registering gcm:" + ex.getMessage());
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return null;
    }
}
