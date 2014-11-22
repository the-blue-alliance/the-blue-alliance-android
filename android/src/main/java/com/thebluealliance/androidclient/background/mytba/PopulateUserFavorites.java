package com.thebluealliance.androidclient.background.mytba;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
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
    protected Void doInBackground(Void... params) {

        favorites = new ArrayList<>();
        ArrayList<Favorite> collection = Database.getInstance(activity).getFavoritesTable().getForUser(AccountHelper.getSelectedAccount(activity));
        int lastModel = -1;
        if (collection != null) {
            for (Favorite favorite : collection) {
                ListItem item = ModelHelper.renderModelFromKey(activity, favorite.getModelKey(), favorite.getModelType());
                if(item != null) {
                    if(lastModel != favorite.getModelEnum()){
                        favorites.add(new EventTypeHeader(favorite.getModelType().getTitle()));
                    }
                    favorites.add(item);
                }
                lastModel = favorite.getModelEnum();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        View view = fragment.getView();
        if(activity != null && fragment != null && view != null) {
            TextView noDataText = (TextView)view.findViewById(R.id.no_data);
            if (favorites == null || favorites.isEmpty()) {
                noDataText.setText(activity.getString(R.string.no_favorite_data));
                noDataText.setVisibility(View.VISIBLE);
            } else {
                noDataText.setVisibility(View.GONE);
                ListViewAdapter adapter = new ListViewAdapter(activity, favorites);
                ListView listView = (ListView) fragment.getView().findViewById(R.id.list);
                listView.setAdapter(adapter);
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);

            activity.notifyRefreshComplete(fragment);
        }
    }
}
