package com.thebluealliance.androidclient.activities.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.AuthenticatorActivity;
import com.thebluealliance.androidclient.activities.ContributorsActivity;
import com.thebluealliance.androidclient.activities.OpenSourceLicensesActivity;

public class SettingsActivity extends ActionBarActivity {

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
            appVersion.setSummary(BuildConfig.VERSION_NAME);

            Preference githubLink = findPreference("github_link");
            githubLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/")));

            Preference licenses = findPreference("licenses");
            licenses.setIntent(new Intent(getActivity(), OpenSourceLicensesActivity.class));

            Preference contributors = findPreference("contributors");
            contributors.setIntent(new Intent(getActivity(), ContributorsActivity.class));

            Preference notifications = findPreference("notifications");
            notifications.setIntent(new Intent(getActivity(), NotificationSettingsActivity.class));

            Preference changelog = findPreference("changelog");
            String version = BuildConfig.VERSION_NAME;
            if (version.contains("/")) {
                // if debug build, the version string will be like v0.1/#<sha hash>
                // so load the page for the most recent commit
                String sha = version.split("/")[1];
                sha = sha.replace("#", "");
                changelog.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/commit/" + sha)));
            } else {
                // this is not a debug build, so link to the GitHub release page tagged with the version name
                changelog.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/releases/tag/v" + version)));
            }

            Preference tbaLink = findPreference("tba_link");
            tbaLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thebluealliance.com")));

            final SwitchPreference enable_mytba = (SwitchPreference)findPreference("mytba_enabled");
            final Activity activity = getActivity();
            enable_mytba.setChecked(AccountHelper.isMyTBAEnabled(activity));
            enable_mytba.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean enabled = AccountHelper.isMyTBAEnabled(activity);
                    Log.d(Constants.LOG_TAG, "myTBA is: "+enabled);
                    if(!enabled){
                        Intent authIntent = AuthenticatorActivity.newInstance(activity, false);
                        activity.startActivity(authIntent);
                    }else{
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
