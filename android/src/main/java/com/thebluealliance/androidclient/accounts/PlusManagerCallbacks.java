package com.thebluealliance.androidclient.accounts;

/**
 * Created by Nathan on 8/13/2015.
 */
public interface PlusManagerCallbacks {

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
