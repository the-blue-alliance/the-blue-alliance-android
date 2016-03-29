package com.thebluealliance.androidclient.activities.settings;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RedownloadActivity;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.status.StatusRefreshService;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.dev_preferences);

            Preference analytics_dryRyn = findPreference("analytics_dry_run");
            analytics_dryRyn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Analytics.setAnalyticsDryRun(getActivity(), (boolean) newValue);
                    return true;
                }
            });

            Preference addMyTBAItem = findPreference("add_mytba_item");
            addMyTBAItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Favorite fav = new Favorite();
                    fav.setUserName(AccountHelper.getSelectedAccount(getActivity()));
                    fav.setModelKey("frc111");
                    fav.setModelEnum(ModelType.TEAM.getEnum());
                    Database.getInstance(getActivity()).getFavoritesTable().add(fav);
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

            Preference testUpcomingMatchNotification = findPreference("test_upcoming_match_notification");
            /*testUpcomingMatchNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String data = "{\"match_key\":\"2014ilch_f1m2\",\"event_name\":\"Midwest Regional\",\"team_keys\":[\"frc111\",\"frc118\",\"frc254\",\"frc496\",\"frc1114\",\"frc2056\"],\"scheduled_time\":12345,\"predicted_time\":123456}";
                    GCMMessageHandler.handleMessage(getActivity(), "upcoming_match", data);
                    return true;
                }
            });*/


            Preference testScoreNotification = findPreference("test_score_notification");
            /*testScoreNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Comment out different strings to see different types of notifications.
                    // This is kind of a hacky way of demonstrating notifications; it just removes
                    // teams from the alliance json objects to simulate different numbers of favorited teams.
                    // This will break once we actually check if teams are favorited while constructing notifications.

                    // All teams are favorited
                    //String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\",\"frc1102\"]},\"red\":{\"score\":0,\"teams\":[\"frc1398\",\"frc343\",\"frc1261\"]}},\"event_key\":\"2010sc\"}}";
                    // 2 teams on winning alliance are favorited
                    //String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\"]},\"red\":{\"score\":0,\"teams\":[]}},\"event_key\":\"2010sc\"}}";
                    // 1 team on losing alliance is favorited
                    //String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[]},\"red\":{\"score\":0,\"teams\":[\"frc1398\"]}},\"event_key\":\"2010sc\"}}";
                    // 2 teams on tied alliance are favorited
                    // String data = "{\"event_name\":\"EVENT NAME\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010sc_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\"]},\"red\":{\"score\":5,\"teams\":[]}},\"event_key\":\"2010sc\"}}";
                    // 2 teams on each tied alliance are favorited
                    String data = "{\"event_name\":\"San Diego Regional\",\"match\":{\"comp_level\":\"f\",\"match_number\":1,\"videos\":[],\"time_string\":null,\"set_number\":1,\"key\":\"2010casd_f1m1\",\"time\":null,\"alliances\":{\"blue\":{\"score\":5,\"teams\":[\"frc1772\",\"frc2751\"]},\"red\":{\"score\":5,\"teams\":[\"frc1398\",\"frc343\"]}},\"event_key\":\"2010casd\"}}";
                    GCMMessageHandler.handleMessage(getActivity(), "score", data);
                    return true;
                }
            });*/

            Preference gcmRegister = findPreference("select_account");
            gcmRegister.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    GoogleAccountCredential credential = AccountHelper.getSelectedAccountCredential(getActivity());
                    getActivity().startActivity(credential.newChooseAccountIntent());
                    return false;
                }
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
