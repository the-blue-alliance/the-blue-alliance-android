package com.thebluealliance.androidclient.mytba;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

public class MyTbaUpdateService extends IntentService {

    private static final String UPDATE_FAVORITES = "favorites";
    private static final String UPDATE_SUBSCRIPTIONS = "subscriptions";

    @Inject MyTbaDatafeed mDatafeed;

    public static Intent newInstance(Context context,
                                     boolean updateFavorites,
                                     boolean updateSubscriptions) {
        Intent intent = new Intent(context, MyTbaUpdateService.class);
        intent.putExtra(UPDATE_FAVORITES, updateFavorites);
        intent.putExtra(UPDATE_SUBSCRIPTIONS, updateSubscriptions);
        return intent;
    }

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
        boolean updateFavorites = intent.getBooleanExtra(UPDATE_FAVORITES, true);
        boolean updateSubscriptions = intent.getBooleanExtra(UPDATE_SUBSCRIPTIONS, true);
        if (updateFavorites) {
            mDatafeed.updateUserFavorites();
        }
        if (updateSubscriptions) {
            mDatafeed.updateUserSubscriptions();
        }
    }

    private MyTbaComponent getComponenet() {
        TBAAndroid application = ((TBAAndroid) getApplication());
        return DaggerMyTbaComponent.builder()
                                   .applicationComponent(application.getComponent())
                                   .gceModule(application.getGceModule())
                                   .authModule(application.getAuthModule())
                                   .build();
    }
}
