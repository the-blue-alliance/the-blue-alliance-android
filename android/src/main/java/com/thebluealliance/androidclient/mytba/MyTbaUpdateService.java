package com.thebluealliance.androidclient.mytba;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class MyTbaUpdateService extends IntentService {

    @Inject MyTbaDatafeed mDatafeed;

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
        mDatafeed.updateUserFavorites();
        mDatafeed.updateUserSubscriptions();
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
