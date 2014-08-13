package com.thebluealliance.androidclient.accounts;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;

import java.io.IOException;

/**
 * File created by phil on 8/2/14.
 */
public class UserFavorite extends AsyncTask<String, Void, Boolean> {

    private Activity activity;
    private MenuItem icon;

    public UserFavorite(Activity activity, MenuItem icon) {
        this.activity = activity;
        this.icon = icon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        icon.setActionView(R.layout.actionbar_indeterminate_progress);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String modelKey = params[0];
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(activity);
        try {
            String token = currentCredential.getToken();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }
        TbaMobile service = AccountHelper.getTbaMobile(currentCredential);
        ModelsMobileApiMessagesFavoriteMessage request = new ModelsMobileApiMessagesFavoriteMessage();
        request.setModelKey(modelKey);
        try {
            ModelsMobileApiMessagesBaseResponse response = service.favorites().add(request).execute();
            if(response.getCode() == 200){
                return true;
            }else{
                Log.w(Constants.LOG_TAG, "Code "+response.getCode()+" while adding favorite.\n"+response.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exampetion while adding favorite");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        icon.setActionView(null);
        String text;
        if (result) {
            icon.setIcon(R.drawable.ic_action_remove_favorite);
            text = "Favorite added";
        } else {
            text = "Error adding favorite";
        }
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
}
