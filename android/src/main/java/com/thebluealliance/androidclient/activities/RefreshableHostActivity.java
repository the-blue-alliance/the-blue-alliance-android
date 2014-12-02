package com.thebluealliance.androidclient.activities;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.eventbus.ConnectivityChangeEvent;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.interfaces.RefreshableHost;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nathan on 4/29/2014.
 */
public abstract class RefreshableHostActivity extends BaseActivity implements RefreshableHost {

    private ArrayList<RefreshListener> mRefreshListeners = new ArrayList<>();
    private ArrayList<RefreshListener> mCompletedRefreshListeners = new ArrayList<>();
    private boolean mRefreshed = false;

    Menu mOptionsMenu;

    private boolean mRefreshInProgress = false;

    private boolean mProgressBarShowing = false;

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
            setMenuProgressBarVisible(true);
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
                        startRefresh(true);
                    } else {
                        restartRefresh(true);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (!mRefreshed) {
            startRefresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelRefresh();
        EventBus.getDefault().unregister(this);
    }

    public synchronized void registerRefreshListener(RefreshListener listener) {
        if (listener != null && !mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
    }

    public synchronized void unregisterRefreshListener(RefreshListener listener) {
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
        setMenuProgressBarVisible(false);
        mRefreshInProgress = false;
        mRefreshed = true;

        //update myTBA after content loads
        if(AccountHelper.isAccountSelected(this)){
            new UpdateMyTBA(this, new RequestParams()).execute();
        }
    }

    /*
     * Notifies all registered listeners that they should start their refresh.
     * Passes parameter if toolbar icon initiated refresh (defaults to false)
     */
    public void startRefresh(){
        startRefresh(false);
    }

    public void startRefresh(boolean actionIconPressed) {
        if (mRefreshInProgress) {
            //if a refresh is already happening, don't start another
            return;
        }
        if (mRefreshListeners.isEmpty()) {
            return;
        }
        mRefreshInProgress = true;
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart(actionIconPressed);
        }
        setMenuProgressBarVisible(true);
    }

    /*
      Refreshes a specific listener
     */
    public void startRefresh(RefreshListener listener) {
        if (!mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
        listener.onRefreshStart(false);
        setMenuProgressBarVisible(true);
    }

    /*
    Notifies all registered listeners that they should cancel their refresh
     */
    public void cancelRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        mRefreshInProgress = false;
        setMenuProgressBarVisible(false);
    }

    /*
    Notifies all refresh listeners that they should stop, and immediately notifies them that they should start again.
     */
    public void restartRefresh(boolean actionIconPressed) {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart(actionIconPressed);
        }
        mRefreshInProgress = true;
        setMenuProgressBarVisible(true);
    }

    private void setMenuProgressBarVisible(boolean visible) {
        MenuItem refresh;
        if (mOptionsMenu != null) {
            refresh = mOptionsMenu.findItem(R.id.refresh);
        } else {
            return;
        }

        if (mProgressBarShowing && !visible) {
            // Hide progress indicator
            Log.d("RHA", "hidden");
            refresh.setActionView(null);
            mProgressBarShowing = false;
        } else if (!mProgressBarShowing && visible) {
            // Show progress indicator
            Log.d("RHA", "shown!");
            refresh.setActionView(R.layout.actionbar_indeterminate_progress);
            mProgressBarShowing = true;
        } else {
            Log.d("RHA", "did nothing! showing: " + mProgressBarShowing + "; desired: " + visible);
            // Do nothing
        }
    }

    public void onEvent(ConnectivityChangeEvent event) {
        if (event.getConnectivityChangeType() == ConnectivityChangeEvent.CONNECTION_FOUND) {
            hideWarningMessage();
            startRefresh();
        } else {
            showWarningMessage(getString(R.string.warning_no_internet_connection));
        }
    }
}
