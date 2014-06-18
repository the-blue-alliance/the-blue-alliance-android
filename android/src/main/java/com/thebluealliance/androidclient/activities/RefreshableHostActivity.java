package com.thebluealliance.androidclient.activities;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

import java.util.ArrayList;

/**
 * Created by Nathan on 4/29/2014.
 */
public abstract class RefreshableHostActivity extends BaseActivity {

    private ArrayList<RefreshListener> mRefreshListeners = new ArrayList<>();
    private ArrayList<RefreshListener> mCompletedRefreshListeners = new ArrayList<>();

    private Menu mOptionsMenu;

    private boolean mRefreshInProgress = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        mOptionsMenu = menu;
        System.out.println("menu created: "+mOptionsMenu);
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
        boolean refreshComplete = true;
        if (mRefreshListeners.size() == mCompletedRefreshListeners.size()) {
            for (RefreshListener listener : mRefreshListeners) {
                if (!mCompletedRefreshListeners.contains(listener)) {
                    refreshComplete = false;
                }
            }
        } else {
            refreshComplete = false;
        }
        if (refreshComplete) {
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
        if (mOptionsMenu != null) {
            // Hide refresh indicator
            MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
            refresh.setActionView(null);
        }
        // Only show "Refresh complete" if a refresh event was actually started
        if (mRefreshInProgress) {
            Toast.makeText(this, R.string.refresh_complete, Toast.LENGTH_SHORT).show();
        }
        mRefreshInProgress = false;
    }

    /*
    Notifies all registered listeners that they should start their refresh.
     */
    public void startRefresh() {
        if(mRefreshInProgress){
            //if a refresh is already happening, don't start another
            return;
        }
        mRefreshInProgress = true;
        if (mRefreshListeners.isEmpty()) {
            return;
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart();
        }
        System.out.println(mOptionsMenu+" OPTIONS");
        if (mOptionsMenu != null) {
            // Show refresh indicator
            MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
            refresh.setActionView(R.layout.actionbar_indeterminate_progress);
        }
    }

    /*
    Notifies all registered listeners that they should cancel their refresh
     */
    protected void cancelRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        if (mOptionsMenu != null) {
            // Hide refresh indicator
            MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
            refresh.setActionView(null);
        }
    }

    /*
    Notifies all refresh listeners that they should stop, and immediately notifies them that they should start again.
     */
    protected void restartRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart();
        }
        mRefreshInProgress = true;
    }
}
