package com.thebluealliance.androidclient.auth.google;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.auth.AuthCredential;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.AuthProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class GoogleAuthProvider implements AuthProvider,
                                           GoogleApiClient.OnConnectionFailedListener,
                                           GoogleApiClient.ConnectionCallbacks
{

    private final Context mContext;
    private final AccountController mAccountController;
    private @Nullable GoogleApiClient mGoogleApiClient;
    private @Nullable GoogleSignInUser mCurrentUser;

    @Inject
    public GoogleAuthProvider(Context context, AccountController accountController) {
        mCurrentUser = null;
        mAccountController = accountController;
        mContext = context;

    }

    private void loadGoogleApiClient() {
        String clientId = mAccountController.getWebClientId();
        if (clientId.isEmpty()) {
            // No client id set in tba.properties, can't continue
            TbaLogger.w("Oauth client ID not set, can't enable myTBA. See https://goo.gl/Swp5PC "
                        + "for config details");
            mGoogleApiClient = null;
            return;
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(clientId)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public AuthCredential getAuthCredential(String idToken) {
        return com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null);
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null
            && !mGoogleApiClient.isConnecting()
            && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null
            && !mGoogleApiClient.isConnecting()
            && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean isUserSignedIn() {
        return mCurrentUser != null;
    }

    @Nullable @Override
    public GoogleSignInUser getCurrentUser() {
        return mCurrentUser;
    }

    @Override
    public Intent buildSignInIntent() {
        if (mGoogleApiClient == null) {
            loadGoogleApiClient();
        }
        return Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    }

    @Override
    public Observable<GoogleSignInUser> userFromSignInResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        boolean success = result.isSuccess();
        TbaLogger.d("Google Sign In Result: " + success);
        if (success) {
            mCurrentUser = new GoogleSignInUser(result.getSignInAccount());
        }
        return Observable.just(mCurrentUser);
    }

    @WorkerThread
    public Observable<GoogleSignInUser> signInLegacyUser() {
        onStart();
        OptionalPendingResult<GoogleSignInResult> optionalResult = Auth.GoogleSignInApi
                .silentSignIn(mGoogleApiClient);
        GoogleSignInResult result = optionalResult.await();
        onStop();
        if (result.isSuccess()) {
            return Observable.just(new GoogleSignInUser(result.getSignInAccount()));
        }
        return Observable.empty();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        TbaLogger.w("Google API client connection failed");
        TbaLogger.w(connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        TbaLogger.d("Google API client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
