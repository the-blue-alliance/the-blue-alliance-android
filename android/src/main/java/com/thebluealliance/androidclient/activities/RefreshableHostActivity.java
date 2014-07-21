package com.thebluealliance.androidclient.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.intents.ConnectionChangeBroadcast;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.interfaces.RefreshableHost;

import java.util.ArrayList;

/**
 * Created by Nathan on 4/29/2014.
 */
public abstract class RefreshableHostActivity extends BaseActivity implements RefreshableHost {

    private ArrayList<RefreshListener> mRefreshListeners = new ArrayList<>();
    private ArrayList<RefreshListener> mCompletedRefreshListeners = new ArrayList<>();
    private RefreshBroadcastReceiver refreshListener;

    Menu mOptionsMenu;

    private boolean mRefreshInProgress = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        mOptionsMenu = menu;
        if (mRefreshInProgress) {
            showMenuProgressBar();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (shouldRefresh()) {
                    // If a refresh is already in progress, restart it. Otherwise, begin a refresh.
                    if (!mRefreshInProgress) {
                        startRefresh();
                    } else {
                        restartRefresh();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListener = new RefreshBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(refreshListener, new IntentFilter(ConnectionChangeBroadcast.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelRefresh();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshListener);
        refreshListener = null;
    }

    public synchronized void registerRefreshableActivityListener(RefreshListener listener) {
        if (listener != null && !mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
    }

    public synchronized void deregisterRefreshableActivityListener(RefreshListener listener) {
        if (listener != null && mRefreshListeners.contains(listener)) {
            mRefreshListeners.remove(listener);
        }
        if (listener != null && mCompletedRefreshListeners.contains(listener)) {
            mCompletedRefreshListeners.remove(listener);
        }
    }

    /*
    This can be overridden by child classes to check whether or not a refresh should take place.
    For example, this might return false if there is no network connection and a network connection
    is required to refresh data. The default return value is true.

    @return true if a refresh can and should be triggered, false if otherwise
     */
    protected boolean shouldRefresh() {
        return true;
    }

    /*
    Registered listeners call this to notify the activity that they have finished refreshing.
    Once all registered listeners have indicated that they have finished refreshing, the list
    of listeners that have reported completion is cleared and onRefreshComplete() is called.

    @param listener the listener that has finished refreshing
     */
    public synchronized void notifyRefreshComplete(RefreshListener completedListener) {
        if (completedListener == null || !mRefreshListeners.contains(completedListener)) {
            return;
        }
        if (!mCompletedRefreshListeners.contains(completedListener)) {
            mCompletedRefreshListeners.add(completedListener);
        }

        if (mCompletedRefreshListeners.size() >= mRefreshListeners.size()) {
            onRefreshComplete();
            mCompletedRefreshListeners.clear();
        }
    }

    /*
    Called when all registered listeners have reported that they are done refreshing.
    This can be overridden to do custom things when refreshing is completed. However, the child class should ALWAYS
    call super.onRefreshComplete() to ensure proper behavior.
     */
    protected void onRefreshComplete() {
        hideMenuProgressBar();
        mRefreshInProgress = false;
    }

    /*
    Notifies all registered listeners that they should start their refresh.
     */
    public void startRefresh() {
        if (mRefreshInProgress) {
            //if a refresh is already happening, don't start another
            return;
        }
        Log.d(Constants.LOG_TAG, "Refresh listeners: " + mRefreshListeners.size());
        mRefreshInProgress = true;
        if (mRefreshListeners.isEmpty()) {
            return;
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart();
        }
        showMenuProgressBar();
    }

    /*
      Refreshes a specific listener
     */
    public void startRefresh(RefreshListener listener) {
        if (!mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
        listener.onRefreshStart();
    }

    /*
    Notifies all registered listeners that they should cancel their refresh
     */
    public void cancelRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        mRefreshInProgress = false;
        hideMenuProgressBar();
    }

    /*
    Notifies all refresh listeners that they should stop, and immediately notifies them that they should start again.
     */
    public void restartRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart();
        }
        mRefreshInProgress = true;
        showMenuProgressBar();
    }

    public void showMenuProgressBar() {
        if (mOptionsMenu != null) {
            // Show refresh indicator
            MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
            refresh.setActionView(R.layout.actionbar_indeterminate_progress);
        }
    }

    private void hideMenuProgressBar() {
        if (mOptionsMenu != null) {
            // Hide refresh indicator
            MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
            refresh.setActionView(null);
        }
    }

    class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.LOG_TAG, "RefreshableHost received refresh broadcast");
            if (intent.getIntExtra(ConnectionChangeBroadcast.CONNECTION_STATUS, ConnectionChangeBroadcast.CONNECTION_LOST) == ConnectionChangeBroadcast.CONNECTION_FOUND) {
                hideWarningMessage();
                startRefresh();
            } else {
                showWarningMessage(getString(R.string.warning_no_internet_connection));
            }
        }
    }
}
