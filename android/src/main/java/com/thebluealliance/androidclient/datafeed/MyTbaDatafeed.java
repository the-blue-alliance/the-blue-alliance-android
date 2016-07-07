package com.thebluealliance.androidclient.datafeed;

import com.appspot.tbatv_prod_hrd.Favorites;
import com.appspot.tbatv_prod_hrd.Subscriptions;
import com.appspot.tbatv_prod_hrd.Tbamobile;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesFavoriteMessage;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesRegistrationRequest;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesSubscriptionCollection;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.datafeed.gce.GceAuthController;
import com.thebluealliance.androidclient.gcm.GcmController;
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

import retrofit2.Response;
import rx.Observable;

@Singleton
public class MyTbaDatafeed {
    private static final String LAST_FAVORITES_UPDATE = "last_mytba_favorites_update_%s";
    private static final String LAST_SUBSCRIPTIONS_UPDATE = "last_mytba_subscriptions_update_%s";

    private final Context mApplicationContext;
    private final Resources mRes;
    private final SharedPreferences mPrefs;
    private final Database mDb;
    private final Tbamobile mTbaMobile;
    private final Favorites mFavoriteApi;
    private final Subscriptions mSubscriptionApi;
    private final GceAuthController mAuthController;
    private final AccountController mAccountController;
    private final GcmController mGcmController;

    @Inject
    public MyTbaDatafeed(
            Context context,
            GceAuthController authController,
            GcmController gcmController,
            Tbamobile tbaMobile,
            Favorites favoriteApi,
            Subscriptions subscriptionApi,
            Resources res,
            SharedPreferences prefs,
            AccountController accountController,
            Database db) {
        mApplicationContext = context.getApplicationContext();
        mAuthController = authController;
        mGcmController = gcmController;
        mTbaMobile = tbaMobile;
        mFavoriteApi = favoriteApi;
        mSubscriptionApi = subscriptionApi;
        mRes = res;
        mPrefs = prefs;
        mDb = db;
        mAccountController = accountController;
    }

