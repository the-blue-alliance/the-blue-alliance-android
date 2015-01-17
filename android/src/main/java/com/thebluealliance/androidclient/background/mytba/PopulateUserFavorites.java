package com.thebluealliance.androidclient.background.mytba;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.FavoriteSortByModelComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Favorite;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 8/2/14.
 */
public class PopulateUserFavorites extends AsyncTask<Void, Void, APIResponse.CODE> {

    private MyFavoritesFragment fragment;
    private RefreshableHostActivity activity;
    private RequestParams requestParams;
    private ArrayList<ListItem> favorites;

    public PopulateUserFavorites(MyFavoritesFragment fragment, RequestParams requestParams) {
        super();
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected APIResponse.CODE doInBackground(Void... params) {

        favorites = new ArrayList<>();
        APIResponse<ArrayList<Favorite>> response = DataManager.MyTBA.updateUserFavorites(activity, requestParams);
        ArrayList<Favorite> collection;
        if(!requestParams.forceFromCache && response.getCode() == APIResponse.CODE.WEBLOAD){
            // we have new favorite data
            collection = response.getData();
            Collections.sort(collection, new FavoriteSortByModelComparator());
        }else {
            // otherwise, load local data
            collection = Database.getInstance(activity).getFavoritesTable().getForUser(AccountHelper.getSelectedAccount(activity));
            if(requestParams.forceFromCache){
                // When we force from cache, set the codes properly
                // so the second task will fire off
                response.updateCode(APIResponse.CODE.LOCAL);
            }
        }

        int lastModel = -1;
        if (collection != null) {
            for (Favorite favorite : collection) {
                ListItem item = ModelHelper.renderModelFromKey(activity, favorite.getModelKey(), favorite.getModelType(), true);
                if(item != null) {
                    if(lastModel != favorite.getModelEnum()){
                        favorites.add(new EventTypeHeader(favorite.getModelType().getTitle()));
                    }
                    favorites.add(item);
                }
                lastModel = favorite.getModelEnum();
            }
        }

        return response.getCode();
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        if(activity != null && fragment != null && fragment.getView() != null) {
            View view = fragment.getView();
            TextView noDataText = (TextView)view.findViewById(R.id.no_data);
            ListView listView = (ListView) fragment.getView().findViewById(R.id.list);
            if (code == APIResponse.CODE.NODATA || favorites == null || favorites.isEmpty()) {
                noDataText.setText(activity.getString(R.string.no_favorite_data));
                noDataText.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noDataText.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                ListViewAdapter adapter = new ListViewAdapter(activity, favorites);
                listView.setAdapter(adapter);
            }

            // Display warning if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateUserFavorites secondLoad = new PopulateUserFavorites(fragment, requestParams);
                fragment.updateTask(secondLoad);
                secondLoad.execute();
            } else {
                // Show notification if we've refreshed data.
                if (fragment instanceof RefreshListener) {
                    Log.d(Constants.REFRESH_LOG, "User favorites refresh complete");
                    activity.notifyRefreshComplete(fragment);
                }
            }
        }
    }
}
