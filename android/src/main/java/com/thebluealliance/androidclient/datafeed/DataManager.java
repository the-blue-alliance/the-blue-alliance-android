package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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
import com.thebluealliance.androidclient.models.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nathan on 4/30/2014.
 */
public class DataManager {

    public static class NoDataException extends Exception {
        public NoDataException(String message) {
            super(message);
        }

        public NoDataException(String message, Throwable t) {
            super(message, t);
        }
    }

    public static class Teams {
        public static final String ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE = "all_teams_loaded_for_page_";

        public static Team getTeamFromDB(Context c, String teamKey) {
            synchronized (Database.getInstance(c)) {
                return Database.getInstance(c).getTeamsTable().get(teamKey);
            }
        }
    }

    public static class Events {
        public static final String ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";
    }

    public static class Districts {

        public static final String ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR = "all_districts_loaded_for_year_";
    }

    public static class MyTBA {

        /**
         * These methods will fetch the current user's myTBA data from the web and store it in the
         * local db They also return an ArrayList of the favorite/subscription models and a
         * convienence
         */

        public static final String LAST_FAVORITES_UPDATE = "last_mytba_favorites_update_%s";
        public static final String LAST_SUBSCRIPTIONS_UPDATE = "last_mytba_subscriptions_update_%s";

        public static APIResponse<ArrayList<Favorite>> updateUserFavorites(final Context context, RequestParams requestParams) {
            String currentUser = AccountHelper.getSelectedAccount(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String prefString = String.format(LAST_FAVORITES_UPDATE, currentUser);

            ArrayList<Favorite> favoriteModels = new ArrayList<>();
            Date now = new Date();
            Date futureTime = new Date(prefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
            // TODO this endpoint needs some caching so we keep load off the server
            if (!requestParams.forceFromWeb && now.before(futureTime)) {
                //don't hit the API too often.
                Log.d(Constants.LOG_TAG, "Not updating myTBA favorites. Too soon since last update");
                return new APIResponse<>(null, APIResponse.CODE.CACHED304);
            }

            if (!ConnectionDetector.isConnectedToInternet(context)) {
                return new APIResponse<>(null, APIResponse.CODE.OFFLINECACHE);
            }

            Log.d(Constants.LOG_TAG, "Updating myTBA favorites");
            TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
            if (service == null) {
                Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show();
                    }
                });
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }
            ModelsMobileApiMessagesFavoriteCollection favoriteCollection;
            try {
                favoriteCollection = service.favorites().list().execute();
            } catch (IOException e) {
                Log.w(Constants.LOG_TAG, "Unable to update myTBA favorites");
                e.printStackTrace();
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }

            FavoritesTable favorites = Database.getInstance(context).getFavoritesTable();
            favorites.recreate(currentUser);
            if (favoriteCollection.getFavorites() != null) {
                for (ModelsMobileApiMessagesFavoriteMessage f : favoriteCollection.getFavorites()) {
                    favoriteModels.add(new Favorite(currentUser, f.getModelKey(), f.getModelType().intValue()));
                }
                favorites.add(favoriteModels);
                Log.d(Constants.LOG_TAG, "Added " + favoriteModels.size() + " favorites");
            }

            prefs.edit().putLong(prefString, new Date().getTime()).apply();
            return new APIResponse<>(favoriteModels, APIResponse.CODE.WEBLOAD);
        }

        public static APIResponse<ArrayList<Subscription>> updateUserSubscriptions(final Context context, RequestParams requestParams) {
            String currentUser = AccountHelper.getSelectedAccount(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String prefString = String.format(LAST_SUBSCRIPTIONS_UPDATE, currentUser);

            ArrayList<Subscription> subscriptionModels = new ArrayList<>();
            Date now = new Date();
            Date futureTime = new Date(prefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
            // TODO this endpoint needs some caching so we keep load off the server
            if (!requestParams.forceFromWeb && now.before(futureTime)) {
                //don't hit the API too often.
                Log.d(Constants.LOG_TAG, "Not updating myTBA subscriptions. Too soon since last update");
                return new APIResponse<>(null, APIResponse.CODE.CACHED304);
            }

            if (!ConnectionDetector.isConnectedToInternet(context)) {
                return new APIResponse<>(null, APIResponse.CODE.OFFLINECACHE);
            }

            Log.d(Constants.LOG_TAG, "Updating myTBA subscriptions");
            TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
            if (service == null) {
                Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show();
                    }
                });
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }
            ModelsMobileApiMessagesSubscriptionCollection subscriptionCollection;
            try {
                subscriptionCollection = service.subscriptions().list().execute();
            } catch (IOException e) {
                Log.w(Constants.LOG_TAG, "Unable to update myTBA subscriptions");
                e.printStackTrace();
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }

            SubscriptionsTable subscriptions = Database.getInstance(context).getSubscriptionsTable();
            subscriptions.recreate(currentUser);
            if (subscriptionCollection.getSubscriptions() != null) {
                for (ModelsMobileApiMessagesSubscriptionMessage s : subscriptionCollection.getSubscriptions()) {
                    subscriptionModels.add(new Subscription(currentUser, s.getModelKey(), s.getNotifications(), s.getModelType().intValue()));
                }
                subscriptions.add(subscriptionModels);
            }

            Log.d(Constants.LOG_TAG, "Added " + subscriptionCollection.size() + " subscriptions");
            prefs.edit().putLong(prefString, new Date().getTime()).apply();
            return new APIResponse<>(subscriptionModels, APIResponse.CODE.WEBLOAD);
        }
    }
}