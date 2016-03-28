package com.thebluealliance.androidclient.activities.settings;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class NotificationSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment existingFragment = getFragmentManager().findFragmentById(android.R.id.content);
        if (existingFragment == null || !existingFragment.getClass().equals(NotificationSettingsFragment.class)) {
            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new NotificationSettingsFragment())
                    .commit();
        }
    }

    public static class NotificationSettingsFragment extends PreferenceFragment {

        private static Preference notificationTone;
        private static Preference notificationVibrate;
        private static CheckBoxPreference notificationLedEnabled;
        private static Preference notificationLedColor;
        private static @Nullable Preference notificationHeadsup;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notification_preferences);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addPreferencesFromResource(R.xml.notification_preferences_lollipop);
            }

            notificationTone = findPreference("notification_tone");
            notificationVibrate = findPreference("notification_vibrate");
            notificationHeadsup = null;
            if (Utilities.hasLApis()) {
                notificationHeadsup = findPreference("notification_headsup");
            }
            notificationLedEnabled = (CheckBoxPreference) findPreference("notification_led_enabled");
            notificationLedColor = findPreference("notification_led_color");

            CheckBoxPreference enableNotifications = (CheckBoxPreference) findPreference("enable_notifications");
            boolean currentlyEnabled = enableNotifications.isChecked();
            setPreferencesEnabled(currentlyEnabled);

            enableNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (Boolean) newValue;
                setPreferencesEnabled(enabled);
                return true;
            });

            notificationLedColor.setEnabled(notificationLedEnabled.isChecked());

            notificationLedEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (Boolean) newValue;
                notificationLedColor.setEnabled(enabled);
                return true;
            });
        }

        private void setPreferencesEnabled(boolean enabled) {
            notificationTone.setEnabled(enabled);
            notificationVibrate.setEnabled(enabled);
            notificationLedEnabled.setEnabled(enabled);
            notificationLedColor.setEnabled(enabled);
            if (notificationHeadsup != null) {
                notificationHeadsup.setEnabled(enabled);
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

}
