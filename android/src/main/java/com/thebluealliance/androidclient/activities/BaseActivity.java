package com.thebluealliance.androidclient.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.accounts.PlusHelper;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.gcm.GCMHelper;
import com.thebluealliance.androidclient.helpers.ModelHelper;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer,
 * a search button, and the ability to show and hide warning messages. Also provides Android Beam functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity
        implements NfcAdapter.CreateNdefMessageCallback, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    String beamUri;
    boolean searchEnabled = true;
    String modelKey = "";
    ModelHelper.MODELS modelType;

    GoogleAccountCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = this;
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void[] params) {
                /* Report the activity start to GAnalytics */
                GoogleAnalytics.getInstance(activity).reportActivityStart(activity);
                return null;
            }
        }.execute();

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Hide the shadow below the Action Bar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean mytba = AccountHelper.isMyTBAEnabled(this);
        if (!AccountHelper.isAccountSelected(this) && mytba) {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        } else if(mytba && !PlusHelper.isConnected() && !PlusHelper.isConnecting()){
            PlusHelper.connect(this, this, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* Report the activity stop to GAnalytics */
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
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

    public abstract void showWarningMessage(String message);

    public abstract void hideWarningMessage();

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

    protected void setModelKey(String key, ModelHelper.MODELS type){
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
        new UpdateMyTBA(this, new RequestParams(true, false)).execute();
        setDrawerProfileInfo();
    }

    /**
     * Successfully disconnected (called by PlusClient)
     */
    @Override
    public void onDisconnected() {

    }

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }


}
