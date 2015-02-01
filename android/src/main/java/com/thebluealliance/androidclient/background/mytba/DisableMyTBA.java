package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesRegistrationRequest;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;

import java.io.IOException;

/**
 * File created by phil on 8/29/14.
 */
public class DisableMyTBA extends AsyncTask<String, Void, Void> {

    private Context context;

    public DisableMyTBA(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String user = params[0];

        Database.Favorites favorites = Database.getInstance(context).getFavoritesTable();
        favorites.recreate(user);

        Database.Subscriptions subscriptions = Database.getInstance(context).getSubscriptionsTable();
        subscriptions.recreate(user);

        ModelsMobileApiMessagesRegistrationRequest request = new ModelsMobileApiMessagesRegistrationRequest();
        request.setMobileId(GCMAuthHelper.getRegistrationId(context));
        request.setOperatingSystem(GCMAuthHelper.OS_ANDROID);

        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        if(service == null){
            Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
            return null;
        }
        try {
            ModelsMobileApiMessagesBaseResponse response = service.unregister(request).execute();
            if(response.getCode() == 200){
                Log.i(Constants.LOG_TAG, "Unregistered from GCM");
            }else{
                Log.e(Constants.LOG_TAG, "error unregistering from gcm: "+response.getMessage());
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "unable to unregister");
            e.printStackTrace();
        }

        //remove selected account
        AccountHelper.setSelectedAccount(context, "");
        GCMAuthHelper.storeRegistrationId(context, "");

        return null;
    }
}
