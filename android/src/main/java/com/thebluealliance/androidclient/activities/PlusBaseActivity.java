package com.thebluealliance.androidclient.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.thebluealliance.androidclient.accounts.PlusHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public abstract class PlusBaseActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = PlusBaseActivity.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int OUR_REQUEST_CODE = 49404;

    // A flag to stop multiple dialogues appearing for the user
    private boolean mAutoResolveOnFail;

    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;


    /**
     * Called when the PlusClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();


    protected abstract void onPlusClientBlockingUI(boolean show);

    /**
     * Called when there is a change in connection state.  If you have "Sign in"/ "Connect", "Sign
     * out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states need to
     * be updated.
     */
    protected abstract void updateConnectButtonState();

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        // Show the dialog as we are now signing in.
        setProgressBarVisible(true);
        // Make sure that we will start the resolution (e.g. fire the intent and pop up a
        // dialog for the user) for any errors that come in.
        mAutoResolveOnFail = true;
        // We should always have a connection result ready to resolve,
        // so we can start that process.
        if (mConnectionResult != null) {
            startResolution();
        } else {
            // If we don't have one though, we can start connect in
            // order to retrieve one.
            initiatePlusClientConnect();
        }

        updateConnectButtonState();
    }


    private void initiatePlusClientConnect() {
        if (!PlusHelper.isConnected() && !PlusHelper.isConnecting()) {
            PlusHelper.connect(this, this, this);
        }
    }


    private void initiatePlusClientDisconnect() {
        if (PlusHelper.isConnected()) {
            PlusHelper.disconnect();
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOut() {

        // We only want to sign out if we're connected.
        if (PlusHelper.isConnected()) {
            // Clear the default account in order to allow the user to potentially choose a
            // different account from the account chooser.
            PlusHelper.clearDefaultAccount();

            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            initiatePlusClientDisconnect();

            Log.v(TAG, "Sign out successful!");
        }

        updateConnectButtonState();
    }

    /**
     * Revoke Google+ authorization completely.
     */
    public void revokeAccess() {

    }

    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution of the
     * ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt by
     * PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        updateConnectButtonState();
        if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.

            initiatePlusClientConnect();
        } else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            setProgressBarVisible(false);
        }
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        updateConnectButtonState();
        setProgressBarVisible(false);

        PlusHelper.onConnectCommon(this, null);

        onPlusClientSignIn();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Connection failed for some reason (called by PlusClient) Try and resolve the result.  Failure
     * here is usually not an indication of a serious error, just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        updateConnectButtonState();

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                startResolution();
            }
        }
    }
}
