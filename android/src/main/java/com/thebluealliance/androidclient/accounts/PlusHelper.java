package com.thebluealliance.androidclient.accounts;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by phil on 11/16/14.
 */
public class PlusHelper {

    // This is the helper object that connects to Google Play Services.
    private static GoogleApiClient mApiClient = null;

    private static void buildPlusClient(Context context,
                                        GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener connectionFailedListener){

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
        if(mApiClient == null){
            buildPlusClient(context, connectionCallbacks, connectionFailedListener);
        }
        return mApiClient;
    }

    public static void setApiClientClient(GoogleApiClient mApiClient) {
        PlusHelper.mApiClient = mApiClient;
    }

    public static boolean isConnected(){
        return mApiClient != null && mApiClient.isConnected();
    }

    public static boolean isConnecting(){
        return mApiClient != null && mApiClient.isConnecting();
    }

    public static void connect(Context context,
                               GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                               GoogleApiClient.OnConnectionFailedListener connectionFailedListener){
        if(mApiClient == null){
            buildPlusClient(context, connectionCallbacks, connectionFailedListener);
        }
        mApiClient.connect();
    }

    public static void disconnect(){
        if(mApiClient != null){
            mApiClient.disconnect();
        }
    }

    public static void clearDefaultAccount(){
        if(mApiClient != null){
            mApiClient.clearDefaultAccountAndReconnect();
        }
    }

    public static void revokeAccessAndDisconnect(PlusClient.OnAccessRevokedListener listener){
        disconnect();
    }

    public static String getAccountName(){
        if(mApiClient != null){
            return Plus.AccountApi.getAccountName(mApiClient);
        }
        return "";
    }

    public static Person getCurrentPerson(){
        if(mApiClient != null){
            return Plus.PeopleApi.getCurrentPerson(mApiClient);
        }
        return null;
    }
}
