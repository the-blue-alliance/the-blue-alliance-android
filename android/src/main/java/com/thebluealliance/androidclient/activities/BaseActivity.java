package com.thebluealliance.androidclient.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentSender;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.gcm.GCMHelper;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer,
 * a search button, and the ability to show and hide warning messages. Also provides Android Beam functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity implements NfcAdapter.CreateNdefMessageCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int RESOLVE_CONNECTION_REQUEST = 12897;

    String beamUri;

    boolean searchEnabled = true;

    private GoogleApiClient driveClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Report the activity start to GAnalytics */
        Tracker t = ((TBAAndroid) getApplication()).getTracker(TBAAndroid.GAnalyticsTracker.ANDROID_TRACKER);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);

        createDriveClient();

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(driveClient!= null){
            driveClient.connect();
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST:
                if (resultCode == RESULT_OK && driveClient != null) {
                    driveClient.connect();
                }
                break;
        }
    }

    protected void setSearchEnabled(boolean enabled) {
        searchEnabled = enabled;
        invalidateOptionsMenu();
    }

    private void registerGCMIfNeeded(){
        if(!GCMHelper.checkPlayServices(this)){
            Log.w(Constants.LOG_TAG, "Google Play Services unavailable. Can't register with GCM");
            return;
        }
        final String registrationId = GCMAuthHelper.getRegistrationId(this);
        if(TextUtils.isEmpty(registrationId)){
            // GCM has not yet been registered on this device
            Log.d(Constants.LOG_TAG, "GCM is not currently registered. Registering....");
            GCMAuthHelper.registerInBackground(this, driveClient);
        }
    }

    private void createDriveClient(){
        if(driveClient == null){
            driveClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        registerGCMIfNeeded();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }
}
