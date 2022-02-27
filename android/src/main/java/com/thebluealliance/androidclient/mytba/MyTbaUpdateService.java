package com.thebluealliance.androidclient.mytba;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
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
}
