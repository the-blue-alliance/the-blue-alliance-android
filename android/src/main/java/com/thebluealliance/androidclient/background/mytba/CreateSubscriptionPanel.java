package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
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
    private FloatingActionButton icon;
    private ListView listView;
    private ArrayList<ListItem> notifications;

    public CreateSubscriptionPanel(Context context, FloatingActionButton icon, ListView listView) {
        this.context = context;
        this.icon = icon;
        this.listView = listView;
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
        int pos = 0;
        for(String notification: notificationTypes){
            boolean enabled = currentSettings.contains(notification);
            notifications.add(new NotificationTypeListElement(NotificationTypes.getDisplayName(notification), enabled, pos));
            listView.setItemChecked(pos, enabled);
            pos++;
        }
        Log.d(Constants.LOG_TAG, "Started with checked: "+listView.getCheckedItemPositions());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(favExists){
            icon.setImageResource(R.drawable.ic_my_tba_blue);
        } else{
            icon.setImageResource(R.drawable.ic_action_add_favorite);
        }

        ListViewAdapter adapter = new ListViewAdapter(context, notifications);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
    }
}
