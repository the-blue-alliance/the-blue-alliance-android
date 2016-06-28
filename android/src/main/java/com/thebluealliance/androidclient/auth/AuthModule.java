package com.thebluealliance.androidclient.auth;

import com.google.firebase.auth.FirebaseAuth;

import com.firebase.ui.auth.AuthUI;
import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {

    public AuthModule() {}

    @Provides @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides @Singleton
    public AuthUI provideFirebaseAuthUI() {
        return AuthUI.getInstance();
    }

    @Provides @Singleton @Named("firebase_auth")
    public AuthProvider provideFirebaseAuthProvider(FirebaseAuth firebaseAuth, AuthUI authUI) {
        return new FirebaseAuthProvider(firebaseAuth, authUI);
    }
}