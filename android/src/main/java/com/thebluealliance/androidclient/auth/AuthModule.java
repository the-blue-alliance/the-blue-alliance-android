package com.thebluealliance.androidclient.auth;

import com.google.firebase.auth.FirebaseAuth;

import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;
import com.thebluealliance.androidclient.auth.google.GoogleAuthProvider;

import android.content.Context;

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

    @Provides @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    public GoogleAuthProvider provideGoogleAuthProvider(AccountController accountController) {
        return new GoogleAuthProvider(mContext, accountController);
    }

    @Provides @Named("firebase_auth")
    public AuthProvider provideFirebaseAuthProvider(FirebaseAuth firebaseAuth,
                                                            GoogleAuthProvider googleAuthProvider) {
        return new FirebaseAuthProvider(firebaseAuth, googleAuthProvider);
    }
}
