package com.thebluealliance.androidclient.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by phil on 11/16/14.
 */
public class AccountAuthenticatorService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
