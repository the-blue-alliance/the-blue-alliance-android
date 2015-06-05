package com.thebluealliance.androidclient.background.match;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.melnykov.fab.FloatingActionButton;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateMatchInfo extends AsyncTask<String, Void, APIResponse.CODE> {

    private RefreshableHostActivity mActivity;
    private String mMatchKey, mEventShortName, mMatchTitle;
    private ArrayList<ListItem> mMatchDetails;
    private RequestParams requestParams;
    private long startTime;

    public PopulateMatchInfo(RefreshableHostActivity activity, RequestParams requestParams) {
        mActivity = activity;
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        mMatchKey = params[0];
        if (!MatchHelper.validateMatchKey(mMatchKey)) {
            throw new IllegalArgumentException("Invalid match key. Can't populate match.");
        }
        String mEventKey = mMatchKey.substring(0, mMatchKey.indexOf("_"));
        try {
            APIResponse<Match> response = DataManager.Matches.getMatch(mActivity, mMatchKey, requestParams);
            Match match = response.getData();

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            try {
                mMatchDetails = new ArrayList<>();

                mMatchDetails.add(match.render(false, true, false, false));
                mMatchTitle = match.getTitle();
                Gson gson = JSONHelper.getGson();
                for (JsonElement v : match.getVideos()) {
                    if (Media.TYPE.fromString(v.getAsJsonObject().get("type").getAsString()) != Media.TYPE.NONE) {
                        mMatchDetails.add(gson.fromJson(v, Media.class).render());
                    }
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Couldn't get match data");
                return APIResponse.CODE.NODATA;
            }

            APIResponse<Event> eventResponse = DataManager.Events.getEvent(mActivity, mEventKey, requestParams);
            Event event = eventResponse.getData();
            if (event != null) {
                try {
                    mEventShortName = event.getEventShortName();
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.w(Constants.LOG_TAG, "Unable to render match info. Missing stuff");
                }
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load match info");
            //some temp data
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        if (mActivity != null) {
            ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.progress);
            FloatingActionButton myTbaFav = (FloatingActionButton) mActivity.findViewById(R.id.open_notification_settings_button);
            if (code != APIResponse.CODE.NODATA) {

                mActivity.setActionBarTitle(mMatchTitle);
                mActivity.setActionBarSubtitle("@ " + mMatchKey.substring(0, 4) + " " + mEventShortName);

                ListViewAdapter adapter = new ListViewAdapter(mActivity, mMatchDetails);
                ListView list = (ListView) mActivity.findViewById(R.id.match_details);

                //disable touch feedback (you can't click the elements here...)
                list.setCacheColorHint(android.R.color.transparent);
                list.setSelector(R.drawable.transparent);

                list.setAdapter(adapter);

                if (code == APIResponse.CODE.OFFLINECACHE) {
                    mActivity.showWarningMessage(mActivity.getString(R.string.warning_using_cached_data));
                }

                progressBar.setVisibility(View.GONE);

            } else {
                // No data found
                mActivity.setActionBarTitle(mMatchKey);
                mActivity.showWarningMessage(mActivity.getString(R.string.match_not_found));
                progressBar.setVisibility(View.GONE);
                if (myTbaFav != null) {
                    myTbaFav.setVisibility(View.GONE);
                }
            }

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                new PopulateMatchInfo(mActivity, requestParams).execute(mMatchKey);
            } else {
                // Show notification if we've refreshed data.
                Log.i(Constants.REFRESH_LOG, "Match " + mMatchKey + " refresh complete");
                if (mActivity != null && mActivity instanceof RefreshableHostActivity) {
                    mActivity.notifyRefreshComplete((RefreshListener) mActivity);
                }
            }
            AnalyticsHelper.sendTimingUpdate(mActivity, System.currentTimeMillis() - startTime, "match info", mMatchKey);
        }
    }
}
