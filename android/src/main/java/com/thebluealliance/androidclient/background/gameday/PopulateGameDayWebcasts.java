package com.thebluealliance.androidclient.background.gameday;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndNameComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.gameday.GamedayWebcastsFragment;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phil on 3/27/15.
 */
public class PopulateGameDayWebcasts extends AsyncTask<String, Void, APIResponse.CODE> {

    private Activity activity;
    private GamedayWebcastsFragment fragment;
    private ArrayList<ListItem> webcasts;
    private RequestParams requestParams;

    public PopulateGameDayWebcasts(GamedayWebcastsFragment fragment, RequestParams requestParams) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        webcasts = new ArrayList<>();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        int year = Utilities.getCurrentYear();
        int week = Utilities.getCurrentCompWeek();
        APIResponse<ArrayList<Event>> response;
        try {
            response = DataManager.Events.getSimpleEventsInWeek(activity, year, week, requestParams);
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch current events");
            return APIResponse.CODE.NODATA;
        }

        Collections.sort(response.getData(), new EventSortByTypeAndNameComparator());

        for (Event event : response.getData()) {
            webcasts.addAll(event.renderWebcasts());
        }

        return response.getCode();
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if (activity != null && fragment != null && fragment.getView() != null) {
            View view = fragment.getView();
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            ListViewAdapter adapter = new ListViewAdapter(activity, webcasts);
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_webcast_data_found);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView rankings = (ListView) view.findViewById(R.id.list);
                Parcelable state = rankings.onSaveInstanceState();
                rankings.setAdapter(adapter);
                rankings.onRestoreInstanceState(state);
                noDataText.setVisibility(View.GONE);
            }

            // Remove progress indicator and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateGameDayWebcasts secondLoad = new PopulateGameDayWebcasts(fragment, requestParams);
                secondLoad.execute();
            }
        }
    }
}