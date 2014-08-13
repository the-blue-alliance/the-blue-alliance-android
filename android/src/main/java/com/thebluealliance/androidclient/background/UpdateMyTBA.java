package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesSubscriptionCollection;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * File created by phil on 8/13/14.
 */
public class UpdateMyTBA extends AsyncTask<Void, Void, Void> {

    private static final String LAST_MY_TBA_UPDATE = "last_mytba_update_%s";

    private Context context;

    public UpdateMyTBA(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String currentUser = AccountHelper.getSelectedAccount(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefString = String.format(LAST_MY_TBA_UPDATE, currentUser);

        Date now = new Date();
        Date futureTime = new Date(prefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
        // TODO this endpoint needs some caching so we keep load off the server
        if (now.before(futureTime)) {
            //don't hit the API too often.
            Log.d(Constants.LOG_TAG, "Not updating myTBA. Too soon since last update");
            return null;
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA data");
        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        ModelsMobileApiMessagesFavoriteCollection favoriteCollection = null;
        ModelsMobileApiMessagesSubscriptionCollection subscriptionCollection = null;
        try {
            favoriteCollection = service.favorites().list().execute();
            subscriptionCollection = service.subscriptions().list().execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA");
            e.printStackTrace();
            return null;
        }

        Database.Favorites favorites = Database.getInstance(context).getFavoritesTable();
        favorites.recreate(currentUser);
        if(favoriteCollection.getFavorites() != null){
            ArrayList<Favorite> favoriteModels = new ArrayList<>();
            for(ModelsMobileApiMessagesFavoriteMessage f: favoriteCollection.getFavorites()){
                favoriteModels.add(new Favorite(currentUser, f.getModelKey()));
            }
            favorites.add(favoriteModels);
        }

        Database.Subscriptions subscriptions = Database.getInstance(context).getSubscriptionsTable();
        subscriptions.recreate(currentUser);
        if(subscriptionCollection.getSubscriptions() != null){
            ArrayList<Subscription> subscriptionModels = new ArrayList<>();
            for(ModelsMobileApiMessagesSubscriptionMessage s: subscriptionCollection.getSubscriptions()){
                subscriptionModels.add(new Subscription(currentUser, s.getModelKey()));
            }
            subscriptions.add(subscriptionModels);
        }

        prefs.edit().putLong(prefString, new Date().getTime()).apply();
        return null;
    }
}
