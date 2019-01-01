package com.thebluealliance.androidclient.datafeed.gce;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Class that handles getting oauth tokens to interact with Cloud Endpoints
 * Mainly uses the {@link GoogleAuthUtil} helper class
 * Resulting oauth tokens are to be used with the GCE Retrofit Services
 */
public class GceAuthController {
    private static final int MAX_BACKOFF_TRIES = 3;
    private static final String AUTH_HEADER_FORMAT = "Bearer %1$s";

    private final Context mContext;
    private final AccountController mAccountController;

    /* Time to wait during exponential backoff (ms) */
    private long mBackoffTime;

    /* Number of tries we've made while getting token */
    private int mBackoffCount;

    @Inject
    public GceAuthController(
            Context context,
            AccountController accountController) {
        mContext = context;
        mAccountController = accountController;
        resetBackoff();
    }

    private void resetBackoff() {
        mBackoffTime = 1000;
        mBackoffCount = 0;
    }

    /**
     * Negotiates a Google oauth2 token to use with Cloud Endpoints + Retrofit
     * This <b>MUST</b> be run from a background thread, performs much network operations and sleep
     * Based on https://developers.google.com/api-client-library/java/google-api-java-client/reference/1.19.1/com/google/api/client/googleapis/extensions/android/gms/auth/GoogleAccountCredential
     * Specifically #getToken()
     * @return An auth token to be used with Cloud Endpoints
     * @throws GoogleAuthException Trouble authenticating to Google
     */
    @WorkerThread @VisibleForTesting
    @Nullable private String getAuthTokenWithBackoff() throws GoogleAuthException {
        String scope = getAudience();
        String account = mAccountController.getSelectedAccount();
        if (account == null || account.isEmpty()) {
            TbaLogger.e("No system account found, can't get auth token");
            return null;
        }
        resetBackoff();
        while (mBackoffCount < MAX_BACKOFF_TRIES) {
            try {
                return getGoogleAuthToken(account, scope);
            } catch (IOException e) {
                TbaLogger.i("Unable to get token, sleeping " + mBackoffTime + " ms");
                e.printStackTrace();
                SystemClock.sleep(mBackoffTime);
                mBackoffTime *= 2;
                mBackoffCount++;
            }
        }
        return null;
    }

    /**
     * Builds the correct Authorization header to be used with GCE requests
     * This method <b>MUST</b> be called from a background thread; it uses {@link #getAuthTokenWithBackoff()}
     * @return Authorization header, or null if {@link GoogleAuthException} or other error happened
     */
    @WorkerThread
    public @Nullable String getAuthHeader() {
        try {
            String token = getAuthTokenWithBackoff();
            if (token == null) {
                return null;
            }
            return String.format(AUTH_HEADER_FORMAT, token);
        } catch (GoogleAuthException e) {
            TbaLogger.w("Auth exception while fetching google token");
            return null;
        }

    }

    @WorkerThread @VisibleForTesting
    String getGoogleAuthToken(String account, String scope)
    throws IOException, GoogleAuthException {
        if (account == null || account.isEmpty()) return null;
        return GoogleAuthUtil.getToken(mContext, account, scope);
    }

    private String getAudience() {
        String webClientId = mAccountController.getWebClientId();
        return "audience:server:client_id:" + webClientId;
    }

}
