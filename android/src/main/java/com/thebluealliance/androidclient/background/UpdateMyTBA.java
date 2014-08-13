package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.thebluealliance.androidclient.accounts.AccountHelper;

/**
 * File created by phil on 8/13/14.
 */
public class UpdateMyTBA extends AsyncTask<Void, Void, Void> {

    private Context context;

    public UpdateMyTBA(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        
    }
}
