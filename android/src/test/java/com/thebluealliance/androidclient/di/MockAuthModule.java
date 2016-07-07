package com.thebluealliance.androidclient.di;

import com.google.firebase.auth.FirebaseAuth;

import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.google.GoogleAuthProvider;

import org.mockito.Mockito;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockAuthModule {

    public MockAuthModule() {}

    @Provides @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    public GoogleAuthProvider provideGoogleAuthProvider() {
        return Mockito.mock(GoogleAuthProvider.class);
    }

    @Provides @Named("firebase_auth")
    public AuthProvider provideFirebaseAuthProvider() {
        return Mockito.mock(AuthProvider.class);
    }
}
