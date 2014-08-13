package com.thebluealliance.androidclient.background.mytba;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Favorite;

import java.util.ArrayList;

/**
 * File created by phil on 8/2/14.
 */
public class PopulateUserFavorites extends AsyncTask<Void, Void, Void> {

    public static final String USER_KEY = "user_key";

    private MyFavoritesFragment fragment;
    private RefreshableHostActivity activity;
    private boolean forceFromCache;
    private ArrayList<ListItem> favorites;

    public PopulateUserFavorites(MyFavoritesFragment fragment, boolean forceFromCache) {
        super();
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (activity != null) {
            activity.showMenuProgressBar();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        favorites = new ArrayList<>();
        ArrayList<Favorite> collection = Database.getInstance(activity).getFavoritesTable().getForUser(AccountHelper.getSelectedAccount(activity));
        if (collection != null) {
            for (Favorite favorite : collection) {
                favorites.add(ModelHelper.renderModelFromKey(activity, favorite.getModelKey()));
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        ListViewAdapter adapter = new ListViewAdapter(activity, favorites);
        ListView listView = (ListView) fragment.getView().findViewById(R.id.list);
        listView.setAdapter(adapter);

        fragment.getView().findViewById(R.id.progress).setVisibility(View.GONE);

        activity.notifyRefreshComplete(fragment);
    }
}
