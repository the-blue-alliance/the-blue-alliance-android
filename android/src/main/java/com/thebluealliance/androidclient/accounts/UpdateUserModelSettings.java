package com.thebluealliance.androidclient.accounts;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
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

        // Handle notifications first
        String user = AccountHelper.getSelectedAccount(context);
        String key = MyTBAHelper.createKey(user, modelKey);
        Database.Subscriptions subscriptionsTable = Database.getInstance(context).getSubscriptionsTable();
        initialSubscriptionState = subscriptionsTable.get(key);
        if (notifications.size() == 0) {
            // All notifications were removed
            if (subscriptionsTable.exists(key)) {
                TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
                ModelsMobileApiMessagesSubscriptionMessage request = new ModelsMobileApiMessagesSubscriptionMessage();
                request.setModelKey(modelKey);
                request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
                request.setNotifications(new ArrayList<String>());
                Log.d(Constants.LOG_TAG, "Subscription already exists. Removing it");
                try {
                    ModelsMobileApiMessagesBaseResponse response = service.subscriptions().remove(request).execute();
                    if (response.getCode() == 200 || response.getCode() == 304) {
                        subscriptionsTable.remove(key);
                        notificationsResult = Result.SUCCESS;
                    } else {
                        Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding subscription.\n" + response.getMessage());
                        notificationsResult = Result.ERROR;
                    }
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "IO Exception while adding subscription");
                    e.printStackTrace();
                    notificationsResult = Result.ERROR;
                }
            } else {
                notificationsResult = Result.NOOP;
            }
        } else {
            // At least some notifications still exist. Update the list.
            TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
            ModelsMobileApiMessagesSubscriptionMessage request = new ModelsMobileApiMessagesSubscriptionMessage();
            request.setModelKey(modelKey);
            request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
            request.setNotifications(notifications);

            ContentValues newValues = new Subscription(user, modelKey, notifications).getParams();
            Subscription oldSub = subscriptionsTable.get(key);
            if(oldSub == null) {
                oldSub = new Subscription();
            }
            ContentValues oldValues = oldSub.getParams();

            if(!newValues.equals(oldValues)) {
                // Subscriptions have changed
                Log.d(Constants.LOG_TAG, "Updating subscriptions");
                try {
                    ModelsMobileApiMessagesBaseResponse response = service.subscriptions().add(request).execute();
                    if (response.getCode() == 200) {
                        subscriptionsTable.add(new Subscription(user, modelKey, notifications));
                        notificationsResult = Result.SUCCESS;
                    } else if (response.getCode() == 304) {
                        Log.d(Constants.LOG_TAG, "Subscription not modified");
                        notificationsResult = Result.NOOP;
                    } else {
                        Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding subscription.\n" + response.getMessage());
                        notificationsResult = Result.ERROR;
                    }
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "IO Exception while adding subscription");
                    e.printStackTrace();
                    notificationsResult = Result.ERROR;
                }
            } else {
                // Nothing has changed. No-op
                notificationsResult = Result.NOOP;
            }
        }

        // Now handle favorite
        Log.d(Constants.LOG_TAG, "Favorite: " + modelKey);
        Database.Favorites table = Database.getInstance(context).getFavoritesTable();

        // Note the initial state
        initialFavorite = table.get(key);

        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        ModelsMobileApiMessagesFavoriteMessage request = new ModelsMobileApiMessagesFavoriteMessage();
        request.setModelKey(modelKey);
        request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
        if (isFavorite && table.exists(key)) {
            // Already favorited; no op
            favoriteResult = Result.NOOP;
        } else if (isFavorite && !table.exists(key)) {
            Log.d(Constants.LOG_TAG, "Favorite doesn't exist. Adding it");
            try {
                ModelsMobileApiMessagesBaseResponse response = service.favorites().add(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    table.add(new Favorite(user, modelKey));
                    favoriteResult = Result.SUCCESS;
                } else {
                    Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding favorite.\n" + response.getMessage());
                    favoriteResult = Result.ERROR;
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exampetion while adding favorite");
                e.printStackTrace();
                favoriteResult = Result.ERROR;
            }
        } else if (!isFavorite && !table.exists(key)) {
            // Already not favorited; no op
            favoriteResult = Result.NOOP;
        } else if (!isFavorite && table.exists(key)) {
            Log.d(Constants.LOG_TAG, "Favorite already exists. Removing it");
            try {
                ModelsMobileApiMessagesBaseResponse response = service.favorites().remove(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    table.remove(key);
                    favoriteResult = Result.SUCCESS;
                } else {
                    Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding favorite.\n" + response.getMessage());
                    favoriteResult = Result.ERROR;
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exception while adding favorite");
                e.printStackTrace();
                favoriteResult = Result.ERROR;
            }
        } else {
            // We should never get to this point, but the compiler complains if we don't initialize favoriteResult
            favoriteResult = Result.NOOP;
        }

        if (notificationsResult == Result.ERROR || favoriteResult == Result.ERROR) {
            // If either thing errored, roll back the database transaction
            if(initialFavorite != null) {
                // There was something in the database before; remove any changes and commit the old data
                table.remove(key);
                table.add(initialFavorite);
            } else {
                // Delete anything that was added
                table.remove(key);
            }

            if(initialSubscriptionState != null) {
                // There was something in the database before; remove any changes and commit the old data
                subscriptionsTable.remove(key);
                subscriptionsTable.add(initialSubscriptionState);
            } else {
                // Delete anything that was added
                subscriptionsTable.remove(key);
            }
            return Result.ERROR;
        } else if (notificationsResult == Result.NOOP && favoriteResult == Result.NOOP) {
            return Result.NOOP;
        } else {
            return Result.SUCCESS;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if(callbacks.get() != null) {
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
