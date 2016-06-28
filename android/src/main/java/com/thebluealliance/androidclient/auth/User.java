package com.thebluealliance.androidclient.auth;

import android.net.Uri;

public class User {

    private final String mName;
    private final String mEmail;
    private final Uri mProfilePicUrl;

    public User(String name, String email, Uri profilePicUrl) {
        mName = name;
        mEmail = email;
        mProfilePicUrl = profilePicUrl;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public Uri getProfilePicUrl() {
        return mProfilePicUrl;
    }
}
