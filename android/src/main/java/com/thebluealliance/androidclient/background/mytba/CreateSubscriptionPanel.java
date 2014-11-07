package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.ListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.fragments.mytba.NotificationSettingsFragment;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.NotificationTypeListElement;
import com.thebluealliance.androidclient.views.FloatingActionButton;

import java.util.ArrayList;

/**
 * File created by phil on 8/13/14.
 */
public class CreateSubscriptionPanel extends AsyncTask<String, Void, Void> {

    private Context context;
    private boolean favExists;
    private PreferenceFragment fragment;
    private ArrayList<Preference> notifications;
    private PreferenceCategory notificationSettingsCategory;

    public CreateSubscriptionPanel(Context context, NotificationSettingsFragment preferenceFragment) {
        this.context = context;
        this.fragment = preferenceFragment;
    }

    @Override
    protected Void doInBackground(String... params) {
        String modelKey = params[0];
        ModelHelper.MODELS type = ModelHelper.getModelFromKey(modelKey);

        Database.Favorites favTable = Database.getInstance(context).getFavoritesTable();
        Database.Subscriptions subTable = Database.getInstance(context).getSubscriptionsTable();

        String currentUser = AccountHelper.getSelectedAccount(context);
        String myKey = MyTBAHelper.createKey(currentUser, modelKey);

        favExists = favTable.exists(myKey);
        String currentSettings ="";
        if(subTable.exists(myKey)){
            currentSettings = subTable.get(myKey).getNotificationSettings();
        }

        String[] notificationTypes = ModelHelper.getNotificationTypes(type);
        notifications = new ArrayList<>();

        for(String notification: notificationTypes){
            boolean enabled = currentSettings.contains(notification);
            CheckBoxPreference preference = new CheckBoxPreference(context);
            preference.setTitle(NotificationTypes.getDisplayName(notification));
            preference.setKey(notification);
            preference.setChecked(enabled);
            // Don't store this in shared prefs
            preference.setPersistent(false);
            notifications.add(preference);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        PreferenceScreen preferenceScreen = fragment.getPreferenceScreen();
        CheckBoxPreference favorite = new CheckBoxPreference(context);
        favorite.setTitle("Favorite");
        favorite.setSummary("You can save teams, events, and more for easy access by marking them as \"favorites\".");
        favorite.setChecked(favExists);
        favorite.setPersistent(false);
        preferenceScreen.addPreference(favorite);

        // Only show the notification section if there is at least one enabled notification
        if(!notifications.isEmpty()) {
            notificationSettingsCategory = new PreferenceCategory(context);
            notificationSettingsCategory.setTitle("Notification settings");
            preferenceScreen.addPreference(notificationSettingsCategory);

            Preference summary = new Preference(context);
            summary.setSummary("Subscribing to something lets you get a push notification whenever there is an update.");
            summary.setSelectable(false);
            notificationSettingsCategory.addPreference(summary);
            for (Preference p : notifications) {
                notificationSettingsCategory.addPreference(p);
            }
        }
    }
}
