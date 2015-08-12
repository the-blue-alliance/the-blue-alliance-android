package com.thebluealliance.androidclient.mytba;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesSubscriptionCollection;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class MyTbaUpdateService extends IntentService {

    @Inject Database mDb;

    public MyTbaUpdateService() {
        super("Update myTBA");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDb = ((TBAAndroid) getApplication()).getModule().provideDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Schedulers.io().createWorker().schedule(this::loadMyTbaData);
    }

    private void loadMyTbaData() {
        String currentUser = AccountHelper.getSelectedAccount(this);

        List<Favorite> favoriteModels = new ArrayList<>();
        List<Subscription> subscriptionModels = new ArrayList<>();
        // TODO this endpoint needs some caching so we keep load off the server
        if (!ConnectionDetector.isConnectedToInternet(this)) {
            return;
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA favorites");
        TbaMobile service = AccountHelper.getAuthedTbaMobile(this);
        if (service == null) {
            Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
            Handler mainHandler = new Handler(this.getMainLooper());
            mainHandler.post(() -> Toast.makeText(
              MyTbaUpdateService.this,
              getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show());
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
        if (favoriteCollection == null || favoriteCollection.getFavorites() == null) {
            Log.w(Constants.LOG_TAG, "Favorite collection is null");
            return;
        }

        FavoritesTable favorites = mDb.getFavoritesTable();
        favorites.recreate(currentUser);
        for (int i = 0; i < favoriteCollection.size(); i++) {
            ModelsMobileApiMessagesFavoriteMessage f = favoriteCollection.getFavorites().get(i);
            favoriteModels.add(
              new Favorite(currentUser, f.getModelKey(), f.getModelType().intValue()));
        }
        favorites.add(favoriteModels);
        Log.d(Constants.LOG_TAG, "Added " + favoriteModels.size() + " favorites");

        ModelsMobileApiMessagesSubscriptionCollection subscriptionCollection;
        try {
            subscriptionCollection = service.subscriptions().list().execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA subscriptions");
            e.printStackTrace();
            return;
        }
        if (subscriptionCollection == null || subscriptionCollection.getSubscriptions() == null) {
            Log.w(Constants.LOG_TAG, "Subscription collection is null");
            return;
        }

        SubscriptionsTable subscriptions = mDb.getSubscriptionsTable();
        subscriptions.recreate(currentUser);
        for (ModelsMobileApiMessagesSubscriptionMessage s : subscriptionCollection.getSubscriptions()) {
            subscriptionModels.add(
              new Subscription(
                currentUser,
                s.getModelKey(),
                s.getNotifications(),
                s.getModelType().intValue()));
        }
        subscriptions.add(subscriptionModels);
        Log.d(Constants.LOG_TAG, "Added " + subscriptionModels.size() + " subscriptions");
    }
}