    public boolean register(String regId) {
        Log.d(Constants.LOG_TAG, "Registering for GCM");
        if (!ConnectionDetector.isConnectedToInternet(mApplicationContext)) {
            return false;
        }

        String authHeader = mAuthController.getAuthHeader();
        ModelsMobileApiMessagesRegistrationRequest request = new ModelsMobileApiMessagesRegistrationRequest();
        request.mobile_id = regId;
        request.operating_system = GcmController.OS_ANDROID;
        request.device_uuid = Utilities.getDeviceUUID(mApplicationContext);

        Response<ModelsMobileApiMessagesBaseResponse> response = null;
        try {
             response = mTbaMobile.register(authHeader, request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (response != null && (response.code() == 200 || response.code() == 304));
    }

    public boolean unregister() {
        Log.d(Constants.LOG_TAG, "Unregistering for GCM");
        if (!ConnectionDetector.isConnectedToInternet(mApplicationContext)) {
            return false;
        }

        String authHeader = mAuthController.getAuthHeader();
        ModelsMobileApiMessagesRegistrationRequest request = new ModelsMobileApiMessagesRegistrationRequest();
        request.mobile_id = mGcmController.getRegistrationId();
        request.operating_system = GcmController.OS_ANDROID;
        request.device_uuid = Utilities.getDeviceUUID(mApplicationContext);

        Response<ModelsMobileApiMessagesBaseResponse> response = null;
        try {
            response = mTbaMobile.unregister(authHeader, request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (response != null && (response.code() == 200 || response.code() == 304));
    }

    public void updateUserFavorites() {
        List<Favorite> favoriteModels = new ArrayList<>();
        String currentUser = mAccountController.getSelectedAccount();
        String prefString = String.format(LAST_FAVORITES_UPDATE, currentUser);

        Date now = new Date();
        Date futureTime = new Date(mPrefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
        if (now.before(futureTime)) {
            Log.d(Constants.LOG_TAG, "Not updating myTBA subscriptions. Too soon since last update");
            return;
        }
        if (!ConnectionDetector.isConnectedToInternet(mApplicationContext)) {
            return;
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA favorites");
        if (!mAccountController.isMyTbaEnabled()) {
            Log.e(Constants.LOG_TAG, "MyTBA is not enabled");
            Handler mainHandler = new Handler(mApplicationContext.getMainLooper());
            mainHandler.post(() -> Toast.makeText(mApplicationContext, mRes.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show());
            return;
        }

        String authHeader = mAuthController.getAuthHeader();
        Response<ModelsMobileApiMessagesFavoriteCollection> favoriteResponse;
        try {
            favoriteResponse = mFavoriteApi.list(authHeader).execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA favorites");
            e.printStackTrace();
            return;
        }

        if (favoriteResponse != null) {
            ModelsMobileApiMessagesFavoriteCollection favoriteCollection = favoriteResponse.body();
            if (favoriteCollection != null && favoriteCollection.favorites != null) {
                FavoritesTable favorites = mDb.getFavoritesTable();
                favorites.recreate(currentUser);
                for (int i = 0; i < favoriteCollection.favorites.size(); i++) {
                    ModelsMobileApiMessagesFavoriteMessage f = favoriteCollection.favorites.get(i);
                    favoriteModels.add(new Favorite(currentUser, f.model_key, f.model_type));
                }
                favorites.add(favoriteModels);
                Log.d(Constants.LOG_TAG, "Added " + favoriteModels.size() + " favorites");
            }
        }

        mPrefs.edit().putLong(prefString, now.getTime()).apply();
    }

    public void updateUserSubscriptions() {
        String currentUser = mAccountController.getSelectedAccount();
        String prefString = String.format(LAST_SUBSCRIPTIONS_UPDATE, currentUser);

        Date now = new Date();
        Date futureTime = new Date(mPrefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
        if (now.before(futureTime)) {
            Log.d(Constants.LOG_TAG, "Not updating myTBA subscriptions. Too soon since last update");
            return;
        }
        if (!ConnectionDetector.isConnectedToInternet(mApplicationContext)) {
            return;
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA subscriptions");
        if (!mAccountController.isMyTbaEnabled()) {
            Log.e(Constants.LOG_TAG, "MyTBA is not enabled");
            Handler mainHandler = new Handler(mApplicationContext.getMainLooper());
            mainHandler.post(() -> Toast.makeText(mApplicationContext, mRes.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show());
            return;
        }

        String authHeader = mAuthController.getAuthHeader();
        Response<ModelsMobileApiMessagesSubscriptionCollection> subscriptionResponse;
        try {
            subscriptionResponse = mSubscriptionApi.list(authHeader).execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA favorites");
            e.printStackTrace();
            return;
        }

        List<Subscription> subscriptionModels = new ArrayList<>();
        if (subscriptionResponse != null) {
            ModelsMobileApiMessagesSubscriptionCollection subscriptionCollection = subscriptionResponse.body();
            if (subscriptionCollection != null && subscriptionCollection.subscriptions != null) {
                SubscriptionsTable subscriptions = mDb.getSubscriptionsTable();
                subscriptions.recreate(currentUser);
                for (int i = 0; i < subscriptionCollection.subscriptions.size(); i++) {
                    ModelsMobileApiMessagesSubscriptionMessage s = subscriptionCollection.subscriptions.get(i);
                    subscriptionModels.add(new Subscription(currentUser, s.model_key, s.notifications, s.model_type));
                }
                subscriptions.add(subscriptionModels);
                Log.d(Constants.LOG_TAG, "Added " + subscriptionModels.size() + " subscriptions");
            }
        }

        mPrefs.edit().putLong(prefString, now.getTime()).apply();
    }


    public Observable<List<Subscription>> fetchLocalSubscriptions() {
        return Observable.create((observer) -> {
            try {
                String account = mAccountController.getSelectedAccount();
                List<Subscription> subscriptions = mDb.getSubscriptionsTable().getForUser(account);
                observer.onNext(subscriptions);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Favorite>> fetchLocalFavorites() {
        return Observable.create((observer) -> {
            try {
                String account = mAccountController.getSelectedAccount();
                List<Favorite> favorites = mDb.getFavoritesTable().getForUser(account);
                observer.onNext(favorites);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }
}
