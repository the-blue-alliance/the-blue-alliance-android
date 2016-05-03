package com.thebluealliance.androidclient.background.mytba;

import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;

import android.os.AsyncTask;

public class DisableMyTBA extends AsyncTask<Void, Void, Void> {

    private final MyTbaDatafeed mDatafeed;

    public DisableMyTBA(MyTbaDatafeed datafeed) {
        mDatafeed = datafeed;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mDatafeed.unregister();
        return null;
    }
}
