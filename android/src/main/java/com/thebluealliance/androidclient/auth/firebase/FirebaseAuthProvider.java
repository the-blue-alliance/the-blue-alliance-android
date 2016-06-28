package com.thebluealliance.androidclient.auth.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.firebase.client.annotations.Nullable;
import com.firebase.ui.auth.AuthUI;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.User;

import android.content.Intent;

import javax.inject.Singleton;

@Singleton
public class FirebaseAuthProvider implements AuthProvider {

    private final FirebaseAuth mFirebaseAuth;
    private final AuthUI mAuthUI;

    public FirebaseAuthProvider(FirebaseAuth firebaseAuth, AuthUI authUI) {
        mFirebaseAuth = firebaseAuth;
        mAuthUI = authUI;
    }

    @Override
    public boolean isUserSignedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    @Override @Nullable
    public User getCurrentUser() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }

        return new User(firebaseUser.getDisplayName(),
                        firebaseUser.getEmail(),
                        firebaseUser.getPhotoUrl());
    }

    @Override
    public Intent buildSignInIntent() {
        return mAuthUI.createSignInIntentBuilder()
                .setProviders(AuthUI.GOOGLE_PROVIDER)
                .setLogo(R.drawable.ic_launcher)
                .build();
    }

}
