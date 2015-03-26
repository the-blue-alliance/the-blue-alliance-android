package com.thebluealliance.androidclient.background.team;

import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamInfo extends AsyncTask<String, Void, APIResponse.CODE> {

    private TeamInfoFragment mFragment;
    private RefreshableHostActivity activity;
    private String mTeamName;
    private int mTeamNumber;
    private String mLocation;
    private String mFullName;
    private String mTeamKey;
    private String mTeamWebsite;
    private RequestParams requestParams;
    private long startTime;

    public PopulateTeamInfo(TeamInfoFragment fragment, RequestParams requestParams) {
        mFragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        mTeamKey = params[0];
        try {
            Long start = System.nanoTime();
            APIResponse<Team> teamResponse = DataManager.Teams.getTeam(activity, mTeamKey, requestParams);

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            Team team = teamResponse.getData();
            Long end = System.nanoTime();
            Log.d("doInBackground", "Total time to load team: " + (end - start));
            try {
                mTeamName = team.getNickname();
                mLocation = team.getLocation();
                mFullName = team.getFullName();
                mTeamWebsite = team.getWebsite();
                mTeamNumber = team.getTeamNumber();

            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
                Log.e(Constants.LOG_TAG, "Can't load team parameters");
                return APIResponse.CODE.NODATA;
            }

            return teamResponse.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load team info");
            //some temp data
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        View view = mFragment.getView();
        if (view != null && activity != null) {
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            View infoContainer = view.findViewById(R.id.team_info_container);
            if (code == APIResponse.CODE.NODATA) {
                noDataText.setText(R.string.no_team_info);
                noDataText.setVisibility(View.VISIBLE);
                infoContainer.setVisibility(View.GONE);
            } else {
                noDataText.setVisibility(View.GONE);
                TextView teamName = ((TextView) view.findViewById(R.id.team_name));
                if (mTeamName.isEmpty()) {
                    teamName.setText("Team " + mTeamNumber);
                } else {
                    teamName.setText(mTeamName);
                }

                View teamLocationContainer = view.findViewById(R.id.team_location_container);
                if (mLocation.isEmpty()) {
                    // No location; hide the location view
                    teamLocationContainer.setVisibility(View.GONE);
                } else {
                    // Show and populate the location view
                    ((TextView) view.findViewById(R.id.team_location)).setText(mLocation);
                    // Tag is used to create an ACTION_VIEW intent for a maps application
                    view.findViewById(R.id.team_location_container).setTag("geo:0,0?q=" + mLocation.replace(" ", "+"));
                }

                view.findViewById(R.id.team_twitter_button).setTag("https://twitter.com/search?q=%23" + mTeamKey);
                view.findViewById(R.id.team_youtube_button).setTag("https://www.youtube.com/results?search_query=" + mTeamKey);
                view.findViewById(R.id.team_cd_button).setTag("http://www.chiefdelphi.com/media/photos/tags/" + mTeamKey);
                view.findViewById(R.id.team_website_button).setTag(!mTeamWebsite.isEmpty() ? mTeamWebsite : "https://www.google.com/search?q=" + mTeamKey);
                if (mFullName.isEmpty()) {
                    // No full name specified, hide the view
                    view.findViewById(R.id.team_full_name_container).setVisibility(View.GONE);
                } else {
                    // This string needs to be specially formatted
                    SpannableString string = new SpannableString("aka " + mFullName);
                    string.setSpan(new TextAppearanceSpan(mFragment.getActivity(), R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    ((TextView) view.findViewById(R.id.team_full_name)).setText(string);
                }

                view.findViewById(R.id.team_next_match_label).setVisibility(View.GONE);
                view.findViewById(R.id.team_next_match_details).setVisibility(View.GONE);

                if (code == APIResponse.CODE.OFFLINECACHE) {
                    ((RefreshableHostActivity) mFragment.getActivity()).showWarningMessage(mFragment.getString(R.string.warning_using_cached_data));
                }

                view.findViewById(R.id.team_info_container).setVisibility(View.VISIBLE);
            }
            view.findViewById(R.id.progress).setVisibility(View.GONE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateTeamInfo secondLoad = new PopulateTeamInfo(mFragment, requestParams);
                mFragment.updateTask(secondLoad);
                secondLoad.execute(mTeamKey);
            } else {
                // Show notification if we've refreshed data.
                Log.i(Constants.REFRESH_LOG, "Team " + mTeamKey + " info refresh complete");
                if (activity != null && mFragment instanceof RefreshListener) {
                    activity.notifyRefreshComplete(mFragment);
                }
            }
            AnalyticsHelper.sendTimingUpdate(activity, System.currentTimeMillis() - startTime, "team info", mTeamKey);
        }
    }


}
