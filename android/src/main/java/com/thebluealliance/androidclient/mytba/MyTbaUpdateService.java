package com.thebluealliance.androidclient.mytba;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tbatv_prod_hrd.Favorites;
import com.appspot.tbatv_prod_hrd.Subscriptions;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesFavoriteMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.database.writers.FavoriteCollectionWriter;
import com.thebluealliance.androidclient.datafeed.gce.GceAuthController;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;
import retrofit2.Response;
import rx.schedulers.Schedulers;

public class MyTbaUpdateService extends IntentService {

    @Inject Database mDb;
    @Inject GceAuthController mGceAuthController;
    @Inject Favorites mFavoritesApi;
    @Inject Subscriptions mSubscriptionsApi;
    @Inject FavoriteCollectionWriter mFavoriteWriter;

    public MyTbaUpdateService() {
        super("Update myTBA");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
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

        String authHeader = mGceAuthController.getAuthHeader();
        if (authHeader == null) {
            Log.e(Constants.LOG_TAG, "Couldn't get GCE Auth Info");
            Handler mainHandler = new Handler(this.getMainLooper());
            mainHandler.post(() -> Toast.makeText(
                    MyTbaUpdateService.this,
                    getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show());
            return;
        }


        mFavoritesApi.list(authHeader).enqueue(mFavoriteWriter);
    }

    private MyTbaComponent getComponenet() {
        TBAAndroid application = ((TBAAndroid) getApplication());
        return DaggerMyTbaComponent.builder()
          .applicationComponent(application.getComponent())
          .gceModule(application.getGceModule())
                .datafeedModule(application.getDatafeedModule())
          .build();
    }
}
