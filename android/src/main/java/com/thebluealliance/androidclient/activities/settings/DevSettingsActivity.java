package com.thebluealliance.androidclient.activities.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.gcm.GCMMessageDispatcher;

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
                    GCMMessageDispatcher.dispatchMessage(getActivity(), "upcoming_match", data);
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
