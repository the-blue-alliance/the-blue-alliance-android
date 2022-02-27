package com.thebluealliance.androidclient.auth.firebase;

import android.app.IntentService;
import android.content.Intent;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.User;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MigrateLegacyUserToFirebase extends IntentService {

    @Inject @Named("firebase_auth") AuthProvider mAuthProvider;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MigrateLegacyUserToFirebase() {
        super("migrate-legacy-user");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TbaLogger.i("Trying to migrate legacy auth to Firebase");
        User user = mAuthProvider.signInLegacyUser().toBlocking().firstOrDefault(null);

        if (user != null) {
            TbaLogger.i("Migrated user");
        } else {
            TbaLogger.i("Failed to migrate");
        }
    }
}
