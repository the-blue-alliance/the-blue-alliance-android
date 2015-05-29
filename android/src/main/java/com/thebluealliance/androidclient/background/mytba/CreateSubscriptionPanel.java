package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.fragments.mytba.NotificationSettingsFragment;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;

/**
 * File created by phil on 8/13/14.
 */
public class CreateSubscriptionPanel extends AsyncTask<String, Void, Void> {

    private Context context;
    private boolean favExists;
    private NotificationSettingsFragment fragment;
    private Bundle savedState;
    private ModelHelper.MODELS type;
    private String currentSettings;

    public CreateSubscriptionPanel(Context context, NotificationSettingsFragment preferenceFragment, Bundle savedState, ModelHelper.MODELS type) {
        this.context = context;
        this.fragment = preferenceFragment;
        this.savedState = savedState;
        this.type = type;
    }

    @Override
    protected Void doInBackground(String... params) {
        String modelKey = params[0];

        Database.Favorites favTable = Database.getInstance(context).getFavoritesTable();
        Database.Subscriptions subTable = Database.getInstance(context).getSubscriptionsTable();

        String currentUser = AccountHelper.getSelectedAccount(context);
        String myKey = MyTBAHelper.createKey(currentUser, modelKey);

        favExists = favTable.exists(myKey);
        currentSettings = "";
        if (subTable.exists(myKey)) {
            currentSettings = subTable.get(myKey).getNotificationSettings();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (fragment == null || context == null || fragment.getActivity() == null) {
            // Uh oh, stuff was destroyed while we were working.
            return;
        }
        PreferenceScreen preferenceScreen = fragment.getPreferenceScreen();
        preferenceScreen.removeAll();
        CheckBoxPreference favorite = new CheckBoxPreference(context);
        favorite.setTitle(context.getString(R.string.mytba_favorite_title));
        favorite.setSummary(context.getString(R.string.mytba_favorite_summary));
        favorite.setKey(MyTBAHelper.getFavoritePreferenceKey());
        if (savedState != null) {
            if (savedState.containsKey(MyTBAHelper.getFavoritePreferenceKey())) {
                favorite.setChecked(savedState.getBoolean(MyTBAHelper.getFavoritePreferenceKey()));
            } else {
                favorite.setChecked(false);
            }
        } else {
            favorite.setChecked(favExists);
        }
        favorite.setPersistent(false);
        preferenceScreen.addPreference(favorite);

        // Only show the notification section if there is at least one enabled notification
        String[] notificationTypes = ModelHelper.getNotificationTypes(type);
        if (notificationTypes.length > 0) {
            PreferenceCategory notificationSettingsCategory = new PreferenceCategory(context);
            notificationSettingsCategory.setTitle(context.getString(R.string.mytba_subscription_header));
            preferenceScreen.addPreference(notificationSettingsCategory);

            Preference summary = new Preference(context);
            summary.setSummary(context.getString(R.string.mytba_subscriptions_summary));
            summary.setSelectable(false);
            notificationSettingsCategory.addPreference(summary);
            for (String notificationKey : notificationTypes) {
                boolean enabled;
                if (savedState != null) {
                    if (savedState.containsKey(notificationKey)) {
                        enabled = savedState.getBoolean(notificationKey);
                    } else {
                        enabled = false;
                    }
                } else {
                    enabled = currentSettings.contains(notificationKey);
                }
                CheckBoxPreference preference = new CheckBoxPreference(context);
                preference.setTitle(NotificationTypes.getDisplayName(notificationKey));
                preference.setKey(notificationKey);
                preference.setChecked(enabled);
                // Don't store this in shared prefs
                preference.setPersistent(false);
                notificationSettingsCategory.addPreference(preference);
            }

            Preference buffer = new Preference(context);
            buffer.setLayoutResource(R.layout.buffer_preference);
            notificationSettingsCategory.addPreference(buffer);
        }
        fragment.setPreferencesLoaded();

        // Tell the fragment about its initial state. That way if the user checks some boxes and then unchecks them,
        // they can be restored to their proper initial state.
        Bundle initialStateBundle = new Bundle();
        for (String notificationKey : notificationTypes) {
            initialStateBundle.putBoolean(notificationKey, currentSettings.contains(notificationKey));
        }
        initialStateBundle.putBoolean(MyTBAHelper.getFavoritePreferenceKey(), favorite.isChecked());
        fragment.setInitialStateBundle(initialStateBundle);
    }
}
