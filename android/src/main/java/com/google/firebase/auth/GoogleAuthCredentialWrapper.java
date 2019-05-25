package com.google.firebase.auth;

import androidx.annotation.Nullable;

/**
 * A wrapper for {@link GoogleAuthCredential}, because the original class doesn't have a
 * public constructor
 */
public class GoogleAuthCredentialWrapper extends GoogleAuthCredential {
    public GoogleAuthCredentialWrapper(@Nullable String idToken, @Nullable String accessToken) {
        super(idToken, accessToken);
    }
}
