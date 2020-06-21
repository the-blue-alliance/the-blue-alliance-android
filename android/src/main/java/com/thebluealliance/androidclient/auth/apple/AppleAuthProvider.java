package com.thebluealliance.androidclient.auth.apple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.thebluealliance.androidclient.accounts.AccountController;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.crashlytics.android.core.CrashlyticsCore.TAG;

public class AppleAuthProvider {
    private static FirebaseAuth mAuth;
    private final Context mContext;
    private final AccountController mAccountController;
    public static OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com");

    @Inject
    public AppleAuthProvider(Context context, AccountController accountController) {
        mAuth.getCurrentUser();
        mAccountController = accountController;
        mContext = context;

    }

    public static FirebaseUser getCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        return user;
    }

    public static void reAuthenticateApple(Context context){
        // The user is already signed-in.
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        assert firebaseUser != null;
        firebaseUser
                .startActivityForReauthenticateWithProvider(/* activity= */ (Activity) context, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // User is re-authenticated with fresh tokens and
                                // should be able to perform sensitive operations
                                // like account deletion and email or password
                                // update.
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                            }
                        });
    }

    public static void getPendingAuthResult(){
        mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> pending = mAuth.getPendingAuthResult();
        if (pending != null) {
            pending.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d(TAG, "checkPending:onSuccess:" + authResult);
                    // Get the user profile with authResult.getUser() and
                    // authResult.getAdditionalUserInfo(), and the ID
                    // token from Apple with authResult.getCredential().

                    authResult.getUser();
                    authResult.getAdditionalUserInfo();
                    authResult.getCredential();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "checkPending:onFailure", e);
                }
            });
        } else {
            Log.d(TAG, "pending: null");
        }
    }

    public static void startSignUpWithApple(Context context){
        mAuth = FirebaseAuth.getInstance();
        mAuth.startActivityForSignInWithProvider((Activity) context, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // Sign-in successful!
                                Log.d(TAG, "activitySignIn:onSuccess:" + authResult.getUser());
                                FirebaseUser user = authResult.getUser();
                                AuthCredential credential = authResult.getCredential();
                                // ...
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "activitySignIn:onFailure", e);
                            }
                        });
    }


}
