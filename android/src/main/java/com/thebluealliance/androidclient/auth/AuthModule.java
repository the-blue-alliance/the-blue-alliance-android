package com.thebluealliance.androidclient.auth;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;
import com.thebluealliance.androidclient.auth.google.GoogleAuthProvider;
import com.thebluealliance.androidclient.mytba.MyTbaOnboardingController;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module(includes = AccountModule.class)
public class AuthModule {

    @Provides @Singleton @Nullable
    public FirebaseAuth provideFirebaseAuth() {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (BuildConfig.DEBUG) {
                auth.useEmulator("10.0.2.2", 9099);
            }
            return auth;
        } catch (IllegalStateException ex) {
            /* When there is no google-secrets.json file found, the library throws an exception
             * here which causes insta-crashes for us. Silently recover here...
             */
            TbaLogger.e("Unable to find google-secrets.json, disabling login");
            return null;
        }
    }

    @Provides
    public GoogleAuthProvider provideGoogleAuthProvider(@ApplicationContext Context context, AccountController accountController) {
        return new GoogleAuthProvider(context, accountController);
    }

    @Provides @Named("firebase_auth")
    public AuthProvider provideFirebaseAuthProvider(@Nullable FirebaseAuth firebaseAuth,
                                                    GoogleAuthProvider googleAuthProvider) {
        return new FirebaseAuthProvider(firebaseAuth, googleAuthProvider);
    }

    @Provides
    public MyTbaOnboardingController provideMyTbaOnbordingController(@Named("firebase_auth") AuthProvider authProvider,
                                                                     AccountController accountController) {
        return new MyTbaOnboardingController(authProvider, accountController);
    }
}
