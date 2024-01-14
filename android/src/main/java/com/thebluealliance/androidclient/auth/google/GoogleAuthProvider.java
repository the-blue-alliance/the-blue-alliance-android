package com.thebluealliance.androidclient.auth.google;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.AuthProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class GoogleAuthProvider implements AuthProvider {

    private final Context mContext;
    private final AccountController mAccountController;

    private GoogleSignInClient mSignInClient;

    private @Nullable GoogleSignInUser mCurrentUser;

    @Inject
    public GoogleAuthProvider(Context context, AccountController accountController) {
        mCurrentUser = null;
        mAccountController = accountController;
        mContext = context;
    }

    private void loadGoogleApiClient() {
        String clientId = mAccountController.getWebClientId();
        TbaLogger.d("Google client id: " + clientId);
        if (clientId.isEmpty()) {
            // No client id set in tba.properties, can't continue
            TbaLogger.w("Oauth client ID not set, can't enable myTBA. See https://goo.gl/Swp5PC "
                        + "for config details");
            mSignInClient = null;
            return;
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(clientId)
                .build();
        mSignInClient = GoogleSignIn.getClient(mContext, gso);
    }

    public AuthCredential getAuthCredential(String idToken) {
        return com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null);
    }

    @Override
    public boolean isUserSignedIn() {
        return mCurrentUser != null;
    }

    @Nullable @Override
    public GoogleSignInUser getCurrentUser() {
        return mCurrentUser;
    }

    @Nullable @Override
    public Intent buildSignInIntent() {
        if (mSignInClient == null) {
            // Lazy load the API client, if needed
            loadGoogleApiClient();
        }

        if (mSignInClient != null) {
            return mSignInClient.getSignInIntent();
        }

        // If we still can't get the API client, just give up
        return null;
    }

    @Override
    public Observable<GoogleSignInUser> userFromSignInResult(int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        boolean success = result != null && result.isSuccess();
        TbaLogger.d("Google Sign In Result: " + success);
        if (success) {
            mCurrentUser = new GoogleSignInUser(result.getSignInAccount());
        }
        return Observable.just(mCurrentUser);
    }

    @WorkerThread
    public Observable<GoogleSignInUser> signInLegacyUser() {
        TbaLogger.w("Legacy sign in migration no longer supported");
        return Observable.empty();
    }
}
