package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tbatv_dev_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_dev_hrd.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_dev_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.views.FloatingActionButton;

import java.io.IOException;

/**
 * File created by phil on 8/2/14.
 */
public class AddRemoveUserFavorite extends AsyncTask<String, Void, AddRemoveUserFavorite.Result> {

    protected enum Result {
        ADDED, REMOVED, ERROR
    }

    private Context context;
    private FloatingActionButton icon;

    public AddRemoveUserFavorite(Context context, FloatingActionButton icon) {
        this.context = context;
        this.icon = icon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Result doInBackground(String... params) {
        String modelKey = params[0];
        Log.d(Constants.LOG_TAG, "Favorite: "+modelKey);
        String user = AccountHelper.getSelectedAccount(context);
        String key = MyTBAHelper.createKey(user, modelKey);
        Database.Favorites table = Database.getInstance(context).getFavoritesTable();
        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        ModelsMobileApiMessagesFavoriteMessage request = new ModelsMobileApiMessagesFavoriteMessage();
        request.setModelKey(modelKey);
        request.setDeviceKey(GCMAuthHelper.getRegistrationId(context));
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
        String text;
        if (result == Result.ADDED) {
            icon.setImageResource(R.drawable.ic_my_tba_blue);
            text = "Favorite added";
        } else if (result == Result.REMOVED) {
            icon.setImageResource(R.drawable.ic_action_add_favorite);
            text = "Favorite removed";
        } else{
            text = "Error adding favorite";
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
