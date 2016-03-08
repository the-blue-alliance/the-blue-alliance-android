package com.thebluealliance.androidclient.activities.settings;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NotificationSettingsFragment())
                .commit();
    }

    public static class NotificationSettingsFragment extends PreferenceFragment {

        private static Preference notificationTone;
        private static Preference notificationVibrate;
        private static @Nullable Preference  notificationVisibility;
        private static @Nullable Preference notificationHeadsup;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notification_preferences);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addPreferencesFromResource(R.xml.notification_preferences_lollipop);
            }

            CheckBoxPreference enableNotifications = (CheckBoxPreference) findPreference("enable_notifications");
            notificationTone = findPreference("notification_tone");
            notificationVibrate = findPreference("notification_vibrate");
            notificationVisibility = null;
            notificationHeadsup = null;
            if (Utilities.hasLApis()) {
                notificationVisibility = findPreference("notification_visibility");
                notificationHeadsup = findPreference("notification_headsup");
            }

            boolean currentlyEnabled = enableNotifications.isChecked();
            notificationTone.setEnabled(currentlyEnabled);
            notificationVibrate.setEnabled(currentlyEnabled);

            enableNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean value = (Boolean) newValue;
                notificationTone.setEnabled(value);
                notificationVibrate.setEnabled(value);
                if (notificationVisibility != null) {
                    notificationVisibility.setEnabled(value);
                }
                if (notificationHeadsup != null) {
                    notificationHeadsup.setEnabled(value);
                }
                return true;
            });
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
