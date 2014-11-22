package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by phil on 11/16/14.
 */
public class PlusHelper {

    // This is the helper object that connects to Google Play Services.
    private static PlusClient mPlusClient = null;

    private static void buildPlusClient(Context context,
                                        GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks,
                                        GooglePlayServicesClient.OnConnectionFailedListener connectionFailedListener){

        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mPlusClient = new PlusClient.Builder(context, connectionCallbacks, connectionFailedListener)
            .setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_ME)
            .build();
    }

    public static PlusClient getPlusClient(Context context,
                                           GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks,
                                           GooglePlayServicesClient.OnConnectionFailedListener connectionFailedListener) {
        if(mPlusClient == null){
            buildPlusClient(context, connectionCallbacks, connectionFailedListener);
        }
        return mPlusClient;
    }

    public static void setPlusClient(PlusClient mPlusClient) {
        PlusHelper.mPlusClient = mPlusClient;
    }

    public static boolean isConnected(){
        return mPlusClient != null && mPlusClient.isConnected();
    }

    public static boolean isConnecting(){
        return mPlusClient != null && mPlusClient.isConnecting();
    }

    public static void connect(Context context,
                               GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks,
                               GooglePlayServicesClient.OnConnectionFailedListener connectionFailedListener){
        if(mPlusClient == null){
            buildPlusClient(context, connectionCallbacks, connectionFailedListener);
        }
        mPlusClient.connect();
    }

    public static void disconnect(){
        if(mPlusClient != null){
            mPlusClient.disconnect();
        }
    }

    public static void clearDefaultAccount(){
        if(mPlusClient != null){
            mPlusClient.clearDefaultAccount();
        }
    }

    public static void revokeAccessAndDisconnect(PlusClient.OnAccessRevokedListener listener){
        if(mPlusClient != null){
            mPlusClient.revokeAccessAndDisconnect(listener);
        }
    }

    public static String getAccountName(){
        if(mPlusClient != null){
            return mPlusClient.getAccountName();
        }
        return "";
    }

    public static Person getCurrentPerson(){
        if(mPlusClient != null){
            return mPlusClient.getCurrentPerson();
        }
        return null;
    }
}
