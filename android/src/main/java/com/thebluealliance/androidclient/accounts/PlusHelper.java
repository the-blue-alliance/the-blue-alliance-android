package com.thebluealliance.androidclient.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import com.thebluealliance.androidclient.gcm.GCMHelper;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

/**
 * Created by phil on 11/16/14.
 */
public class PlusHelper {

    // This is the helper object that connects to Google Play Services.
    private static GoogleApiClient mApiClient = null;

    private static void buildPlusClient(Context context,
                                        GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {

        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }

    public static GoogleApiClient getApiClient(Context context,
                                               GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                               GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        if (mApiClient == null) {
            buildPlusClient(context, connectionCallbacks, connectionFailedListener);
        }
        return mApiClient;
    }

    public static void setApiClientClient(GoogleApiClient mApiClient) {
        PlusHelper.mApiClient = mApiClient;
    }

    public static boolean isConnected() {
        return mApiClient != null && mApiClient.isConnected();
    }

    public static boolean isConnecting() {
        return mApiClient != null && mApiClient.isConnecting();
    }

    public static void connect(Context context,
                               GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                               GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        if (mApiClient == null) {
            buildPlusClient(context, connectionCallbacks, connectionFailedListener);
        }
        if (!mApiClient.isConnected()) {
            mApiClient.connect();
        }
    }

    public static void onConnectCommon(Activity activity) {
        String accountName = PlusHelper.getAccountName();
        AccountHelper.setSelectedAccount(activity, accountName);
        AccountHelper.enableMyTBA(activity, true);
        AccountHelper.setSelectedAccount(activity, PlusHelper.getAccountName());
        GCMHelper.registerGCMIfNeeded(activity);
        activity.startService(new Intent(activity, MyTbaUpdateService.class));

        AccountHelper.registerSystemAccount(activity, accountName);
    }

    public static void disconnect() {
        if (mApiClient != null) {
            mApiClient.disconnect();
        }
    }

    public static void clearDefaultAccount() {
        if (mApiClient != null) {
            mApiClient.clearDefaultAccountAndReconnect();
        }
    }

    public static String getAccountName() {
        if (mApiClient != null) {
            return Plus.AccountApi.getAccountName(mApiClient);
        }
        return "";
    }

    public static Person getCurrentPerson() {
        if (mApiClient != null) {
            return Plus.PeopleApi.getCurrentPerson(mApiClient);
        }
        return null;
    }
}
