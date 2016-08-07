package com.thebluealliance.androidclient.auth.firebase;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.User;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;

import android.app.IntentService;
import android.content.Intent;
import com.thebluealliance.androidclient.TbaLogger;

import javax.inject.Inject;
import javax.inject.Named;

public class MigrateLegacyUserToFirebase extends IntentService {

    @Inject @Named("firebase_auth") AuthProvider mAuthProvider;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MigrateLegacyUserToFirebase() {
        super("migrate-legacy-user");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TbaLogger.d("Trying to migrate legacy auth to Firebase");
        User user = mAuthProvider.signInLegacyUser().toBlocking().first();

        if (user != null) {
            TbaLogger.d("Migrated user");
        } else {
            TbaLogger.d("Failed to migrate");
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
