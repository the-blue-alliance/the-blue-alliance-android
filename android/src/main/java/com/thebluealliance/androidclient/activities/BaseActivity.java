package com.thebluealliance.androidclient.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Intent;
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
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.accounts.AddRemoveUserFavorite;
import com.thebluealliance.androidclient.accounts.AddRemoveUserSubscription;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.background.mytba.SetActionBarIcons;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer,
 * a search button, and the ability to show and hide warning messages. Also provides Android Beam functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity implements NfcAdapter.CreateNdefMessageCallback {

    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;
    String beamUri;
    boolean searchEnabled = true;
    boolean myTbaEnabled = false;
    String modelKey = "";

    GoogleAccountCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Report the activity start to GAnalytics */
        Tracker t = ((TBAAndroid) getApplication()).getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);

        if (!AccountHelper.isAccountSelected(this)) {
            signIn();
        } else {
            registerGCMIfNeeded();
            new UpdateMyTBA(this).execute();
        }

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
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
        if(myTbaEnabled){
            getMenuInflater().inflate(R.menu.user_favorite_menu, menu);
            getMenuInflater().inflate(R.menu.user_subscription_menu, menu);

            new SetActionBarIcons(this, menu.findItem(R.id.action_favorite), menu.findItem(R.id.action_subscribe)).execute(modelKey);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
            case R.id.action_favorite:
                if(myTbaEnabled) {
                    new AddRemoveUserFavorite(this, item).execute(modelKey);
                }
                return true;
            case R.id.action_subscribe:
                if(myTbaEnabled){
                    new AddRemoveUserSubscription(this, item).execute(modelKey, NotificationTypes.MATCH_SCORE);
                }
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

    protected void setModelKey(String key){
        myTbaEnabled = true;
        modelKey = key;
    }

    private void signIn(){
        int googleAccounts = AccountHelper.countGoogleAccounts(this);
        if (googleAccounts == 0) {
            // No accounts registered, nothing to do.
            Log.w(Constants.LOG_TAG, "No google accounts found.");
        } else if (googleAccounts == 1) {
            // If only one account then select it.
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                // Select account and perform authorization check.
                AccountHelper.setSelectedAccount(this, accounts[0].name);
            }
        } else {
            // More than one Google Account is present, a chooser is necessary.

            // Invoke an {@code Intent} to allow the user to select a Google account.
            Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                    "Select the account to use with The Blue Alliance", null, null, null);
            startActivityForResult(accountSelector,
                    ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.

            // Set the selected account.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            AccountHelper.setSelectedAccount(this, accountName);
            registerGCMIfNeeded();
            new UpdateMyTBA(this).execute();
        }
    }

    private void registerGCMIfNeeded() {
        if (!AccountHelper.checkGooglePlayServicesAvailable(this)) {
            Log.w(Constants.LOG_TAG, "Google Play Services unavailable. Can't register with GCM");
            return;
        }
        final String registrationId = GCMAuthHelper.getRegistrationId(this);
        if (TextUtils.isEmpty(registrationId)) {
            // GCM has not yet been registered on this device
            Log.d(Constants.LOG_TAG, "GCM is not currently registered. Registering....");
            GCMAuthHelper.registerInBackground(this);
        }
    }

}
