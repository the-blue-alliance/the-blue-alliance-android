package com.thebluealliance.androidclient.background.mytba;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        // TODO need to associate a data model with this, eventually
        // (e.g. store favorites locally. Don't forget multi account support)

        favorites = new ArrayList<>();
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
        try {
            ModelsMobileApiMessagesFavoriteCollection favoriteCollection = service.favorites().list().execute();
            List<ModelsMobileApiMessagesFavoriteMessage> collection = favoriteCollection.getFavorites();
            if(collection != null) {
                for (ModelsMobileApiMessagesFavoriteMessage favorite : collection) {
                    favorites.add(new LabelValueListItem("", favorite.getModelKey()));
                }
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception fetching favorites");
            e.printStackTrace();
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
    }
}
