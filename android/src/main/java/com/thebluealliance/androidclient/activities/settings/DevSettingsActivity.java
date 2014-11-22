package com.thebluealliance.androidclient.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;

public class DevSettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DevSettingsFragment())
                .commit();
    }

    public static class DevSettingsFragment extends PreferenceFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.dev_preferences);

            Preference analytics_dryRyn = findPreference("analytics_dry_run");
            analytics_dryRyn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Analytics.setAnalyticsDryRun(getActivity(), (boolean) newValue);
                    return true;
                }
            });

            Preference testUpcomingMatchNotification = findPreference("test_upcoming_match_notification");
            testUpcomingMatchNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String data = "{\"match_key\":\"2014ilch_f1m2\",\"event_name\":\"EVENT NAME\",\"team_keys\":[\"frc111\",\"frc118\",\"frc254\",\"frc496\",\"frc1114\",\"frc2056\"],\"scheduled_time\":12345,\"predicted_time\":123456}";
                    GCMMessageHandler.handleMessage(getActivity(), "upcoming_match", data);
                    return true;
                }
            });


            Preference testScoreNotification = findPreference("test_score_notification");
            testScoreNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Comment out different strings to see different types of notifications.
                    // This is kind of a hacky way of demonstrating notifications; it just removes
                    // teams from the alliance json objects to simulate different numbers of favorited teams.
                    // This will break once we actually check if teams are favorited while constructing notifications.

                    // All teams are favorited
                    //String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\",\"frc1102\"]},\"red\":{\"score\":0,\"teams\":[\"frc1398\",\"frc343\",\"frc1261\"]}},\"event_key\":\"2010sc\"}}";
                    // 2 teams on winning alliance are favorited
                    //String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\"]},\"red\":{\"score\":0,\"teams\":[]}},\"event_key\":\"2010sc\"}}";
                    // 1 team on losing alliance is favorited
                    //String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[]},\"red\":{\"score\":0,\"teams\":[\"frc1398\"]}},\"event_key\":\"2010sc\"}}";
                    // 2 teams on tied alliance are favorited
                    // String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\"]},\"red\":{\"score\":5,\"teams\":[]}},\"event_key\":\"2010sc\"}}";
                    // 2 teams on each tied alliance are favorited
                    String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\"]},\"red\":{\"score\":5,\"teams\":[\"frc1398\",\"frc343\"]}},\"event_key\":\"2010sc\"}}";
                    GCMMessageHandler.handleMessage(getActivity(), "score", data);
                    return true;
                }
            });

            Preference gcmRegister = findPreference("select_account");
            gcmRegister.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    GoogleAccountCredential credential = AccountHelper.getSelectedAccountCredential(getActivity());
                    getActivity().startActivity(credential.newChooseAccountIntent());
                    return false;
                }
            });
        }

        @Override
        public void onConnected(Bundle bundle) {
            GCMAuthHelper.registerInBackground(getActivity());
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(), 0).show();
            Preference redownload = findPreference("redownload_data");
            Intent redownloadIntent = new Intent(getActivity(), LaunchActivity.class);
            redownloadIntent.putExtra(LaunchActivity.REDOWNLOAD, true);
            redownload.setIntent(redownloadIntent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
