package com.thebluealliance.androidclient.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.accounts.PlusHelper;
import com.thebluealliance.androidclient.gcm.GCMHelper;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;
import com.thebluealliance.androidclient.types.ModelType;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer, a search
 * button, and the ability to show and hide warning messages. Also provides Android Beam
 * functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity
        implements NfcAdapter.CreateNdefMessageCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @IntDef({WARNING_OFFLINE, WARNING_FIRST_API_DOWN, WARNING_EVENT_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WarningMessageType {}

    public static final int WARNING_OFFLINE = 3;
    public static final int WARNING_FIRST_API_DOWN = 2;
    public static final int WARNING_EVENT_DOWN = 1;

    public Set<Integer> activeMessages = new HashSet<>();

    String beamUri;
    boolean searchEnabled = true;
    String modelKey = "";
    ModelType modelType;

    /**
     * If this Activity was triggered by tapping a system notification, dismiss the "active" stored
     * notifications as having been "read."
     */
    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            sendBroadcast(NotificationDismissedListener.newIntent(this));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Hide the shadow below the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (searchEnabled) {
            getMenuInflater().inflate(R.menu.search_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * By default, all activities that extend BaseActivity will show global warning messages. If
     * you don't want a subclass to show warning messages, override this and return false;
     *
     * @return true if this activity should show warning messages, false if otherwise
     */
    public boolean shouldShowWarningMessages() {
        return true;
    }

    public void showWarningMessage(@WarningMessageType int messageType) {
        if (!shouldShowWarningMessages()) {
            return;
        }
        activeMessages.add(messageType);

        // The latest message to be shown might not be the highest priority
        // Find the highest priority active message and show that one
        displayMessageForMessageType(getHighestPriorityMessage());

    }

    private void displayMessageForMessageType(@WarningMessageType int type) {
        View container = findViewById(R.id.root_snackbar_container);
        TextView message = (TextView) findViewById(R.id.root_snackbar_message);
        TextView action = (TextView) findViewById(R.id.root_snackbar_action);

        // Set default visibilities
        container.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        action.setVisibility(View.GONE);

        switch (type) {
            case WARNING_OFFLINE:
                message.setText(R.string.warning_offline_message);
                action.setVisibility(View.VISIBLE);
                action.setText(R.string.warning_more);
                action.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.warning_more_info);
                    builder.setMessage(R.string.warning_offline_explanation);
                    builder.setCancelable(true);
                    builder.setNeutralButton(getString(R.string.warning_close),
                            (dialog, which) -> dialog.cancel()
                    );
                    builder.create().show();
                });
                break;
            case WARNING_EVENT_DOWN:
                message.setText(R.string.warning_event_down_message);
                action.setVisibility(View.VISIBLE);
                action.setText(R.string.warning_more);
                action.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.warning_more_info);
                    builder.setMessage(R.string.warning_event_down_explanation);
                    builder.setCancelable(true);
                    builder.setNeutralButton(getString(R.string.warning_close),
                            (dialog, which) -> dialog.cancel()
                    );
                    builder.create().show();
                });
                break;
            case WARNING_FIRST_API_DOWN:
                message.setText(R.string.warning_first_down_message);
                action.setVisibility(View.VISIBLE);
                action.setText(R.string.warning_more);
                action.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.warning_more_info);
                    builder.setMessage(R.string.warning_first_down_explanation);
                    builder.setCancelable(true);
                    builder.setNeutralButton(getString(R.string.warning_close),
                            (dialog, which) -> dialog.cancel()
                    );
                    builder.create().show();
                });
                break;
        }
    }

    public void dismissWarningMessage(@WarningMessageType int messageType) {
        activeMessages.remove(messageType);
        if (activeMessages.isEmpty()) {
            // There are no more active messages; hide the container
            findViewById(R.id.root_snackbar_container).setVisibility(View.GONE);
        } else {
            // There are more active messages; find the highest priority one and display it
            displayMessageForMessageType(getHighestPriorityMessage());
        }
    }

    public void dismissAllWarningMessages() {
        activeMessages.clear();
        findViewById(R.id.root_snackbar_container).setVisibility(View.GONE);
    }

    private @WarningMessageType int getHighestPriorityMessage() {
        int highestPriority = Integer.MIN_VALUE;
        for (int type : activeMessages) {
            if (type > highestPriority) {
                highestPriority = type;
            }
        }

        return highestPriority;
    }

    public void setBeamUri(String uri) {
        beamUri = uri;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        if (beamUri == null || beamUri.isEmpty()) {
            return null;
        } else {
            return new NdefMessage(new NdefRecord[]{NdefRecord.createMime("application/vnd.com.thebluealliance.androidclient", beamUri.getBytes())});
        }
    }

    protected void setSearchEnabled(boolean enabled) {
        searchEnabled = enabled;
        invalidateOptionsMenu();
    }

    protected void setModelKey(String key, ModelType type) {
        modelKey = key;
        modelType = type;
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        String accountName = PlusHelper.getAccountName();
        AccountHelper.setSelectedAccount(this, accountName);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(AccountHelper.PREF_MYTBA_ENABLED, true).apply();
        GCMHelper.registerGCMIfNeeded(this);
        startService(new Intent(this, MyTbaUpdateService.class));
    }

    /**
     * Connection failed for some reason (called by PlusClient) Try and resolve the result.
     * Failure here is usually not an indication of a serious error, just that the user's input
     * is needed.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
