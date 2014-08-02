package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.HTTP;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.gcm.GCMHelper;

import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * File created by phil on 8/2/14.
 */
public class UserFavorite extends AsyncTask<String, Void, Boolean> {

    public static final String USER_KEY = "user_key", MODEL_KEY = "model_key";


    private Context context;
    private GoogleApiClient driveClient;
    private MenuItem icon;

    public UserFavorite(Context context, GoogleApiClient driveClient, MenuItem icon){
        this.context = context;
        this.driveClient = driveClient;
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
        String endpoint = TBAv2.getGCMEndpoint(context, TBAv2.GCM_ENDPOINT.FAVORITE_ADD);

        JsonObject data = new JsonObject();
        data.addProperty(USER_KEY, AccountHelper.getGCMKey(context, driveClient));
        data.addProperty(MODEL_KEY, modelKey);

        Map<String, String> headers = new HashMap<>();
        headers.put(GCMAuthHelper.REGISTRATION_CHECKSUM, GCMHelper.requestChecksum(context, data));

        HttpResponse response = HTTP.postRequest(endpoint, headers, data);
        int responseCode = response.getStatusLine().getStatusCode();
        Log.d(Constants.LOG_TAG, "Code from adding favorite: "+responseCode);
        // TODO add exponential backoff
        return responseCode == 200;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        icon.setActionView(null);
        String text;
        if(result){
            icon.setIcon(R.drawable.ic_action_remove_favorite);
            text = "Favorite added";
        }else{
            text = "Error adding favorite";
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
