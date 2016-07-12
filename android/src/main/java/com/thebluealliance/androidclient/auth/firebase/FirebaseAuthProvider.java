package com.thebluealliance.androidclient.auth.firebase;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.firebase.client.annotations.Nullable;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.User;
import com.thebluealliance.androidclient.auth.google.GoogleAuthProvider;
import com.thebluealliance.androidclient.auth.google.GoogleSignInUser;

import android.content.Intent;

import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

@Singleton
public class FirebaseAuthProvider implements AuthProvider {

    private final FirebaseAuth mFirebaseAuth;
    private final GoogleAuthProvider mGoogleAuthProvider;

    public FirebaseAuthProvider(FirebaseAuth firebaseAuth, GoogleAuthProvider googleProvider) {
        mFirebaseAuth = firebaseAuth;
        mGoogleAuthProvider = googleProvider;
    }

    @Override
    public void onStart() {
        mGoogleAuthProvider.onStart();
    }

    @Override
    public void onStop() {
        mGoogleAuthProvider.onStop();
    }

    @Override
    public boolean isUserSignedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    @Override @Nullable
    public User getCurrentUser() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }

        return new FirebaseSignInUser(firebaseUser);
    }

    @Override
    public Intent buildSignInIntent() {
        return mGoogleAuthProvider.buildSignInIntent();
    }

    @Override
    public Observable<FirebaseSignInUser> userFromSignInResult(int requestCode, int resultCode, Intent data) {
        Observable<? extends User> googleUser = mGoogleAuthProvider.userFromSignInResult(requestCode, resultCode, data);
        return googleUser.switchMap(user -> {
            if (!(user instanceof GoogleSignInUser)) {
                return Observable.empty();
            }

            GoogleSignInUser googleSignInUser = (GoogleSignInUser) user;
            AuthCredential credential = mGoogleAuthProvider
                    .getAuthCredential(googleSignInUser.getIdToken());
            return Observable.create(new Observable.OnSubscribe<FirebaseSignInUser>() {
                @Override
                public void call(Subscriber<? super FirebaseSignInUser> subscriber) {
                    mFirebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AuthResult result = task.getResult();
                                subscriber.onNext(new FirebaseSignInUser(result.getUser()));
                            }
                            subscriber.onCompleted();
                        })
                        .addOnFailureListener(subscriber::onError);
                }
            });
        });
    }

    /**
     * Migrate a pre-Firebase user and try and sign it in with Firebase
     */
    public Observable<FirebaseSignInUser> signInLegacyUser() {
        Observable<? extends User> googleUser = mGoogleAuthProvider.signInLegacyUser();
        return googleUser.switchMap(user -> {
            if (!(user instanceof GoogleSignInUser)) {
                return Observable.empty();
            }

            GoogleSignInUser googleSignInUser = (GoogleSignInUser) user;
            AuthCredential credential = mGoogleAuthProvider
                    .getAuthCredential(googleSignInUser.getIdToken());
            return Observable.create(new Observable.OnSubscribe<FirebaseSignInUser>() {
                @Override
                public void call(Subscriber<? super FirebaseSignInUser> subscriber) {
                    mFirebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AuthResult result = task.getResult();
                                subscriber.onNext(new FirebaseSignInUser(result.getUser()));
                            }
                            subscriber.onCompleted();
                        })
                        .addOnFailureListener(subscriber::onError);
                }
            });
        });
    }
}
