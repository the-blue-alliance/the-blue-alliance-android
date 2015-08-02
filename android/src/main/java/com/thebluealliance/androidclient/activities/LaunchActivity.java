package com.thebluealliance.androidclient.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.FirstLaunchFragmentAdapter;
import com.thebluealliance.androidclient.background.RecreateSearchIndexes;
import com.thebluealliance.androidclient.background.firstlaunch.LoadAllData;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.views.DisableSwipeViewPager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nathan on 5/25/2014.
 */
public class LaunchActivity extends Activity implements View.OnClickListener, LoadAllData.LoadAllDataCallbacks {

    public static final String ALL_DATA_LOADED = "all_data_loaded";
    public static final String REDOWNLOAD = "redownload";
    public static final String DATA_TO_REDOWNLOAD = "redownload_data";
    public static final String APP_VERSION_KEY = "app_version";
    private static final String CURRENT_LOADING_MESSAGE_KEY = "current_loading_message";

    protected DisableSwipeViewPager viewPager;

    private TextView loadingMessage;

    private String currentLoadingMessage = "";

    private LoadAllDataTaskFragment loadFragment;
    private static final String LOAD_FRAGMENT_TAG = "loadFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Database.getInstance(this);

        Log.i(Constants.LOG_TAG, "All data loaded? " + PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ALL_DATA_LOADED, false));
        boolean redownload = checkDataRedownload();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ALL_DATA_LOADED, false) && !redownload) {
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
                        finish();
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
        }
        setContentView(R.layout.activity_launch);
        viewPager = (DisableSwipeViewPager) findViewById(R.id.view_pager);
        viewPager.setSwipeEnabled(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FirstLaunchFragmentAdapter(this));
        loadingMessage = (TextView) findViewById(R.id.message);

        // If the activity is being recreated after a config change, restore the message that was
        // being shown when the last activity was destroyed
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_LOADING_MESSAGE_KEY)) {
                currentLoadingMessage = savedInstanceState.getString(CURRENT_LOADING_MESSAGE_KEY);
                loadingMessage.setText(currentLoadingMessage);
            }
        }
        findViewById(R.id.welcome_next_page).setOnClickListener(this);
        findViewById(R.id.finish).setOnClickListener(this);
        if (redownload) {
            ((TextView) findViewById(R.id.welcome_message)).setText(getString(R.string.update_message));
        }
        loadFragment = (LoadAllDataTaskFragment) getFragmentManager().findFragmentByTag(LOAD_FRAGMENT_TAG);
        if (loadFragment != null) {
            viewPager.setCurrentItem(1, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_LOADING_MESSAGE_KEY, currentLoadingMessage);
    }

    private boolean checkDataRedownload() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt(APP_VERSION_KEY, -1);

        if (lastVersion == -1 && !prefs.getBoolean(ALL_DATA_LOADED, false)) {
            // on a clean install, don't think we're updating
            return false;
        }
        if (getIntent().getBooleanExtra(REDOWNLOAD, false)) {
            return true;
        }

        boolean redownload = false;
        Log.d(Constants.LOG_TAG, "Last version: " + lastVersion + "/" + BuildConfig.VERSION_CODE + " " + prefs.contains(APP_VERSION_KEY));
        if (prefs.contains(APP_VERSION_KEY) && lastVersion < BuildConfig.VERSION_CODE) {
            //we are updating the app. Do stuffs. Start from the next version
            lastVersion++;
            while (lastVersion <= BuildConfig.VERSION_CODE) {
                Log.v(Constants.LOG_TAG, "Updating app to version " + lastVersion);
                switch (lastVersion) {
                    case 14: //addition of districts. Download the required data
                        redownload = true;
                        getIntent().putExtra(LaunchActivity.DATA_TO_REDOWNLOAD, new short[]{LoadAllDataTaskFragment.LOAD_EVENTS, LaunchActivity.LoadAllDataTaskFragment.LOAD_DISTRICTS});
                        getIntent().putExtra(LaunchActivity.REDOWNLOAD, true);
                        break;
                    case 16: //addition of myTBA - Prompt the user for an account
                        redownload = true;
                        getIntent().putExtra(LaunchActivity.DATA_TO_REDOWNLOAD, new short[]{LoadAllDataTaskFragment.LOAD_EVENTS});
                        getIntent().putExtra(LaunchActivity.REDOWNLOAD, true);
                        break;
                    case 21: //redownload to get event short names
                        redownload = true;
                        getIntent().putExtra(LaunchActivity.DATA_TO_REDOWNLOAD, new short[]{LoadAllDataTaskFragment.LOAD_EVENTS});
                        getIntent().putExtra(LaunchActivity.REDOWNLOAD, true);
                        break;
                    case 43: //bugfix: extra 2015 CMP division. Remove its cached response so it'll get downloaded again
                        Database.getInstance(this).getResponseTable().deleteResponse("http://www.thebluealliance.com/api/v2/events/2015");
                        break;
                    case 46: //recreate search indexes to contain foreign keys
                        RecreateSearchIndexes.startActionRecreateSearchIndexes(this);
                        break;
                    default:
                        break;
                }
                lastVersion++;
            }
        }
        // Store the current version key
        prefs.edit().putInt(APP_VERSION_KEY, BuildConfig.VERSION_CODE).apply();
        return redownload;
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void authenticate() {
        startActivity(AuthenticatorActivity.newInstance(this, true));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.welcome_next_page:
                beginLoadingIfConnected();
                break;
            case R.id.finish:
                authenticate();
                break;
        }
    }

    private void beginLoadingIfConnected() {
        if (ConnectionDetector.isConnectedToInternet(this)) {
            viewPager.advanceToNextPage();
            beginLoading();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // Set title
            alertDialogBuilder.setTitle("Check connection");

            // Set dialog message
            alertDialogBuilder.setMessage(getString(R.string.warning_no_internet_connection)).setCancelable(false)
                    .setPositiveButton(getString(R.string.retry), (dialog, id) -> {
                        beginLoadingIfConnected();
                        dialog.dismiss();
                    }).setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                        finish();
                    });

            // Create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // Show it
            alertDialog.show();
        }
    }

    private void beginLoading() {
        Fragment f = new LoadAllDataTaskFragment();
        if (getIntent().hasExtra(DATA_TO_REDOWNLOAD)) {
            Bundle args = new Bundle();
            args.putShortArray(LoadAllDataTaskFragment.DATA_TO_LOAD, getIntent().getShortArrayExtra(DATA_TO_REDOWNLOAD));
            f.setArguments(args);
        }
        f.setRetainInstance(true);
        getFragmentManager().beginTransaction().add(f, LOAD_FRAGMENT_TAG).commit();
    }

    public void errorLoadingData(final String stacktrace) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set title
        alertDialogBuilder.setTitle(getString(R.string.fatal_error));

        // Set dialog message
        alertDialogBuilder.setMessage(getString(R.string.fatal_error_message)).setCancelable(false).setPositiveButton(R.string.contact_developer, (dialog, id) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "contact@thebluealliance.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FATAL ERROR");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Version: " + BuildConfig.VERSION_NAME + "\nStacktrace:\n" + stacktrace);
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            finish();
        }).setNegativeButton(R.string.cancel, (dialog, id) -> {
            finish();
        });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            // Show it
            alertDialog.show();
        } catch (WindowManager.BadTokenException e) {
            // Activity is already gone. Just log the exception
            Log.e(Constants.LOG_TAG, "Error loading data: " + stacktrace);
            e.printStackTrace();
        }
    }

    public void connectionLost() {
        // Scroll to first page
        viewPager.setCurrentItem(0);
        //Cancel task
        if (loadFragment != null) {
            loadFragment.cancelTask();
        }
        // Show a warning
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set title
        alertDialogBuilder.setTitle("Connection lost");

        // Set dialog message
        alertDialogBuilder.setMessage(getString(R.string.connection_lost)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                    dialog.dismiss();
                });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();
    }

    public void loadingFinished() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ALL_DATA_LOADED, true).commit();
    }

    public void onProgressUpdate(LoadAllData.LoadProgressInfo info) {
        if (info.state == LoadAllData.LoadProgressInfo.STATE_NO_CONNECTION) {
            connectionLost();
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_LOADING && loadingMessage != null) {
            currentLoadingMessage = info.message;
            loadingMessage.setText(currentLoadingMessage);
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_FINISHED) {
            loadingFinished();
            if (viewPager != null) {
                viewPager.advanceToNextPage();
            } else {
                // Pager is null, skipping to HomeActivity
                startActivity(HomeActivity.newInstance(this, R.id.nav_item_events));
            }
        } else if (info.state == LoadAllData.LoadProgressInfo.STATE_ERROR) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ALL_DATA_LOADED, false).commit();
            errorLoadingData(info.message);
        }
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

    public static class LoadAllDataTaskFragment extends Fragment implements LoadAllData.LoadAllDataCallbacks {

        public static final String DATA_TO_LOAD = "data_to_load";
        public static final String REDOWNLOAD = "redownload";
        public static final short LOAD_TEAMS = 0,
                LOAD_EVENTS = 1,
                LOAD_DISTRICTS = 2;

        LoadAllData.LoadAllDataCallbacks callback;
        private LoadAllData task;
        private Short[] dataToLoad;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            if (activity instanceof LoadAllData.LoadAllDataCallbacks) {
                callback = (LoadAllData.LoadAllDataCallbacks) activity;
            } else {
                throw new IllegalStateException("TaskFragment must be hosted by an activity that implements LoadAllDataCallbacks");
            }

            if (getArguments() != null && getArguments().containsKey(DATA_TO_LOAD)) {
                short[] inData = getArguments().getShortArray(DATA_TO_LOAD);
                dataToLoad = new Short[inData.length];
                for (int i = 0; i < dataToLoad.length; i++) {
                    dataToLoad[i] = inData[i];
                }
            } else if (getArguments() != null) {
                //don't load any data
            } else {
                dataToLoad = new Short[]{LOAD_TEAMS, LOAD_EVENTS, LOAD_DISTRICTS};
            }

            if (task == null) {
                task = new LoadAllData(this, getActivity());
                task.execute(dataToLoad);
            }
        }

        public void cancelTask() {
            task.cancel(false);
        }

        @Override
        public void onProgressUpdate(LoadAllData.LoadProgressInfo info) {
            if (callback != null) {
                callback.onProgressUpdate(info);
            }
        }
    }
}
