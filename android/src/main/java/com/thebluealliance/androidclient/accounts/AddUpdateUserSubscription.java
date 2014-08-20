package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Subscription;

import java.io.IOException;

/**
 * File created by phil on 8/2/14.
 */
public class AddUpdateUserSubscription extends AsyncTask<String, Void, AddUpdateUserSubscription.Result> {

    protected enum Result {
        ADDED, NOTUPDATED, ERROR
    }

    private Context context;
    private boolean added;

    public AddUpdateUserSubscription(Context context) {
        this.context = context;
    }

    @Override
    protected Result doInBackground(String... params) {
        String modelKey = params[0];
        JsonArray notifications = new JsonArray();
        for (int i = 1; i < params.length; i++) {
            notifications.add(new JsonPrimitive(params[i]));
        }
        String user = AccountHelper.getSelectedAccount(context);
        String key = MyTBAHelper.createKey(user, modelKey);
        Database.Subscriptions table = Database.getInstance(context).getSubscriptionsTable();
        added = !table.exists(key);
        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        ModelsMobileApiMessagesSubscriptionMessage request = new ModelsMobileApiMessagesSubscriptionMessage();
        request.setModelKey(modelKey);
        request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
        request.setSettings(notifications.toString());

        Log.d(Constants.LOG_TAG, "Adding subscription");
        try {
            ModelsMobileApiMessagesBaseResponse response = service.subscriptions().add(request).execute();
            if (response.getCode() == 200) {
                table.add(new Subscription(user, modelKey, notifications.toString()));
                return Result.ADDED;
            }else if(response.getCode() == 304) {
                Log.d(Constants.LOG_TAG, "Subscription not modified");
                return Result.NOTUPDATED;
            }else{
                Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding subscription.\n" + response.getMessage());
                return Result.ERROR;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while adding subscription");
            e.printStackTrace();
            return Result.ERROR;
        }

    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        String text = null;
        if (result == Result.ADDED) {
            text = "Subscription "+(added?"added":"updated");
        } else if(result == Result.ERROR){
            text = "Error adding subscription";
        }
        if(text != null) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }
}
