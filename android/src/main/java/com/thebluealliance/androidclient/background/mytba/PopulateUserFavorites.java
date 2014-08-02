package com.thebluealliance.androidclient.background.mytba;

import android.os.AsyncTask;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.HTTP;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;
import com.thebluealliance.androidclient.gcm.GCMHelper;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * File created by phil on 8/2/14.
 */
public class PopulateUserFavorites extends AsyncTask<Void, Void, Void> {

    public static final String USER_KEY = "user_key";

    private MyFavoritesFragment fragment;
    private RefreshableHostActivity activity;
    private GoogleApiClient apiClient;
    private boolean forceFromCache;
    private ArrayList<ListItem> favorites;

    public PopulateUserFavorites(MyFavoritesFragment fragment, GoogleApiClient apiClient, boolean forceFromCache){
        super();
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.apiClient = apiClient;
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(activity != null) {
            activity.showMenuProgressBar();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO need to associate a data model with this, eventually
        // (e.g. store favorites locally. Don't forget multi account support)

        String endpoint = TBAv2.getGCMEndpoint(activity, TBAv2.GCM_ENDPOINT.FAVORITE_LIST);

        JsonObject data = new JsonObject();
        data.addProperty(USER_KEY, AccountHelper.getGCMKey(activity, apiClient));

        Map<String, String> headers = new HashMap<>();
        headers.put(GCMAuthHelper.REGISTRATION_CHECKSUM, GCMHelper.requestChecksum(activity, data));

        String result = HTTP.POST(endpoint, headers, data);

        JsonArray favoriteList = JSONManager.getasJsonArray(result);

        favorites = new ArrayList<>();
        for(JsonElement e: favoriteList){
            favorites.add(new LabelValueListItem("", e.getAsString()));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        ListViewAdapter adapter = new ListViewAdapter(activity, favorites);
        ListView listView = (ListView) fragment.getView().findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
