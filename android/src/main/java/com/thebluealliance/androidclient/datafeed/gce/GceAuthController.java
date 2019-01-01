package com.thebluealliance.androidclient.datafeed.gce;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Class that handles getting oauth tokens to interact with Cloud Endpoints
 * Mainly uses the {@link GoogleAuthUtil} helper class
 * Resulting oauth tokens are to be used with the GCE Retrofit Services
 */
public class GceAuthController {
    private static final String AUTH_HEADER_FORMAT = "Bearer %1$s";

    private final FirebaseAuthProvider mFirebaseAuth;

    @Inject
    public GceAuthController(FirebaseAuthProvider firebaseAuth) {
        mFirebaseAuth = firebaseAuth;
    }

    /**
     * Builds the correct Authorization header to be used with GCE requests
     * This method <b>MUST</b> be called from a background thread; it will block on a Future
     * @return Authorization header, or null if {@link GoogleAuthException} or other error happened
     */
    @WorkerThread
    public @Nullable String getAuthHeader() {
        try {
            String token = getGoogleAuthToken();
            if (token == null) {
                return null;
            }
            return String.format(AUTH_HEADER_FORMAT, token);
        } catch (InterruptedException | ExecutionException e) {
            TbaLogger.w("Auth exception while fetching google token", e);
            return null;
        }
    }

    @WorkerThread @VisibleForTesting @Nullable
    String getGoogleAuthToken() throws ExecutionException, InterruptedException {
        Task<GetTokenResult> tokenTask = mFirebaseAuth.getUserToken(false);
        if (tokenTask == null) {
            TbaLogger.i("No firebase user found, can't fetch auth token");
            return null;
        }
        GetTokenResult tokenResult = Tasks.await(tokenTask);
        return tokenResult.getToken();
    }

}
