package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ContributorsActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ContributorListElement;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;

/**
 * Created by Nathan on 6/20/2014.
 */
public class PopulateContributors extends AsyncTask<String, Void, Void> {
    private ContributorsActivity activity;
    private ListViewAdapter adapter;

    public PopulateContributors(ContributorsActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        ArrayList<ListItem> list = new ArrayList<>();
        JsonArray data = new JsonArray();//JSONHelper.getasJsonArray(HTTP.GET("https://api.github.com/repos/the-blue-alliance/the-blue-alliance-android/contributors"));

        for (int i = 0; i < data.size(); i++) {
            list.add(new ContributorListElement(data.get(i).getAsJsonObject().get("login").getAsString(), data.get(i).getAsJsonObject().get("avatar_url").getAsString()));
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