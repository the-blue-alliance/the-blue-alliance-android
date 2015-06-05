package com.thebluealliance.androidclient.datafeed;

import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.interfaces.Refreshable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides an easy way to manage a collection of {@link Refreshable} objects. {@link Refreshable}
 * objects should register themselves with this class; they will receive callbacks when a refresh is
 * requested. Additionally, they can push their current state (refreshing or not) into this class.
 * This allows us to determine if any registered objects are currently refreshing and take the
 * appropriate action.
 * <p>
 * The primary way this class will be used is to work with a {@link MenuItem} that controls
 * refreshing. When you bind a {@link RefreshManager} to a {@link MenuItem} with the {@link
 * RefreshManager#bindToMenuItem(MenuItem)} method, this class will monitor the state of its
 * registered {@link Refreshable}s and replace the item's action view with a progress indicator and
 * revert the icon to its normal state when the refresh is finished. And, as long as you proxy
 * {@link android.app.Activity#onOptionsItemSelected(MenuItem)} calls through to this class, it will
 * automatically start a refresh when the bound {@link MenuItem} is clicked.
 * <p>
 * Created by Nathan on 6/4/2015.
 */
public class RefreshManager {

    /**
     * Maps {@link Refreshable} objects to their current refreshing state
     */
    private Map<Refreshable, Boolean> mRefreshableStates;

    /**
     * Optional listener that will receive a callback when the refreshing state changes
     */
    private RefreshStateListener mListener;

    /**
     * Optional {@link MenuItem} that is bound to
     */
    private MenuItem mMenuItem;

    /**
     * True if any registered {@link Refreshable}s are refreshing, false if none are
     */
    private boolean mIsRefreshing = false;

    public RefreshManager() {
        mRefreshableStates = new HashMap<>();
    }

    /**
     * Attaches a listener that will receive a callback when the refresh state changes
     *
     * @param listener listener to receive callbacks
     */
    public void setRefreshStateListener(RefreshStateListener listener) {
        mListener = listener;
    }

    /**
     * Binding to a {@link }MenuItem} will automatically replace its action view with a loading
     * indicator when a refresh is in progress.
     *
     * @param menuItem the {@link MenuItem} to bind to
     */
    public void bindToMenuItem(MenuItem menuItem) {
        mMenuItem = menuItem;
        updateMenuItemState();
    }

    /**
     * Calls to {@link android.app.Activity#onOptionsItemSelected(MenuItem)} should be proxied
     * through to this method if you want to take advantage of automatically starting a refresh when
     * the bound {@link MenuItem} is clicked.
     *
     * @param menuItem the {@link MenuItem} that was clicked.
     * @return true if the click was handled, false otherwise
     */
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == mMenuItem.getItemId()) {
            // Refresh button clicked, start refresh
            startRefresh(true);
            // Click handled
            return true;
        }
        return false;
    }

    /**
     * @param refreshable
     */
    public void registerRefreshable(Refreshable refreshable) {
        // Default to "not refreshing"
        mRefreshableStates.put(refreshable, false);
    }

    /**
     * Simply calls {@link Refreshable#onRefreshStart(boolean)} on all registered {@link
     * Refreshable}s.
     *
     * @param requestedByUser true if this refresh was explicitly requested by the user; for
     *                        instance, if they clicked a refresh button
     */
    public void startRefresh(boolean requestedByUser) {
        for (Refreshable refreshable : mRefreshableStates.keySet()) {
            refreshable.onRefreshStart(requestedByUser);
        }
    }

    /**
     * Called to notify this class that a {@link Refreshable}'s refreshing state has changed. Note
     * that this will also register the {@link Refreshable} for future refresh start callbacks if it
     * was not already registered.
     *
     * @param refreshable  the {@link Refreshable} object whose state has changed
     * @param isRefreshing true if the {@link Refreshable} is currently refreshing, false if it is
     *                     not
     */
    public void notifyRefreshingStateChanged(Refreshable refreshable, boolean isRefreshing) {
        mRefreshableStates.put(refreshable, isRefreshing);
        boolean oldRefreshingState = mIsRefreshing;

        updateRefreshingState();
        updateMenuItemState();

        if (mIsRefreshing != oldRefreshingState) {
            // The state changed. Notify the listener, if one exists
            if (mListener != null) {
                mListener.onRefreshStateChanged(mIsRefreshing);
            }
        }
    }

    /**
     * Checks to see if any {@link Refreshable}s are refreshing, and updates an internal flag with
     * the result.
     *
     * @return returns the value of {@code mIsRefreshing} for convenience
     */
    private boolean updateRefreshingState() {
        Collection<Boolean> refreshingStates = mRefreshableStates.values();
        if (refreshingStates.contains(true)) {
            // Something is still refreshing
            mIsRefreshing = true;
        } else {
            mIsRefreshing = false;
        }

        return mIsRefreshing;
    }

    /**
     * Based on the current refreshing state, shows or hides a loading indicator from the bound
     * {@link MenuItem}, if applicable.
     */
    private void updateMenuItemState() {
        if (mMenuItem == null) {
            return;
        }

        boolean isMenuProgressShowing = (mMenuItem.getActionView() != null);
        if (!mIsRefreshing && isMenuProgressShowing) {
            // Hide progress indicator
            mMenuItem.setActionView(null);
        } else if (mIsRefreshing && !isMenuProgressShowing) {
            // Show progress indicator
            mMenuItem.setActionView(R.layout.actionbar_indeterminate_progress);
        } else {
            // Do nothing
        }
    }

    /**
     * Used to listen for refreshing state changes.
     */
    public class RefreshStateListener {
        public void onRefreshStateChanged(boolean isRefreshing) {
            // Subclasses can override this
        }
    }
}
