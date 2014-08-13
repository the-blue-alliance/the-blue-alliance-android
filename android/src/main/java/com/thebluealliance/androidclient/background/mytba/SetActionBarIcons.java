package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;

/**
 * File created by phil on 8/13/14.
 */
public class SetActionBarIcons extends AsyncTask<String, Void, Void> {

    private Context context;
    private boolean favExists, subExists;
    private MenuItem favorites, subscriptions;

    public SetActionBarIcons(Context context, MenuItem favorites, MenuItem subscriptions) {
        this.context = context;
        this.favorites = favorites;
        this.subscriptions = subscriptions;
    }

    @Override
    protected Void doInBackground(String... params) {
        String modelKey = params[0];

        Database.Favorites favTable = Database.getInstance(context).getFavoritesTable();
        Database.Subscriptions subTable = Database.getInstance(context).getSubscriptionsTable();

        String currentUser = AccountHelper.getSelectedAccount(context);
        String myKey = MyTBAHelper.createKey(currentUser, modelKey);

        favExists = favTable.exists(myKey);
        subExists = subTable.exists(myKey);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(favExists){
            favorites.setIcon(R.drawable.ic_action_remove_favorite);
            favorites.setTitle(context.getString(R.string.action_remove_favorite));
        } else{
            favorites.setIcon(R.drawable.ic_action_add_favorite);
            favorites.setTitle(context.getString(R.string.action_add_favorite));
        }

        if(subExists){
            subscriptions.setIcon(R.drawable.ic_action_remove_subscription);
            subscriptions.setTitle(context.getString(R.string.action_remove_subscription));
        } else{
            subscriptions.setIcon(R.drawable.ic_action_add_subscription);
            subscriptions.setTitle(context.getString(R.string.action_add_subscription));
        }
    }
}
