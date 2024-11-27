package com.thebluealliance.androidclient.activities.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.common.collect.ImmutableMap;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.activities.ContributorsActivity;
import com.thebluealliance.androidclient.activities.MyTBAOnboardingActivity;
import com.thebluealliance.androidclient.activities.OpenSourceLicensesActivity;
import com.thebluealliance.androidclient.databinding.ActivitySettingsBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        @Nullable ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

    @AndroidEntryPoint
    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Inject AccountController mAccountController;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            if (Utilities.isDebuggable()) {
                addPreferencesFromResource(R.xml.dev_preference_link);
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Preference appVersion = findPreference("app_version");

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
            if (appVersion != null) {
                appVersion.setSummary(versionInfo);
                appVersion.setIntent(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/"
                                + "commit/" + commit)));
            }

            Preference githubLink = findPreference("github_link");
            if (githubLink != null) {
                githubLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/")));
            }

            Preference licenses = findPreference("licenses");
            if (licenses != null) {
                licenses.setIntent(new Intent(getActivity(), OpenSourceLicensesActivity.class));
            }

            Preference contributors = findPreference("contributors");
            if (contributors != null) {
                contributors.setIntent(new Intent(getActivity(), ContributorsActivity.class));
            }

            Preference notifications = findPreference("notifications");
            if (notifications != null) {
                notifications.setIntent(new Intent(getActivity(), NotificationSettingsActivity.class));
            }

            Preference changelog = findPreference("changelog");
            if (changelog != null) {
                changelog.setIntent(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/"
                                + "releases/tag/v" + versionName)));
            }

            Preference tbaLink = findPreference("tba_link");
            tbaLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thebluealliance.com")));

            final SwitchPreference mytbaEnabled = findPreference("mytba_enabled");
            final Activity activity = getActivity();
            if (mytbaEnabled != null) {
                mytbaEnabled.setChecked(mAccountController.isMyTbaEnabled());
                mytbaEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = mAccountController.isMyTbaEnabled();
                    TbaLogger.d("myTBA is: " + enabled);
                    if (!enabled) {
                        activity.startActivity(new Intent(getActivity(), MyTBAOnboardingActivity.class));
                    } else {
                        mAccountController.setMyTbaEnabled(false);
                    }
                    return true;
                });
            }

            ImmutableMap<String, String> darkModePrefsForAppVersion = getDarkModeOptions();
            final ListPreference darkModePref = (ListPreference) findPreference("dark_mode");
            if (darkModePref != null) {
                darkModePref.setEntries(darkModePrefsForAppVersion.keySet().toArray(new String[]{}));
                darkModePref.setEntryValues(darkModePrefsForAppVersion.values().toArray(new String[]{}));
                darkModePref.setOnPreferenceChangeListener((preference, newPref) -> {
                    int newValue = Utilities.getCurrentDarkModePreference((String) newPref);
                    TbaLogger.d("Setting dark mode preference to " + newValue);
                    AppCompatDelegate.setDefaultNightMode(newValue);
                    return true;
                });
            }

            if (Utilities.isDebuggable()) {
                Preference devSettings = findPreference("dev_settings");
                if (devSettings != null) {
                    devSettings.setIntent(new Intent(getActivity(), com.thebluealliance.androidclient.activities.settings.DevSettingsActivity.class));
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            // Enable might have failed; update the state of the switch when we resume
            SwitchPreference mytbaEnabled = findPreference("mytba_enabled");
            if (mytbaEnabled != null) {
                mytbaEnabled.setChecked(mAccountController.isMyTbaEnabled());
            }
        }


        private ImmutableMap<String, String> getDarkModeOptions() {
            ImmutableMap.Builder<String, String> baseBuilder = new ImmutableMap.Builder<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                baseBuilder.put(getString(R.string.pref_dark_mode_default), "system");
            } else {
                baseBuilder.put(getString(R.string.pref_dark_mode_battery_saver), "battery");
            }
            return baseBuilder .put(getString(R.string.pref_dark_mode_light), "light")
                    .put(getString(R.string.pref_dark_mode_dark), "dark")
                    .build();
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
