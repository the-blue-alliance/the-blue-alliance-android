package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;

import java.io.IOException;

/**
 * File created by phil on 8/18/14.
 */
public class RemoveUserSubscription extends AsyncTask<String, Void, RemoveUserSubscription.Result> {

    protected enum Result {
        NOP, REMOVED, ERROR
    }

    private Context context;

    public RemoveUserSubscription(Context context) {
        this.context = context;
    }

    @Override
    protected RemoveUserSubscription.Result doInBackground(String... params) {
        String modelKey = params[0];
        String user = AccountHelper.getSelectedAccount(context);
        String key = MyTBAHelper.createKey(user, modelKey);
        Database.Subscriptions table = Database.getInstance(context).getSubscriptionsTable();
        if(table.exists(key)) {
            TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
            ModelsMobileApiMessagesSubscriptionMessage request = new ModelsMobileApiMessagesSubscriptionMessage();
            request.setModelKey(modelKey);
            request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
            request.setSettings("");
            Log.d(Constants.LOG_TAG, "Subscription already exists. Removing it");
            try {
                ModelsMobileApiMessagesBaseResponse response = service.subscriptions().remove(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    table.remove(key);
                    return Result.REMOVED;
                } else {
                    Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding subscription.\n" + response.getMessage());
                    return Result.ERROR;
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exception while adding subscription");
                e.printStackTrace();
                return Result.ERROR;
            }
        }else{
            return Result.NOP;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        String text = null;
        if (result == Result.REMOVED) {
            text = "Subscription removed";
        } else if(result == Result.ERROR){
            text = "Error removing subscription";
        }
        if(text != null) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }
}
