package com.thebluealliance.androidclient.activities.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 9/5/14.
 */
public class NotificationSettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NotificationSettingsFragment())
                .commit();
    }

    public static class NotificationSettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notification_preferences);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                addPreferencesFromResource(R.xml.notification_preferences_lollipop);
                Preference systemSettings = findPreference("system_notification_settings");
                systemSettings.setIntent(new Intent(Settings.ACTION_SETTINGS));
            }

            CheckBoxPreference enableNotifications = (CheckBoxPreference)findPreference("enable_notifications");
            final Preference notificationTone = findPreference("notification_tone");
            final Preference notificationVibrate = findPreference("notification_vibrate");

            boolean currentlyEnabled = enableNotifications.isChecked();
            notificationTone.setEnabled(currentlyEnabled);
            notificationVibrate.setEnabled(currentlyEnabled);

            enableNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean)newValue;
                    notificationTone.setEnabled(value);
                    notificationVibrate.setEnabled(value);
                    return true;
                }
            });
        }
    }

}
