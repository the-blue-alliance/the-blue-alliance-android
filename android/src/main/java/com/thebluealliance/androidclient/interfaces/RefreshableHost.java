package com.thebluealliance.androidclient.interfaces;

import java.io.Serializable;

/**
 * File created by phil on 7/20/14.
 */
public interface RefreshableHost {

    /*
     * Interface makes working with both RefreshableHost Actvities and Fragments easier.
     * However, since you can't declare synchronized methods in an interface, these should
     * end up being declared as synchronized on implementation.
     */

    public void registerRefreshableActivityListener(RefreshListener listener);
    public void deregisterRefreshableActivityListener(RefreshListener listener);
    public void notifyRefreshComplete(RefreshListener completedListener);
    public void startRefresh(RefreshListener listener);

}
