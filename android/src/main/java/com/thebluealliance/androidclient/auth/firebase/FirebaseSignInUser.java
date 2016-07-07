package com.thebluealliance.androidclient.auth.firebase;

import com.google.firebase.auth.FirebaseUser;

import com.thebluealliance.androidclient.auth.User;

import android.net.Uri;

public class FirebaseSignInUser implements User {

    private final FirebaseUser mFirebaseUser;

    public FirebaseSignInUser(FirebaseUser firebaseUser) {
        mFirebaseUser = firebaseUser;
    }

    @Override
    public String getName() {
        return mFirebaseUser.getDisplayName();
    }

    @Override
    public String getEmail() {
        return mFirebaseUser.getEmail();
    }

    @Override
    public Uri getProfilePicUrl() {
        return mFirebaseUser.getPhotoUrl();
    }
}
