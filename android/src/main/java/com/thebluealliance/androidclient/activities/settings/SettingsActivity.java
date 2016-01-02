package com.thebluealliance.androidclient.activities.settings;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.ContributorsActivity;
import com.thebluealliance.androidclient.activities.MyTBAOnboardingActivity;
import com.thebluealliance.androidclient.activities.OpenSourceLicensesActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference appVersion = findPreference("app_version");
            int versionCode = BuildConfig.VERSION_CODE;
            int major = BuildConfig.MAJOR_VERSION;
            int minor = BuildConfig.MINOR_VERSION;
            int patch = BuildConfig.PATCH_VERSION;

            String versionInfo;
            String commit = BuildConfig.GIT_HASH;
            String versionName = BuildConfig.GIT_TAG;
            String buildTime = Utilities.getBuildTimestamp(getActivity());
            if (commit.isEmpty()) {
                versionInfo = String.format(
                  getString(R.string.settings_build_info_summary),
                  versionName,
                  buildTime);
            } else {
                versionInfo = String.format(
                  getString(R.string.settings_build_info_summary_debug),
                  versionName,
                  buildTime,
                  commit);
            }
            appVersion.setSummary(versionInfo);
            appVersion.setIntent(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/" +
                            "commit/" + commit)));

            Preference githubLink = findPreference("github_link");
            githubLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/")));

            Preference licenses = findPreference("licenses");
            licenses.setIntent(new Intent(getActivity(), OpenSourceLicensesActivity.class));

            Preference contributors = findPreference("contributors");
            contributors.setIntent(new Intent(getActivity(), ContributorsActivity.class));

            Preference notifications = findPreference("notifications");
            notifications.setIntent(new Intent(getActivity(), NotificationSettingsActivity.class));

            Preference changelog = findPreference("changelog");
            changelog.setIntent(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/" +
                            "releases/tag/" + versionName)));

            Preference tbaLink = findPreference("tba_link");
            tbaLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thebluealliance.com")));

            final SwitchPreference enable_mytba = (SwitchPreference) findPreference("mytba_enabled");
            final Activity activity = getActivity();
            enable_mytba.setChecked(AccountHelper.isMyTBAEnabled(activity));
            enable_mytba.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean enabled = AccountHelper.isMyTBAEnabled(activity);
                    Log.d(Constants.LOG_TAG, "myTBA is: " + enabled);
                    if (!enabled) {
                        activity.startActivity(new Intent(getActivity(), MyTBAOnboardingActivity.class));
                    } else {
                        AccountHelper.enableMyTBA(activity, false);
                    }
                    return true;
                }
            });


            if (Utilities.isDebuggable()) {
                addPreferencesFromResource(R.xml.dev_preference_link);
                Preference devSettings = findPreference("dev_settings");
                devSettings.setIntent(new Intent(getActivity(), com.thebluealliance.androidclient.activities.settings.DevSettingsActivity.class));
            }
        }


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Remove padding from the list view
            View listView = getView().findViewById(android.R.id.list);
            if (listView != null) {
                listView.setPadding(0, 0, 0, 0);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            // Enable might have failed; update the state of the switch when we resume
            SwitchPreference enable_mytba = (SwitchPreference) findPreference("mytba_enabled");
            enable_mytba.setChecked(AccountHelper.isMyTBAEnabled(getActivity()));
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
