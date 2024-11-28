package com.thebluealliance.androidclient.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RedownloadActivity;
import com.thebluealliance.androidclient.auth.firebase.MigrateLegacyUserToFirebase;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBADataWorker;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.databinding.ActivitySettingsBinding;
import com.thebluealliance.androidclient.datafeed.status.StatusRefreshService;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationWorker;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateWorker;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.Cache;

@AndroidEntryPoint
public class DevSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new DevSettingsFragment())
                .commit();
    }

    @AndroidEntryPoint
    public static class DevSettingsFragment extends PreferenceFragmentCompat {

        @Inject AppConfig mConfig;
        @Inject Cache mOkCache;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.dev_preferences);

            Preference analyticsDryRun = findPreference("analytics_dry_run");
            if (analyticsDryRun != null) {
                analyticsDryRun.setOnPreferenceChangeListener((preference, newValue) -> {
                    Analytics.setAnalyticsDryRun(getActivity(), (boolean) newValue);
                    return true;
                });
            }

            Preference clearOkCache = findPreference("clear_okhttp_cache");
            if (clearOkCache != null) {
                clearOkCache.setOnPreferenceClickListener(preference -> {
                    if (mOkCache != null) {
                        Toast.makeText(getActivity(), "Clearing okhttp cache", Toast.LENGTH_SHORT).show();
                        try {
                            mOkCache.evictAll();
                            Toast.makeText(getActivity(), "Evicted OkHttp Cache", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            TbaLogger.e("Error clearing okcache", e);
                        }
                    }
                    return true;
                });
            }

            Preference refreshConfig = findPreference("refresh_remote_config");
            if (refreshConfig != null) {
                refreshConfig.setOnPreferenceClickListener(preference -> {
                    Toast.makeText(getActivity(), "Updating remote config...", Toast.LENGTH_SHORT).show();
                    mConfig.updateRemoteData(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update complete", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Error updating config", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                });
            }

            Preference configLookup = findPreference("config_lookup");
            if (configLookup != null) {
                configLookup.setOnPreferenceChangeListener((preference, o) -> {
                    if (o instanceof String) {
                        Toast.makeText(getActivity(), "Looking up key " + o, Toast.LENGTH_SHORT).show();
                        String confValue = mConfig.getString((String) o);
                        Toast.makeText(getActivity(), "Value: " + confValue, Toast.LENGTH_SHORT).show();
                    }
                    return false;
                });
            }

            Preference addMyTBAItem = findPreference("add_mytba_item");
            if (addMyTBAItem != null) {
                addMyTBAItem.setOnPreferenceClickListener(preference -> {
                /*Favorite fav = new Favorite();
                fav.setUserName(AccountHelper.getSelectedAccount(getActivity()));
                fav.setModelKey("frc111");
                fav.setModelEnum(ModelType.TEAM.getEnum());
                Database.getInstance(getActivity()).getFavoritesTable().add(fav);*/
                    Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
                    return true;
                });
            }

            Preference redownload = findPreference("redownload_data");
            if (redownload != null) {
                redownload.setOnPreferenceClickListener(preference -> {
                    Intent redownloadIntent = new Intent(getActivity(), RedownloadActivity.class);
                    redownloadIntent.putExtra(LoadTBADataWorker.DATA_TO_LOAD, new short[]{LoadTBADataWorker.LOAD_EVENTS, LoadTBADataWorker.LOAD_TEAMS, LoadTBADataWorker.LOAD_DISTRICTS});
                    startActivity(redownloadIntent);
                    return true;
                });
            }

            Preference updateStatus = findPreference("update_api_status");
            if (updateStatus != null) {
                updateStatus.setOnPreferenceClickListener(preference -> {
                    Intent serviceIntent = new Intent(getActivity(), StatusRefreshService.class);
                    getActivity().startService(serviceIntent);
                    Toast.makeText(getActivity(), R.string.update_status_launched, Toast.LENGTH_SHORT)
                            .show();
                    return true;
                });
            }

            Preference gcmRegister = findPreference("gcm_register");
            if (gcmRegister != null) {
                gcmRegister.setOnPreferenceClickListener((preference) -> {
                    WorkManager.getInstance(getActivity())
                            .enqueue(new OneTimeWorkRequest.Builder(MyTbaRegistrationWorker.class).build());
                    return false;
                });
            }

            Preference updateMytba = findPreference("update_mytba");
            if (updateMytba != null) {
                updateMytba.setOnPreferenceClickListener((preference) -> {
                    MyTbaUpdateWorker.run(getActivity(), true, true);
                    return false;
                });
            }

            Preference migrateAuth = findPreference("migrate_auth");
            if (migrateAuth != null) {
                migrateAuth.setOnPreferenceClickListener((preference) -> {
                    getActivity().startService(new Intent(getActivity(),
                            MigrateLegacyUserToFirebase.class));
                    return false;
                });
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
