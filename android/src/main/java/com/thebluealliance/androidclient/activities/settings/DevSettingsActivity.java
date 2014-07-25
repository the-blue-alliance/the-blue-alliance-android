package com.thebluealliance.androidclient.activities.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;

public class DevSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DevSettingsFragment())
                .commit();
    }

    public static class DevSettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.dev_preferences);

            Preference analytics_dryRyn = findPreference("analytics_dry_run");
            analytics_dryRyn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ((TBAAndroid)getActivity().getApplication()).setAnalyticsDryRun((boolean)newValue);
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
