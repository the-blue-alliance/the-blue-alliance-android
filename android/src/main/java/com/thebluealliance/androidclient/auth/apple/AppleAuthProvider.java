package com.thebluealliance.androidclient.auth.apple;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class AppleAuthProvider {
    private static FirebaseAuth mAuth;
    public static final OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com");

    // Request Scopes
    private static final List<String> scopes =
            new ArrayList<String>() {
                {
                    add("email");
                    add("name");
                }
            };



    @Inject
    public AppleAuthProvider(Context context, AccountController accountController) {
        mAuth.getCurrentUser();

    }

    public static FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }
    // ReAuthenticate a User
    public static void reAuthenticateApple(Context context){
        // The user is already signed-in.
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        provider.setScopes(scopes);

        assert firebaseUser != null;
        firebaseUser
                .startActivityForReauthenticateWithProvider((Activity) context, provider.build())
                .addOnSuccessListener(
                        authResult -> {
                            // User is re-authenticated with fresh tokens and
                            // should be able to perform sensitive operations
                            // like account deletion and email or password
                            // update.
                        })
                .addOnFailureListener(
                        e -> {
                            // Handle failure.
                        });
    }

    // Used when there is a pending Intent from the Apple Provider
    public static void startPendingAuthResult(Context context){
        mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> pending = mAuth.getPendingAuthResult();
        if (pending != null) {
            pending.addOnSuccessListener(authResult -> {
                TbaLogger.d("Pending Sign In with Apple Result: " + authResult);
                // Get the user profile with authResult.getUser() and
                // authResult.getAdditionalUserInfo(), and the ID
                // token from Apple with authResult.getCredential().

                authResult.getUser();
                authResult.getAdditionalUserInfo();
                authResult.getCredential();
            }).addOnFailureListener(e -> TbaLogger.w("Pending Sign In with Apple Result: ", e));
        } else {
            startSignUpWithApple(context);
        }
    }

    public static void startSignUpWithApple(Context context){
        mAuth = FirebaseAuth.getInstance();
        provider.setScopes(scopes);
        mAuth.startActivityForSignInWithProvider((Activity) context, provider.build())
                .addOnSuccessListener(
                        authResult -> {
                            // Sign-in successful!
                            TbaLogger.d("Start Sign Up with Apple: " + authResult.getUser());
                        })
                .addOnFailureListener(e -> TbaLogger.w("Start Sign Up with Apple Failure: ", e));
    }

}
