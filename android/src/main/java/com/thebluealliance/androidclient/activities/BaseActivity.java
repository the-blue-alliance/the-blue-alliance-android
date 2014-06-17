package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer,
 * a search button, and the ability to show and hide warning messages. Also provides Android Beam functionality.
 */
public abstract class BaseActivity extends NavigationDrawerActivity implements NfcAdapter.CreateNdefMessageCallback {

    String beamUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
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

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        if (beamUri == null || beamUri.isEmpty()) {
            return null;
        } else {
            return new NdefMessage(new NdefRecord[]{NdefRecord.createMime("application/vnd.com.thebluealliance.androidclient", beamUri.getBytes())});
        }
    }
}
