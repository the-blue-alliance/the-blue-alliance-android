package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.views.FloatingActionButton;

/**
 * File created by phil on 8/13/14.
 */
public class UpdateFABIcon extends AsyncTask<String, Void, Void> {

    private Context context;
    private boolean favExists, subExists;
    private FloatingActionButton icon;

    public UpdateFABIcon(Context context, FloatingActionButton icon) {
        this.context = context;
        this.icon = icon;
    }

    @Override
    protected Void doInBackground(String... params) {
        String modelKey = params[0];

        Database.Favorites favTable = Database.getInstance(context).getFavoritesTable();

        String currentUser = AccountHelper.getSelectedAccount(context);
        String myKey = MyTBAHelper.createKey(currentUser, modelKey);

        favExists = favTable.exists(myKey);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(favExists){
            icon.setImageResource(R.drawable.ic_action_remove_favorite);
        } else{
            icon.setImageResource(R.drawable.ic_action_add_favorite);
        }
    }
}
