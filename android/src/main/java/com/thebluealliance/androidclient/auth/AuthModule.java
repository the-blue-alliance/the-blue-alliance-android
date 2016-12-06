package com.thebluealliance.androidclient.auth;

import com.google.firebase.auth.FirebaseAuth;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;
import com.thebluealliance.androidclient.auth.google.GoogleAuthProvider;

import android.content.Context;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = AccountModule.class)
public class AuthModule {

    private final Context mContext;

    public AuthModule(Context context) {
        mContext = context;
    }

    @Provides @Singleton @Nullable
    public FirebaseAuth provideFirebaseAuth() {
        try {
            return FirebaseAuth.getInstance();
        } catch (IllegalStateException ex) {
            /* When there is no google-secrets.json file found, the library throws an exception
             * here which causes insta-crashes for us. Silently recover here...
             */
            TbaLogger.e("Unable to find google-secrets.json, disabling login");
            return null;
        }
    }

    @Provides
    public GoogleAuthProvider provideGoogleAuthProvider(AccountController accountController) {
        return new GoogleAuthProvider(mContext, accountController);
    }

    @Provides @Named("firebase_auth")
    public AuthProvider provideFirebaseAuthProvider(@Nullable FirebaseAuth firebaseAuth,
                                                    GoogleAuthProvider googleAuthProvider) {
        return new FirebaseAuthProvider(firebaseAuth, googleAuthProvider);
    }
}
