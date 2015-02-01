package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesModelPreferenceMessage;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.helpers.ModelNotificationFavoriteSettings;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Subscription;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File created by phil on 8/2/14.
 */
public class UpdateUserModelSettings extends AsyncTask<String, Void, UpdateUserModelSettings.Result> {

    protected enum Result {
        SUCCESS, NOOP, ERROR
    }

    private Context context;
    private ModelNotificationFavoriteSettings settings;

    // We use a WeakReference so that the Activity can be garbage-collected if need be.
    private WeakReference<ModelSettingsCallbacks> callbacks;

    public UpdateUserModelSettings(Context context, ModelNotificationFavoriteSettings settings) {
        this.context = context;
        this.settings = settings;
    }

    public void setCallbacks(ModelSettingsCallbacks callbacks) {
        this.callbacks = new WeakReference<>(callbacks);
    }

    @Override
    protected Result doInBackground(String... params) {
        String modelKey = settings.modelKey;
        List<String> notifications = settings.enabledNotifications;
        boolean isFavorite = settings.isFavorite;

        String user = AccountHelper.getSelectedAccount(context);
        String key = MyTBAHelper.createKey(user, modelKey);
        ModelHelper.MODELS modelType = settings.modelType;

        ModelsMobileApiMessagesModelPreferenceMessage request = new ModelsMobileApiMessagesModelPreferenceMessage();
        request.setModelKey(modelKey);
        request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
        request.setNotifications(notifications);
        request.setFavorite(isFavorite);
        request.setModelType(Long.valueOf(settings.modelType.getEnum()));

        Database.Subscriptions subscriptionsTable = Database.getInstance(context).getSubscriptionsTable();
        Database.Favorites favoritesTable = Database.getInstance(context).getFavoritesTable();

        // Determine if we have to do anything
        List<String> existingNotificationsList = new ArrayList<>();
        Subscription existingSubscription = subscriptionsTable.get(key);
        if (existingSubscription != null) {
            existingNotificationsList = existingSubscription.getNotificationList();
        }

        Collections.sort(notifications);
        Collections.sort(existingNotificationsList);

        Log.d(Constants.LOG_TAG, "New notifications: " + notifications.toString());
        Log.d(Constants.LOG_TAG, "Existing notifications: " + existingNotificationsList.toString());

        boolean notificationsHaveChanged = !(notifications.equals(existingNotificationsList));

        // If the user is requesting a favorite and is already a favorite,
        // or if the user is requesting an unfavorite and it is already not a favorite,
        // and if the existing notification settings equal the new ones, do nothing.
        if (((isFavorite && favoritesTable.exists(key)) || (!isFavorite && !favoritesTable.exists(key))) && !notificationsHaveChanged) {
            // nothing has changed, no-op
            return Result.NOOP;
        } else {
            try {
                TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
                if(service == null){
                    Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
                    return Result.ERROR;
                }
                ModelsMobileApiMessagesBaseResponse response = service.model().setPreferences(request).execute();
                Log.d(Constants.LOG_TAG, "Result: "+response.getCode()+"/"+response.getMessage());
                if(response.getCode() == 401){
                    Log.e(Constants.LOG_TAG, response.getMessage());
                    return Result.ERROR;
                }
                JsonObject responseJson = JSONManager.getasJsonObject(response.getMessage());
                JsonObject fav = responseJson.get("favorite").getAsJsonObject(),
                        sub = responseJson.get("subscription").getAsJsonObject();
                int favCode = fav.get("code").getAsInt(),
                        subCode = sub.get("code").getAsInt();
                if (subCode == 200) {
                    // Request was successful, update the local databases
                    if (notifications.isEmpty()) {
                        subscriptionsTable.remove(key);
                    } else if (subscriptionsTable.exists(key)) {
                        subscriptionsTable.update(key, new Subscription(user, modelKey, notifications, modelType.getEnum()));
                    } else {
                        subscriptionsTable.add(new Subscription(user, modelKey, notifications, modelType.getEnum()));
                    }
                } else if (subCode == 500) {
                    Toast.makeText(context, String.format(context.getString(R.string.mytba_error), subCode, sub.get("message").getAsString()), Toast.LENGTH_SHORT).show();
                }
                // otherwise, we tried to add a favorite that already exists or remove one that didn't
                // so the database doesn't need to be changed

                if (favCode == 200) {
                    if (!isFavorite) {
                        favoritesTable.remove(key);
                    } else if (favoritesTable.exists(key)) {
                        // Already favorited, do nothing
                    } else {
                        favoritesTable.add(new Favorite(user, modelKey, modelType.getEnum()));
                    }
                } else if (favCode == 500) {
                    Toast.makeText(context, String.format(context.getString(R.string.mytba_error), favCode, fav.get("message").getAsString()), Toast.LENGTH_SHORT).show();
                }
                return Result.SUCCESS;

            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exception while updating model preferences!");
                e.printStackTrace();
                return Result.ERROR;
            }
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if (callbacks.get() != null) {
            ModelSettingsCallbacks cb = callbacks.get();
            switch (result) {
                case SUCCESS:
                    cb.onSuccess();
                    break;
                case NOOP:
                    cb.onNoOp();
                    break;
                case ERROR:
                    cb.onError();
                    break;
            }
        } else {
            Toast.makeText(context, "Callbacks were null", Toast.LENGTH_SHORT).show();
        }
    }
}
