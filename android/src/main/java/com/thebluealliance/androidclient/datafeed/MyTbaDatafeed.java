package com.thebluealliance.androidclient.datafeed;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesSubscriptionCollection;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Subscription;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyTbaDatafeed {
    public static final String LAST_FAVORITES_UPDATE = "last_mytba_favorites_update_%s";
    public static final String LAST_SUBSCRIPTIONS_UPDATE = "last_mytba_subscriptions_update_%s";

    private final Context mApplicationContext;
    private final Resources mRes;
    private final SharedPreferences mPrefs;
    private final Database mDb;

    @Inject
    public MyTbaDatafeed(Context context, Resources res, SharedPreferences prefs, Database db) {
        mApplicationContext = context.getApplicationContext();
        mRes = res;
        mPrefs = prefs;
        mDb = db;
    }

    public void updateUserFavorites() {
        List<Favorite> favoriteModels = new ArrayList<>();
        String currentUser = AccountHelper.getSelectedAccount(mApplicationContext);
        String prefString = String.format(LAST_FAVORITES_UPDATE, currentUser);

        Date now = new Date();
        Date futureTime = new Date(mPrefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
        // TODO this endpoint needs some caching so we keep load off the server
        if (now.before(futureTime)) {
            Log.d(Constants.LOG_TAG, "Not updating myTBA subscriptions. Too soon since last update");
            return;
        }
        if (!ConnectionDetector.isConnectedToInternet(mApplicationContext)) {
            return;
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA favorites");
        TbaMobile service = AccountHelper.getAuthedTbaMobile(mApplicationContext);
        if (service == null) {
            Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
            Handler mainHandler = new Handler(mApplicationContext.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mApplicationContext, mRes.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        ModelsMobileApiMessagesFavoriteCollection favoriteCollection;
        try {
            favoriteCollection = service.favorites().list().execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA favorites");
            e.printStackTrace();
            return;
        }

        FavoritesTable favorites = mDb.getFavoritesTable();
        favorites.recreate(currentUser);
        if (favoriteCollection.getFavorites() != null) {
            for (ModelsMobileApiMessagesFavoriteMessage f : favoriteCollection.getFavorites()) {
                favoriteModels.add(new Favorite(currentUser, f.getModelKey(), f.getModelType().intValue()));
            }
            favorites.add(favoriteModels);
            Log.d(Constants.LOG_TAG, "Added " + favoriteModels.size() + " favorites");
        }

        mPrefs.edit().putLong(prefString, new Date().getTime()).apply();
    }

    public void updateUserSubscriptions() {
        String currentUser = AccountHelper.getSelectedAccount(mApplicationContext);
        String prefString = String.format(LAST_SUBSCRIPTIONS_UPDATE, currentUser);

        List<Subscription> subscriptionModels = new ArrayList<>();
        Date now = new Date();
        Date futureTime = new Date(mPrefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
        // TODO this endpoint needs some caching so we keep load off the server
        if (now.before(futureTime)) {
            //don't hit the API too often.
            Log.d(Constants.LOG_TAG, "Not updating myTBA subscriptions. Too soon since last update");
            return;
        }

        if (!ConnectionDetector.isConnectedToInternet(mApplicationContext)) {
            return;
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA subscriptions");
        TbaMobile service = AccountHelper.getAuthedTbaMobile(mApplicationContext);
        if (service == null) {
            Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
            Handler mainHandler = new Handler(mApplicationContext.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mApplicationContext, mRes.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        ModelsMobileApiMessagesSubscriptionCollection subscriptionCollection;
        try {
            subscriptionCollection = service.subscriptions().list().execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA subscriptions");
            e.printStackTrace();
            return;
        }

        SubscriptionsTable subscriptions = mDb.getSubscriptionsTable();
        subscriptions.recreate(currentUser);
        if (subscriptionCollection.getSubscriptions() != null) {
            for (ModelsMobileApiMessagesSubscriptionMessage s : subscriptionCollection.getSubscriptions()) {
                subscriptionModels.add(new Subscription(currentUser, s.getModelKey(), s.getNotifications(), s.getModelType().intValue()));
            }
            subscriptions.add(subscriptionModels);
        }

        Log.d(Constants.LOG_TAG, "Added " + subscriptionCollection.size() + " subscriptions");
        mPrefs.edit().putLong(prefString, new Date().getTime()).apply();
    }
}
