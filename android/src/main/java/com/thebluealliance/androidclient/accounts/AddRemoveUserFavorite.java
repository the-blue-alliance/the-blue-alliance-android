package com.thebluealliance.androidclient.accounts;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Favorite;

import java.io.IOException;

/**
 * File created by phil on 8/2/14.
 */
public class AddRemoveUserFavorite extends AsyncTask<String, Void, AddRemoveUserFavorite.Result> {

    protected enum Result {
        ADDED, REMOVED, ERROR
    }

    private Activity activity;
    private MenuItem icon;

    public AddRemoveUserFavorite(Activity activity, MenuItem icon) {
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
        String user = AccountHelper.getSelectedAccount(activity);
        String key = MyTBAHelper.createKey(user, modelKey);
        Database.Favorites table = Database.getInstance(activity).getFavoritesTable();
        TbaMobile service = AccountHelper.getAuthedTbaMobile(activity);
        ModelsMobileApiMessagesFavoriteMessage request = new ModelsMobileApiMessagesFavoriteMessage();
        request.setModelKey(modelKey);
        if(!table.exists(key)) {
            Log.d(Constants.LOG_TAG, "Favorite doesn't exist. Adding it");
            try {
                ModelsMobileApiMessagesBaseResponse response = service.favorites().add(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    table.add(new Favorite(user, modelKey));
                    return Result.ADDED;
                } else {
                    Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding favorite.\n" + response.getMessage());
                    return Result.ERROR;
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exampetion while adding favorite");
                e.printStackTrace();
                return Result.ERROR;
            }
        }else {
            Log.d(Constants.LOG_TAG, "Favorite already exists. Removing it");
            try {
                ModelsMobileApiMessagesBaseResponse response = service.favorites().remove(request).execute();
                if (response.getCode() == 200 || response.getCode() == 304) {
                    table.remove(key);
                    return Result.REMOVED;
                } else {
                    Log.w(Constants.LOG_TAG, "Code " + response.getCode() + " while adding favorite.\n" + response.getMessage());
                    return Result.ERROR;
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO Exampetion while adding favorite");
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
            icon.setIcon(R.drawable.ic_action_remove_favorite);
            icon.setTitle(activity.getString(R.string.action_remove_favorite));
            text = "Favorite added";
        } else if (result == Result.REMOVED) {
            icon.setIcon(R.drawable.ic_action_add_favorite);
            icon.setTitle(activity.getString(R.string.action_add_favorite));
            text = "Favorite removed";
        } else{
            text = "Error adding favorite";
        }
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
}
