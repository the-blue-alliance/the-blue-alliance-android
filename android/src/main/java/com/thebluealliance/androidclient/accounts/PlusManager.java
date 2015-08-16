package com.thebluealliance.androidclient.accounts;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class PlusManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    private Callbacks mCallbacks;
    private Activity mActivity;

    public PlusManager(Activity activity, Callbacks callbacks) {
        mCallbacks = callbacks;
        mActivity = activity;
    }

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

        if (mCallbacks != null) {
            mCallbacks.updateConnectButtonState();
        }
    }


    private void initiatePlusClientConnect() {
        if (!PlusHelper.isConnected() && !PlusHelper.isConnecting()) {
            PlusHelper.connect(mActivity, this, this);
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
        }

        if (mCallbacks != null) {
            mCallbacks.updateConnectButtonState();
        }
    }

    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        if (mCallbacks != null) {
            mCallbacks.onPlusClientBlockingUI(flag);
        }
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
            mConnectionResult.startResolutionForResult(mActivity, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * Users should proxy calls to onActivityResult through to this class
     *
     * An earlier connection failed, and we're now receiving the result of the resolution attempt by
     * PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    public boolean onActivityResult(int requestCode, int responseCode) {
        if (mCallbacks != null) {
            mCallbacks.updateConnectButtonState();
        }
        if (requestCode == OUR_REQUEST_CODE && responseCode == Activity.RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;

            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            initiatePlusClientConnect();
            return true;
        } else if (requestCode == OUR_REQUEST_CODE && responseCode != Activity.RESULT_OK) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            setProgressBarVisible(false);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        setProgressBarVisible(false);

        PlusHelper.onConnectCommon(mActivity);

        if (mCallbacks != null) {
            mCallbacks.onPlusClientSignIn();
            mCallbacks.updateConnectButtonState();
        }
    }

    /**
     * Connection failed for some reason (called by PlusClient) Try and resolve the result.  Failure
     * here is usually not an indication of a serious error, just that the user's input is needed.
     *
     * @see #onActivityResult(int, int)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mCallbacks != null) {
            mCallbacks.updateConnectButtonState();
        }

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    public interface Callbacks {

        /**
         * Called when the PlusClient is successfully connected.
         */
        void onPlusClientSignIn();


        void onPlusClientBlockingUI(boolean show);

        /**
         * Called when there is a change in connection state.  If you have "Sign in"/ "Connect", "Sign
         * out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states need to
         * be updated.
         */
        void updateConnectButtonState();
    }
}
