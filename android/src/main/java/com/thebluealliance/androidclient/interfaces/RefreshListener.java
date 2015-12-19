package com.thebluealliance.androidclient.interfaces;

public interface RefreshListener {

    /*
    Called by the hosting activity to indicate that a refresh has been requested
     */
    public void onRefreshStart(boolean actionIconPressed);

    /*
    Called by the hosting activity to indicate that the current refresh should be stopped for some reason.
    This method should rarely, if ever, be used.
     */
    public void onRefreshStop();

}
