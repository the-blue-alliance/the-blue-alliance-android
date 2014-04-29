package com.thebluealliance.androidclient.activities;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.interfaces.RefreshableActivityListener;

import java.util.ArrayList;

/**
 * Created by Nathan on 4/29/2014.
 */
public abstract class RefreshableActivity extends FragmentActivity {

    private ArrayList<RefreshableActivityListener> refreshListeners = new ArrayList<>();
    private ArrayList<RefreshableActivityListener> completedRefreshListeners = new ArrayList<>();

    private Menu mOptionsMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        mOptionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                startRefresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void registerRefreshableActivityListener(RefreshableActivityListener listener) {
        if (listener != null && !refreshListeners.contains(listener)) {
            refreshListeners.add(listener);
        }
    }

    public void deregisterRefreshableActivityListener(RefreshableActivityListener listener) {
        if (listener != null && refreshListeners.contains(listener)) {
            refreshListeners.remove(listener);
        }
    }

    public void refreshComplete(RefreshableActivityListener listener) {
        if (listener == null || !refreshListeners.contains(listener)) {
            return;
        }
        if (!completedRefreshListeners.contains(listener)) {
            completedRefreshListeners.add(listener);
        }
        boolean refreshComplete = true;
        if (refreshListeners.size() == completedRefreshListeners.size()) {
            for (RefreshableActivityListener rlistener : refreshListeners) {
                if (!completedRefreshListeners.contains(rlistener)) {
                    refreshComplete = false;
                }
            }
        } else {
            refreshComplete = false;
        }
        if (refreshComplete) {
            if (mOptionsMenu != null) {
                // Hide refresh indicator
                MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
                refresh.setActionView(null);
            }
            completedRefreshListeners.clear();
        }
    }

    protected void startRefresh() {
        if (refreshListeners.isEmpty()) {
            return;
        }
        for (RefreshableActivityListener listener : refreshListeners) {
            listener.onRefreshStart();
        }
        if (mOptionsMenu != null) {
            // Show refresh indicator
            MenuItem refresh = mOptionsMenu.findItem(R.id.refresh);
            refresh.setActionView(R.layout.actionbar_indeterminate_progress);
        }
    }

    protected void stopRefresh() {
        for (RefreshableActivityListener listener : refreshListeners) {
            listener.onRefreshStop();
        }
    }
}
