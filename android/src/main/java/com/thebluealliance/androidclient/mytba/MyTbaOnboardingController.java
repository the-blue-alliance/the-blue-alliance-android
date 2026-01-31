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
import com.google.firebase.auth.FirebaseAuth;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.auth.firebase.FirebaseSignInUser;
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

    public void launchDevSignIn(AppCompatActivity activity, MyTbaOnboardingCallbacks callbacks) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = "user@thebluealliance.com";
        String password = "devdev";

        // Try sign-in first, then fall back to create+sign-in
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(activity, authResult -> {
                    onDevSignInSuccess(activity, callbacks, email, authResult);
                })
                .addOnFailureListener(activity, e -> {
                    TbaLogger.d("Dev sign-in attempt failed, creating user: " + e.getMessage());
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(activity, authResult -> {
                                onDevSignInSuccess(activity, callbacks, email, authResult);
                            })
                            .addOnFailureListener(activity, e2 -> {
                                TbaLogger.e("Dev sign-in failed", e2);
                                Toast.makeText(activity, "Dev sign-in failed: " + e2.getMessage()
                                        + "\nYou may need to delete the user from the Firebase Auth emulator UI and retry.",
                                        Toast.LENGTH_LONG).show();
                                callbacks.onLoginFailed();
                            });
                });
    }

    private void onDevSignInSuccess(AppCompatActivity activity, MyTbaOnboardingCallbacks callbacks,
                                     String email, com.google.firebase.auth.AuthResult authResult) {
        TbaLogger.d("Dev sign-in successful: " + email);
        FirebaseSignInUser user = new FirebaseSignInUser(authResult.getUser());
        mAccountController.onAccountConnect(activity, user);
        callbacks.onLoginSuccess();
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
