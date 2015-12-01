package com.thebluealliance.androidclient.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.accounts.PlusHelper;
import com.thebluealliance.androidclient.background.AnalyticsActions;
import com.thebluealliance.androidclient.gcm.GCMHelper;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer, a search
 * button, and the ability to show and hide warning messages. Also provides Android Beam
 * functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity
        implements NfcAdapter.CreateNdefMessageCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String beamUri;
    boolean searchEnabled = true;
    String modelKey = "";
    ModelType.MODELS modelType;

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
        new AnalyticsActions.ReportActivityStart(this).run();

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
    protected void onStop() {
        super.onStop();
        new AnalyticsActions.ReportActivityStop(this).run();
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

    public void showWarningMessage(CharSequence warningMessage) {
        // Do nothing by default
    }

    public void hideWarningMessage() {
        // Do nothing by default
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

    protected void setModelKey(String key, ModelType.MODELS type) {
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
        setDrawerProfileInfo();
        startService(new Intent(this, MyTbaUpdateService.class));
    }

    /**
     * Connection failed for some reason (called by PlusClient) Try and resolve the result.  Failure
     * here is usually not an indication of a serious error, just that the user's input is needed.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
