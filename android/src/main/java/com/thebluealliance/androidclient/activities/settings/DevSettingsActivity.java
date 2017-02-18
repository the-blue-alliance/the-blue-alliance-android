package com.thebluealliance.androidclient.activities.settings;

import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.activities.RedownloadActivity;
import com.thebluealliance.androidclient.auth.firebase.MigrateLegacyUserToFirebase;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData;
import com.thebluealliance.androidclient.datafeed.status.StatusRefreshService;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationService;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Cache;

public class DevSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DevSettingsFragment())
                .commit();
    }

    public static class DevSettingsFragment extends PreferenceFragment {

        @Inject Cache mOkCache;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((TBAAndroid)(getActivity().getApplication())).getDatafeedComponenet().inject(this);
            addPreferencesFromResource(R.xml.dev_preferences);

            Preference analyticsDryRun = findPreference("analytics_dry_run");
            analyticsDryRun.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Analytics.setAnalyticsDryRun(getActivity(), (boolean) newValue);
                    return true;
                }
            });

            Preference clearOkCache = findPreference("clear_okhttp_cache");
            clearOkCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (mOkCache != null) {
                        TbaLogger.i("Clearing okhttp cache");
                        try {
                            mOkCache.evictAll();
                        } catch (IOException e) {
                            TbaLogger.e("Error clearing okcache", e);
                        }
                    }
                    return false;
                }
            });

            Preference addMyTBAItem = findPreference("add_mytba_item");
            addMyTBAItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    /*Favorite fav = new Favorite();
                    fav.setUserName(AccountHelper.getSelectedAccount(getActivity()));
                    fav.setModelKey("frc111");
                    fav.setModelEnum(ModelType.TEAM.getEnum());
                    Database.getInstance(getActivity()).getFavoritesTable().add(fav);*/
                    Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            Preference redownload = findPreference("redownload_data");
            redownload.setOnPreferenceClickListener(preference -> {
                Intent redownloadIntent = new Intent(getActivity(), RedownloadActivity.class);
                redownloadIntent.putExtra(LoadTBAData.DATA_TO_LOAD, new short[]{LoadTBAData.LOAD_EVENTS, LoadTBAData.LOAD_TEAMS, LoadTBAData.LOAD_DISTRICTS});
                startActivity(redownloadIntent);
                return true;
            });

            Preference updateStatus = findPreference("update_api_status");
            updateStatus.setOnPreferenceClickListener(preference -> {
                Intent serviceIntent = new Intent(getActivity(), StatusRefreshService.class);
                getActivity().startService(serviceIntent);
                Toast.makeText(getActivity(), R.string.update_status_launched, Toast.LENGTH_SHORT)
                  .show();
                return true;
            });

            Preference gcmRegister = findPreference("gcm_register");
            gcmRegister.setOnPreferenceClickListener((preference) -> {
                getActivity().startService(new Intent(getActivity(),
                                                      MyTbaRegistrationService.class));
                return false;
            });

            Preference updateMytba = findPreference("update_mytba");
            updateMytba.setOnPreferenceClickListener((preference) -> {
                getActivity().startService(
                        MyTbaUpdateService.newInstance(getActivity(), true, true));
                return false;
            });

            Preference migrateAuth = findPreference("migrate_auth");
            migrateAuth.setOnPreferenceClickListener((preference) -> {
                getActivity().startService(new Intent(getActivity(),
                                                      MigrateLegacyUserToFirebase.class));
                return false;
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
