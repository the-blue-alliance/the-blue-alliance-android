package com.thebluealliance.androidclient.auth;

import android.content.Intent;
import android.support.annotation.Nullable;

public interface AuthProvider {

    /**
     * Check if a user is currently signed in
     */
    boolean isUserSignedIn();

    /**
     * Get the current user account
     * @return The current user. If there is not a user signed in, return {@code null}
     */
    @Nullable  User getCurrentUser();

    /**
     * Build an intent that can be passed to
     * {@link android.app.Activity#startActivityForResult(Intent, int)}.
     * This activity handles signing the user in and
     * either returns RESULT_OK or RESULT_CANCELLED
     */
    Intent buildSignInIntent();

}
