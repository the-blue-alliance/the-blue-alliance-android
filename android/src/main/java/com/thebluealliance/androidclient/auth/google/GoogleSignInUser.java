package com.thebluealliance.androidclient.auth.google;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.thebluealliance.androidclient.auth.User;

public final class GoogleSignInUser implements User {

    private final GoogleSignInAccount mAccount;

    GoogleSignInUser(GoogleSignInAccount account) {
        mAccount = account;
    }

    @Override
    public String getName() {
        return mAccount.getDisplayName();
    }

    @Override
    public String getEmail() {
        return mAccount.getEmail();
    }

    @Override
    public Uri getProfilePicUrl() {
        return mAccount.getPhotoUrl();
    }

    public String getIdToken() {
        return mAccount.getIdToken();
    }
}
