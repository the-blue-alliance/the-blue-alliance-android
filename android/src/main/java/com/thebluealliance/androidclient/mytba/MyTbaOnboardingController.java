package com.thebluealliance.androidclient.mytba;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.Status;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.User;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

public class MyTbaOnboardingController {

    final AuthProvider mAuthProvider;
    final AccountController mAccountController;

    ActivityResultLauncher<Intent> mSignInLauncher;
    ActivityResultLauncher<String> mNotificationPermissionLauncher;

    @Inject
    public MyTbaOnboardingController(
            @Named("firebase_auth") AuthProvider authProvider,
            AccountController accountController
    ) {
        mAuthProvider = authProvider;
        mAccountController = accountController;
    }

    public void registerActivityCallbacks(AppCompatActivity activity, MyTbaOnboardingCallbacks callbacks) {
        mSignInLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            onSignInResult(activity, callbacks, result.getResultCode(), result.getData());
        });
        mNotificationPermissionLauncher =
                activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), callbacks::onPermissionResult);
    }

    private void onSignInResult(AppCompatActivity activity, MyTbaOnboardingCallbacks callbacks, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Observable<? extends User> observable = mAuthProvider.userFromSignInResult(resultCode, data);
            observable.subscribe(user -> {
                TbaLogger.d("User logged in: " + user.getEmail());
                mAccountController.onAccountConnect(activity, user);
                callbacks.onLoginSuccess();
            }, throwable -> {
                TbaLogger.e("Error logging in", throwable);
                mAccountController.setMyTbaEnabled(false);
                callbacks.onLoginFailed();
            });
        } else if (resultCode == RESULT_CANCELED) {
            Status signInStatus = (Status)data.getExtras().get("googleSignInStatus");
            String errorReason = GoogleSignInStatusCodes.getStatusCodeString(signInStatus.getStatusCode());
            Toast.makeText(activity, "Google sign in error: " + errorReason, Toast.LENGTH_LONG).show();
            TbaLogger.w("Google sign in error: " + errorReason);
            callbacks.onLoginFailed();
        }
    }

    public void launchSignIn(AppCompatActivity activity) {
        Intent signInIntent = mAuthProvider.buildSignInIntent();
        if (signInIntent == null) {
            Toast.makeText(activity, R.string.mytba_no_signin_intent, Toast.LENGTH_SHORT).show();
            TbaLogger.e("Unable to get login Intent");
            return;
        }

        mSignInLauncher.launch(signInIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void launchNotificationPermissionRequest(AppCompatActivity activity) {
        TbaLogger.i("Requesting notification permission");
        mNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    public interface MyTbaOnboardingCallbacks {
        void onLoginSuccess();
        void onLoginFailed();
        void onPermissionResult(boolean isGranted);
    }
}
