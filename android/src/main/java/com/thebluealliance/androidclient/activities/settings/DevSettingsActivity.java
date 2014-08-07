package com.thebluealliance.androidclient.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.LaunchActivity;

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
                    Analytics.setAnalyticsDryRun(getActivity(), (boolean) newValue);
                    return true;
                }
            });

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
