package com.thebluealliance.androidclient.accounts;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Subscription;

import java.io.IOException;

/**
 * File created by phil on 8/2/14.
 */
public class AddRemoveUserSubscription extends AsyncTask<String, Void, AddRemoveUserSubscription.Result> {

    protected enum Result {
        ADDED, REMOVED, ERROR
    }

    private Activity activity;
    private MenuItem icon;

    public AddRemoveUserSubscription(Activity activity, MenuItem icon) {
        this.activity = activity;
        this.icon = icon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        icon.setActionView(R.layout.actionbar_indeterminate_progress);
    }

    @Override
    protected Result doInBackground(String... params) {
        String modelKey = params[0];
        JsonArray notifications = new JsonArray();
        for( int i=1; i<params.length; i++){
            notifications.add(new JsonPrimitive(params[i]));
        }
        String user = AccountHelper.getSelectedAccount(activity);
        String key = MyTBAHelper.createKey(user, modelKey);
        Database.Subscriptions table = Database.getInstance(activity).getSubscriptionsTable();
        TbaMobile service = AccountHelper.getAuthedTbaMobile(activity);
        ModelsMobileApiMessagesSubscriptionMessage request = new ModelsMobileApiMessagesSubscriptionMessage();
        request.setModelKey(modelKey);
        request.setDeviceKey(GCMAuthHelper.getRegistrationId(activity));
        request.setSettings(notifications.toString());

        if(!table.exists(key)) {
            Log.d(Constants.LOG_TAG, "Subscription doesn't exist. Adding it");
            try {
                ModelsMobileApiMessagesBaseResponse response = service.subscriptions().add(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    table.add(new Subscription(user, modelKey));
                    return Result.ADDED;
                } else {
                    Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding subscription.\n" + response.getMessage());
                    return Result.ERROR;
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exception while adding subscription");
                e.printStackTrace();
                return Result.ERROR;
            }
        }else {
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
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        icon.setActionView(null);
        String text;
        if (result == Result.ADDED) {
            icon.setIcon(R.drawable.ic_action_remove_subscription);
            icon.setTitle(activity.getString(R.string.action_remove_subscription));
            text = "Subscription added";
        } else if (result == Result.REMOVED) {
            icon.setIcon(R.drawable.ic_action_add_subscription);
            icon.setTitle(activity.getString(R.string.action_add_subscription));
            text = "Subscription removed";
        } else{
            text = "Error adding subscription";
        }
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
}
