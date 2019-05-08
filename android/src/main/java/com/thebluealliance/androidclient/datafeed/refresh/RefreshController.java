package com.thebluealliance.androidclient.datafeed.refresh;

import androidx.annotation.IntDef;
import androidx.annotation.UiThread;
import androidx.collection.ArrayMap;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides an easy way to manage a collection of {@link Refreshable} objects. {@link Refreshable}
 * objects should register themselves with this class; they will receive callbacks when a refresh
 * is requested. Additionally, they can push their current state (refreshing or not) into this
 * class. This allows us to determine if any registered objects are currently refreshing and take
 * the appropriate action.
 * <p>
 * The primary way this class will be used is to work with a {@link MenuItem} that controls
 * refreshing. When you bind a {@link RefreshController} to a {@link MenuItem} with the {@link
 * RefreshController#bindToMenuItem(MenuItem)} method, this class will monitor the state of its
 * registered {@link Refreshable}s and replace the item's action view with a progress indicator and
 * revert the icon to its normal state when the refresh is finished. And, as long as you proxy
 * {@link android.app.Activity#onOptionsItemSelected(MenuItem)} calls through to this class, it
 * will automatically start a refresh when the bound {@link MenuItem} is clicked.
 */
@Singleton
public class RefreshController {

    /**
     * Constants for refresh type
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REQUESTED_BY_USER, NOT_REQUESTED_BY_USER})
    public @interface RefreshType {}

    public static final int REQUESTED_BY_USER = 0;
    public static final int NOT_REQUESTED_BY_USER = 1;

    /**
     * Maps refresh tags to {@link Refreshable} objects and their current refreshing state
     */
    private Map<String, RefreshWrapper> mRefreshableStates;

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

    @Inject
    public RefreshController() {
        mRefreshableStates = new ArrayMap<>();
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
        if (menuItem == null) {
            return;
        }
        mMenuItem = menuItem;
        updateMenuItemState();
    }

    /**
     * Calls to {@link android.app.Activity#onOptionsItemSelected(MenuItem)} should be proxied
     * through to this method if you want to take advantage of automatically starting a refresh
     * when the bound {@link MenuItem} is clicked.
     *
     * @param menuItem the {@link MenuItem} that was clicked.
     * @return true if the click was handled, false otherwise
     */
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (mMenuItem == null) {
            return false;
        }
        if (menuItem.getItemId() == mMenuItem.getItemId()) {
            // Refresh button clicked, start refresh
            startRefresh(REQUESTED_BY_USER);
            // Click handled
            return true;
        }
        return false;
    }

    public void registerRefreshable(String refreshTag, Refreshable refreshable) {
        // Default to "not refreshing"
        mRefreshableStates.put(refreshTag, new RefreshWrapper(refreshable, false));
    }

    public void unregisterRefreshable(String refreshTag) {
        mRefreshableStates.remove(refreshTag);
    }

    /**
     * Simply calls {@link Refreshable#onRefreshStart(int)} on all registered {@link
     * Refreshable}s.
     */
    public void startRefresh(@RefreshType int refreshType) {
        if (mIsRefreshing) {
            return;
        }

        if (mRefreshableStates.isEmpty()) {
            return;
        }
        mIsRefreshing = true;
        for (RefreshWrapper wrapper : mRefreshableStates.values()) {
            Refreshable refreshable = wrapper.getRefreshable();
            if (refreshable != null) {
                refreshable.onRefreshStart(refreshType);
            }
        }
    }

    /**
     * Called to notify this class that a {@link Refreshable}'s refreshing state has changed. Note
     * that this will also register the {@link Refreshable} for future refresh start callbacks if
     * it was not already registered.
     *
     * @param refreshKey   the String linking to the {@link Refreshable} object being updated
     * @param isRefreshing true if the {@link Refreshable} is currently refreshing, false if it is
     *                     not
     */
    @UiThread
    public void notifyRefreshingStateChanged(String refreshKey, boolean isRefreshing) {
        RefreshWrapper wrapper = mRefreshableStates.get(refreshKey);
        if (wrapper == null) {
            return;
        }
        wrapper.setRefreshState(isRefreshing);
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
     * Resets the state of the instance
     */
    public void reset() {
        mIsRefreshing = false;
        mRefreshableStates.clear();
    }

    /**
     * Checks to see if any {@link Refreshable}s are refreshing, and updates an internal flag with
     * the result.
     *
     * @return returns the value of {@code mIsRefreshing} for convenience
     */
    @UiThread
    private boolean updateRefreshingState() {
        Collection<RefreshWrapper> refreshingStates = mRefreshableStates.values();
        for (RefreshWrapper wrapper : refreshingStates) {
            if (wrapper.getRefreshState()) {
                mIsRefreshing = true;
                return true;
            }
        }
        mIsRefreshing = false;
        return false;
    }

    /**
     * Based on the current refreshing state, shows or hides a loading indicator from the bound
     * {@link MenuItem}, if applicable.
     */
    @UiThread
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
        }
    }
}
