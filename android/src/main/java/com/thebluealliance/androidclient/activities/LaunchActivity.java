package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.background.RecreateSearchIndexes;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData;
import com.thebluealliance.androidclient.database.Database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Database.getInstance(this);

        // Create intent to launch data download activity
        Intent redownloadIntent = new Intent(this, RedownloadActivity.class);
        boolean redownload = checkDataRedownload(redownloadIntent);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.ALL_DATA_LOADED_KEY, false) && !redownload) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage message = (NdefMessage) rawMsgs[0];
                String uri = new String(message.getRecords()[0].getPayload());
                Log.d(Constants.LOG_TAG, "NFC URI: " + uri);
                processNfcUri(uri);
                return;
            } else if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
                Uri data = getIntent().getData();
                Log.d(Constants.LOG_TAG, "VIEW URI: " + data.toString());
                if (data != null) {
                    //we caught an Action.VIEW intent, so
                    //now we generate the proper intent to view
                    //the requested content
                    Intent intent = Utilities.getIntentForTBAUrl(this, data);
                    if (intent != null) {
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        goToHome();
                        return;
                    }
                } else {
                    goToHome();
                    return;
                }
            } else {
                goToHome();
                return;
            }
        } else if (redownload) {
            // Start redownload activity
            startActivity(redownloadIntent);
        } else {
            // Go to onboarding activity
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        }
    }

    private boolean checkDataRedownload(Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt(Constants.APP_VERSION_KEY, -1);

        if (lastVersion == -1 && !prefs.getBoolean(Constants.ALL_DATA_LOADED_KEY, false)) {
            // on a clean install, don't think we're updating
            return false;
        }

        boolean redownload = false;
        Log.d(Constants.LOG_TAG, "Last version: " + lastVersion + "/" + BuildConfig.VERSION_CODE + " " + prefs.contains(Constants.APP_VERSION_KEY));
        if (prefs.contains(Constants.APP_VERSION_KEY) && lastVersion < BuildConfig.VERSION_CODE) {
            // We are updating the app. Do stuffs, if necessary.
            // TODO: make sure to modify changelog.txt with any recent changes
            if (lastVersion < 14) {
                // addition of districts. Download the required data
                redownload = true;
                intent.putExtra(LoadTBAData.DATA_TO_LOAD, new short[]{LoadTBAData.LOAD_EVENTS, LoadTBAData.LOAD_DISTRICTS});
            }

            if (lastVersion < 16) {
                // addition of myTBA - Prompt the user for an account
                redownload = true;
                intent.putExtra(LoadTBAData.DATA_TO_LOAD, new short[]{LoadTBAData.LOAD_EVENTS});
            }

            if (lastVersion < 21) {
                // redownload to get event short names
                redownload = true;
                intent.putExtra(LoadTBAData.DATA_TO_LOAD, new short[]{LoadTBAData.LOAD_EVENTS});
            }

            if (lastVersion < 46) {
                // recreate search indexes to contain foreign keys
                redownload = false;
                RecreateSearchIndexes.startActionRecreateSearchIndexes(this);
            }
        }
        // If we don't have to redownload, store the version code here. Otherwise, let the
        // RedownloadActivity store the version code upcn completion
        if (!redownload) {
            prefs.edit().putInt(Constants.APP_VERSION_KEY, BuildConfig.VERSION_CODE).apply();
        }
        return redownload;
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void processNfcUri(String uri) {
        Pattern regexPattern = Pattern.compile(NfcUris.URI_EVENT_MATCHER);
        Matcher m = regexPattern.matcher(uri);
        if (m.matches()) {
            String eventKey = m.group(1);
            TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events))
                    .addNextIntent(ViewEventActivity.newInstance(this, eventKey)).startActivities();
            finish();
            return;
        }

        regexPattern = Pattern.compile(NfcUris.URI_TEAM_MATCHER);
        m = regexPattern.matcher(uri);
        if (m.matches()) {
            String teamKey = m.group(1);
            TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_teams))
                    .addNextIntent(ViewTeamActivity.newInstance(this, teamKey)).startActivities();
            finish();
            return;
        }

        regexPattern = Pattern.compile(NfcUris.URI_TEAM_AT_EVENT_MATCHER);
        m = regexPattern.matcher(uri);
        if (m.matches()) {
            String eventKey = m.group(1);
            String teamKey = m.group(2);
            TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events))
                    .addNextIntent(ViewEventActivity.newInstance(this, eventKey))
                    .addNextIntent(TeamAtEventActivity.newInstance(this, eventKey, teamKey)).startActivities();
            finish();
            return;
        }

        regexPattern = Pattern.compile(NfcUris.URI_TEAM_IN_YEAR_MATCHER);
        m = regexPattern.matcher(uri);
        if (m.matches()) {
            String teamKey = m.group(1);
            String teamYear = m.group(2);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events))
                    .addNextIntent(ViewTeamActivity.newInstance(this, teamKey, Integer.valueOf(teamYear))).startActivities();
            finish();
            return;
        }

        regexPattern = Pattern.compile(NfcUris.URI_MATCH_MATCHER);
        m = regexPattern.matcher(uri);
        if (m.matches()) {
            String matchKey = m.group(1);
            String eventKey = matchKey.substring(0, matchKey.indexOf("_"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events))
                    .addNextIntent(ViewEventActivity.newInstance(this, eventKey))
                    .addNextIntent(ViewMatchActivity.newInstance(this, matchKey)).startActivities();
            finish();
            return;
        }

        // Default to kicking the user to the events list if none of the URIs match
        goToHome();
    }
}
