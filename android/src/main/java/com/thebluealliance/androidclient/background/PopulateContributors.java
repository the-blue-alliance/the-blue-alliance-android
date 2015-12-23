package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ContributorsActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.listitems.ContributorListElement;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.http.HTTP;

public class PopulateContributors extends AsyncTask<String, Void, Void> {
    private ContributorsActivity activity;
    private ListViewAdapter adapter;

    public PopulateContributors(ContributorsActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        ArrayList<ListItem> list = new ArrayList<>();

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/repos/the-blue-alliance/the-blue-alliance-android/contributors")
                    .build();
            Response response = client.newCall(request).execute();
            JsonArray data = JSONHelper.getasJsonArray(response.body().string());

            for (JsonElement e : data) {
                JsonObject user = e.getAsJsonObject();
                String username = user.get("login").getAsString();
                int contributionCount = user.get("contributions").getAsInt();
                String avatarUrl = user.get("avatar_url").getAsString();
                list.add(new ContributorListElement(username, contributionCount, avatarUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter = new ListViewAdapter(activity, list);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.findViewById(R.id.progress).setVisibility(View.GONE);
        activity.findViewById(R.id.no_data).setVisibility(View.GONE);
        activity.findViewById(android.R.id.list).setVisibility(View.VISIBLE);
        ListView contributors = (ListView) activity.findViewById(android.R.id.list);
        Parcelable state = contributors.onSaveInstanceState();
        contributors.setAdapter(adapter);
        contributors.onRestoreInstanceState(state);
    }

}