package com.thebluealliance.androidclient.auth;

import android.net.Uri;

public interface User {

    String getName();

    String getEmail();

    Uri getProfilePicUrl();
}
