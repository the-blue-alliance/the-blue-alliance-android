package com.thebluealliance.androidclient.interfaces;

/**
 * File created by phil on 7/20/14.
 */
public interface RefreshableHost {

    /*
     * Interface makes working with both RefreshableHost Actvities and Fragments easier.
     * However, since you can't declare synchronized methods in an interface, these should
     * end up being declared as synchronized on implementation.
     */

    void registerRefreshListener(RefreshListener listener);

    void unregisterRefreshListener(RefreshListener listener);

    void notifyRefreshComplete(RefreshListener completedListener);

    void startRefresh(RefreshListener listener);

}
