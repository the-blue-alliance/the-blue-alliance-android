package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesModelPreferenceMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
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
        ArrayList<String> notifications = settings.enabledNotifications;
        boolean isFavorite = settings.isFavorite;

        Result notificationsResult, favoriteResult;

        // Save initial state so we can do a rollback
        Subscription initialSubscriptionState;
        Favorite initialFavorite;

        String user = AccountHelper.getSelectedAccount(context);
        String key = MyTBAHelper.createKey(user, modelKey);

        ModelsMobileApiMessagesModelPreferenceMessage request = new ModelsMobileApiMessagesModelPreferenceMessage();
        request.setModelKey(modelKey);
        request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
        request.setNotifications(notifications);
        request.setFavorite(isFavorite);

        Database.Subscriptions subscriptionsTable = Database.getInstance(context).getSubscriptionsTable();
        Database.Favorites favoritesTable = Database.getInstance(context).getFavoritesTable();

        // Determine if we have to do anything
        List<String> existingNotificationsList = new ArrayList<>();
        Subscription existingSubscription = subscriptionsTable.get(key);
        if(existingSubscription != null) {
            existingNotificationsList = existingSubscription.getNotificationList();
        }

        Collections.sort(notifications);
        Collections.sort(existingNotificationsList);

        boolean notificationsHaveChanged = !(notifications.equals(existingNotificationsList));

        // If the user is requesting a favorite and is already a favorite,
        // or if the user is requesting an unfavorite and it is already not a favorite,
        // and if the existing notification settings equal the new ones, do nothing.
        if (((isFavorite && favoritesTable.exists(key)) || (!isFavorite && !favoritesTable.exists(key))) && notificationsHaveChanged) {
            // nothing has changed, no-op
            return Result.NOOP;
        } else {
            try {
                TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
                ModelsMobileApiMessagesBaseResponse response = service.model().setPreferences(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    // Request was successful, update the local databases
                    if(notifications.isEmpty()) {
                        subscriptionsTable.remove(key);
                    } else if (subscriptionsTable.exists(key)){
                        subscriptionsTable.update(key, new Subscription(user, modelKey, notifications));
                    } else {
                        subscriptionsTable.add(new Subscription(user, modelKey, notifications));
                    }

                    if(!isFavorite) {
                        favoritesTable.remove(key);
                    } else if (favoritesTable.exists(key)) {
                        // Already favorited, do nothing
                    } else {
                        favoritesTable.add(new Favorite(user, modelKey));
                    }
                    return Result.SUCCESS;
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
