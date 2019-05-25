package com.thebluealliance.androidclient.activities.settings;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.thebluealliance.androidclient.R;

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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notification_preferences);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addPreferencesFromResource(R.xml.notification_preferences_lollipop);
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
