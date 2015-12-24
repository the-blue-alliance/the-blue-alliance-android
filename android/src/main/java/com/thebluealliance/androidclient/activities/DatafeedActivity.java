package com.thebluealliance.androidclient.activities;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.eventbus.ConnectivityChangeEvent;
import com.thebluealliance.androidclient.interfaces.InvalidateHost;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.models.APIStatus;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * An activity that serves as a host to datafeed fragments
 */
public abstract class DatafeedActivity extends BaseActivity
        implements HasFragmentComponent, InvalidateHost {

    @Inject RefreshController mRefreshController;
    @Inject TBAStatusController mStatusController;
    @Inject EventBus mEventBus;

    protected FragmentComponent mComponent;
    protected Menu mOptionsMenu;

    private boolean mRefreshEnabled;

    public DatafeedActivity() {
        mRefreshEnabled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
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

        if (newStatus.isFmsApiDown()) {
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
    public void onEvent(ConnectivityChangeEvent event) {
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
    public void onEvent(APIStatus tbaStatus) {
        commonStatusUpdate(tbaStatus);
    }

    public abstract void inject();

    @Override
    public FragmentComponent getComponent() {
        if (mComponent == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponent = DaggerFragmentComponent.builder()
                    .applicationComponent(application.getComponent())
                    .datafeedModule(application.getDatafeedModule())
                    .binderModule(application.getBinderModule())
                    .databaseWriterModule(application.getDatabaseWriterModule())
                    .subscriberModule(new SubscriberModule(this))
                    .clickListenerModule(new ClickListenerModule(this))
                    .build();
        }
        return mComponent;
    }
}
