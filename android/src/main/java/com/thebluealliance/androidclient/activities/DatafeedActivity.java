package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.eventbus.ConnectivityChangeEvent;
import com.thebluealliance.androidclient.interfaces.InvalidateHost;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import dagger.hilt.android.AndroidEntryPoint;
import thebluealliance.api.model.APIStatus;

/**
 * An activity that serves as a host to datafeed fragments
 */
@AndroidEntryPoint
public abstract class DatafeedActivity extends BaseActivity
        implements InvalidateHost {

    @Inject protected RefreshController mRefreshController;
    @Inject protected TBAStatusController mStatusController;
    @Inject protected EventBus mEventBus;

    protected Menu mOptionsMenu;

    private boolean mRefreshEnabled;

    public DatafeedActivity() {
        mRefreshEnabled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshController.reset();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mRefreshEnabled) {
            getMenuInflater().inflate(R.menu.refresh_menu, menu);
            mRefreshController.bindToMenuItem(menu.findItem(R.id.refresh));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mRefreshController.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void setRefreshEnabled(boolean enabled) {
        mRefreshEnabled = enabled;
    }

    public RefreshController getRefreshController() {
        return mRefreshController;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
        APIStatus status = mStatusController.fetchApiStatus();
        commonStatusUpdate(status);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    private void commonStatusUpdate(@Nullable APIStatus newStatus) {
        if (newStatus == null) {
            return;
        }

        if (newStatus.getIsDatafeedDown()) {
            // Everything is broken
            showWarningMessage(BaseActivity.WARNING_FIRST_API_DOWN);
        } else {
            // Everything is not broken!
            dismissWarningMessage(BaseActivity.WARNING_FIRST_API_DOWN);
        }

        onTbaStatusUpdate(newStatus);
    }

    /**
     * Extending activities can override this method to respond to TBA status updates
     *
     * @param newStatus The new API Status
     */
    protected void onTbaStatusUpdate(APIStatus newStatus) {
        // Default to do nothing
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onConnectivityChanged(ConnectivityChangeEvent event) {
        if (event.getConnectivityChangeType() == ConnectivityChangeEvent.CONNECTION_FOUND) {
            dismissWarningMessage(BaseActivity.WARNING_OFFLINE);
            mRefreshController.startRefresh(RefreshController.NOT_REQUESTED_BY_USER);
        } else {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }
    }

    /**
     * Receive a notification for an to TBA status
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApiStatusUpdated(APIStatus tbaStatus) {
        commonStatusUpdate(tbaStatus);
    }
}
