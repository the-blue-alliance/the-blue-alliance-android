package com.thebluealliance.androidclient.activities.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.databinding.ActivitySettingsBinding;

public class NotificationSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment existingFragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
        if (existingFragment == null || !existingFragment.getClass().equals(NotificationSettingsFragment.class)) {
            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, new NotificationSettingsFragment())
                    .commit();
        }
    }

    public static class NotificationSettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.notification_preferences, rootKey);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notification_preferences_lollipop);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                addPreferencesFromResource(R.xml.notification_preferences_tiramisu);

                SwitchPreference notifPermissionPref = findPreference("notification_permission_enabled");
                ActivityResultLauncher<String> notificationPermissionLauncher =
                        registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> notifPermissionPref.setChecked(result));
                boolean hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
                notifPermissionPref.setChecked(hasPermission);
                notifPermissionPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (!((boolean) newValue)) {
                        getContext().revokeSelfPermissionOnKill(Manifest.permission.POST_NOTIFICATIONS);
                    } else {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                    return true;
                });
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
