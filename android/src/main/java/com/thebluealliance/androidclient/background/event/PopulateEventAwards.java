package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.datamanger.DataManager;
import com.thebluealliance.androidclient.datafeed.datamanger.Events;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.AwardListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;

/**
 * Retrieves awards data for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p/>
 *         File created by phil on 4/23/14.
 */
public class PopulateEventAwards extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ListViewAdapter adapter;
    private boolean forceFromCache;

    public PopulateEventAwards(Fragment f, boolean forceFromCache) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<>();

        APIResponse<ArrayList<Award>> response;
        try {
            response = Events.getEventAwards(activity, eventKey, forceFromCache);
            ArrayList<Award> awardList = response.getData();
            for (Award a : awardList) {
                ArrayList<AwardListElement> allWinners = a.renderAll();
                awards.addAll(allWinners);
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event awards");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null) {
            adapter = new ListViewAdapter(activity, awards);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no awards in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_awards_data);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView rankings = (ListView) view.findViewById(R.id.list);
                rankings.setAdapter(adapter);
            }
            // Display warning message if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);
        }

        if (code == APIResponse.CODE.LOCAL) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            new PopulateEventAwards(mFragment, false).execute(eventKey);
        } else {
            // Show notification if we've refreshed data.
            if (mFragment instanceof RefreshListener) {
                Log.d(Constants.LOG_TAG, "Event Awards refresh complete");
                activity.notifyRefreshComplete((RefreshListener) mFragment);
            }
        }
    }
}
