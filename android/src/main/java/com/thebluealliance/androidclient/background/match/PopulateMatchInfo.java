package com.thebluealliance.androidclient.background.match;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateMatchInfo extends AsyncTask<String, Void, APIResponse.CODE> {

    private RefreshableHostActivity mActivity;
    private String mMatchKey;
    private String mMatchTitle;
    private JsonArray mMatchVideos;
    private String mEventName;
    private JsonObject alliances;
    private boolean forceFromCache;

    public PopulateMatchInfo(RefreshableHostActivity activity, boolean forceFromCache) {
        mActivity = activity;
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mActivity != null) {
            mActivity.showMenuProgressBar();
        }
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        mMatchKey = params[0];
        if (!MatchHelper.validateMatchKey(mMatchKey)) {
            throw new IllegalArgumentException("Invalid match key. Can't populate match.");
        }
        String mEventKey = mMatchKey.substring(0, mMatchKey.indexOf("_"));
        try {
            APIResponse<Match> response = DataManager.Matches.getMatch(mActivity, mMatchKey, forceFromCache);
            Match match = response.getData();

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            try {
                alliances = match.getAlliances();
                mMatchTitle = match.getTitle();
                mMatchVideos = match.getVideos();
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Couldn't get match data");
                return APIResponse.CODE.NODATA;
            }

            APIResponse<Event> eventResponse = DataManager.Events.getEvent(mActivity, mEventKey, forceFromCache);
            Event event = eventResponse.getData();
            if (event != null) {
                try {
                    mEventName = event.getEventName();
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.w(Constants.LOG_TAG, "Can't get name for event");
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

        if (code != APIResponse.CODE.NODATA) {

            mActivity.setActionBarTitle(mMatchTitle);

            JsonObject redAlliance = alliances.getAsJsonObject("red");
            JsonArray redAllianceTeamKeys = redAlliance.getAsJsonArray("teams");

            TextView red1 = ((TextView) mActivity.findViewById(R.id.red1));
            TextView red2 = ((TextView) mActivity.findViewById(R.id.red2));
            TextView red3 = ((TextView) mActivity.findViewById(R.id.red3));

            TeamAtEventClickListener listener = new TeamAtEventClickListener(mActivity);
            String eventKey = mMatchKey.split("_")[0];

            // Don't set any text or listeners if there's no teams in the red alliance for some reason.
            if (redAllianceTeamKeys.size() == 0) {
                red1.setText("");
                red2.setText("");
                red3.setText("");
            } else {
                // Red 1
                String red1Key = redAllianceTeamKeys.get(0).getAsString();
                red1.setText(red1Key.substring(3));
                red1.setTag(red1Key+"@"+eventKey);
                red1.setOnClickListener(listener);

                // Red 2
                String red2Key = redAllianceTeamKeys.get(1).getAsString();
                red2.setText(red2Key.substring(3));
                red2.setTag(red2Key+"@"+eventKey);
                red2.setOnClickListener(listener);

                // Only add the third team if the alliance has three teams.
                if (redAllianceTeamKeys.size() > 2) {
                    // Red 3
                    String red3Key = redAllianceTeamKeys.get(2).getAsString();
                    red3.setText(red3Key.substring(3));
                    red3.setTag(red3Key+"@"+eventKey);
                    red3.setOnClickListener(listener);

                } else {
                    red3.setVisibility(View.GONE);
                }
            }
            // Red Score
            JsonElement redScore = redAlliance.get("score");
            TextView red_score = ((TextView) mActivity.findViewById(R.id.red_score));
            if (redScore.getAsInt() < 0) { // if there is no score, add "?"
                red_score.setText("?");
            } else {
                red_score.setText(redAlliance.get("score").getAsString());
            }

            // Repeat process for blue alliance.
            JsonObject blueAlliance = alliances.getAsJsonObject("blue");
            JsonArray blueAllianceTeamKeys = blueAlliance.getAsJsonArray("teams");

            TextView blue1 = ((TextView) mActivity.findViewById(R.id.blue1));
            TextView blue2 = ((TextView) mActivity.findViewById(R.id.blue2));
            TextView blue3 = ((TextView) mActivity.findViewById(R.id.blue3));

            if (blueAllianceTeamKeys.size() == 0) {
                blue1.setText("");
                blue2.setText("");
                blue3.setText("");
            } else {
                // Blue 1
                String blue1Key = blueAllianceTeamKeys.get(0).getAsString();
                blue1.setText(blue1Key.substring(3));
                blue1.setTag(blue1Key+"@"+eventKey);
                blue1.setOnClickListener(listener);

                // Blue 2
                String blue2Key = blueAllianceTeamKeys.get(1).getAsString();
                blue2.setText(blue2Key.substring(3));
                blue2.setTag(blue2Key+"@"+eventKey);
                blue2.setOnClickListener(listener);

                if (blueAllianceTeamKeys.size() > 2) {
                    // Blue 3
                    String blue3Key = blueAllianceTeamKeys.get(2).getAsString();
                    blue3.setText(blue3Key.substring(3));
                    blue3.setTag(blue3Key+"@"+eventKey);
                    blue3.setOnClickListener(listener);

                } else {
                    blue3.setVisibility(View.GONE);
                }
            }
            // Blue score
            TextView blue_score = ((TextView) mActivity.findViewById(R.id.blue_score));
            JsonElement blueScore = blueAlliance.get("score");
            if (blueScore.getAsInt() < 0) {
                blue_score.setText("?");
            } else {
                blue_score.setText(blueScore.getAsString());
            }

            if (blueScore.getAsInt() > redScore.getAsInt()) {
                //blue wins
                View blue_alliance = mActivity.findViewById(R.id.blue_alliance);
                blue_alliance.setBackgroundResource(R.drawable.blue_border);
            } else if (blueScore.getAsInt() < redScore.getAsInt()) {
                //red wins
                View red_alliance = mActivity.findViewById(R.id.red_alliance);
                red_alliance.setBackgroundResource(R.drawable.red_border);
            }


            if(mEventName != null && !mEventName.isEmpty()) {
                ((TextView) mActivity.findViewById(R.id.event_name)).setText(mMatchKey.substring(0,4) + " " + mEventName);
            }

            Picasso picasso = Picasso.with(mActivity);
            List<ImageView> images = new ArrayList<>();
            for (int i = 0; i < mMatchVideos.size(); i++) {
                JsonObject video = mMatchVideos.get(i).getAsJsonObject();
                if (video.get("type").getAsString().equals("youtube")) {
                    final String videoKey = video.get("key").getAsString();
                    String thumbnailURL = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(Media.TYPE.YOUTUBE), videoKey);
                    ImageView thumbnail = new ImageView(mActivity);
                    thumbnail.setAdjustViewBounds(true);
                    thumbnail.setClickable(true);
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(String.format(Constants.MEDIA_LINK_URL_PATTERN.get(Media.TYPE.YOUTUBE), videoKey)));
                            mActivity.startActivity(intent);
                        }
                    });
                    images.add(thumbnail);
                    picasso.load(thumbnailURL).into(thumbnail);
                }
            }
            LinearLayout mediaList = (LinearLayout) mActivity.findViewById(R.id.video_thumbnail_container);
            mediaList.removeAllViews();
            for (int i = 0; i < images.size(); i++) {
                ImageView thumbnail = images.get(i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Add padding between thumbnails if the list of thumbnail has multiple items
                if (!images.isEmpty() && i > 0) {
                    layoutParams.topMargin = Utilities.getPixelsFromDp(mActivity, 16);
                }
                mediaList.addView(thumbnail, layoutParams);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                ((RefreshableHostActivity) mActivity).showWarningMessage(mActivity.getString(R.string.warning_using_cached_data));
            }

            mActivity.findViewById(R.id.progress).setVisibility(View.GONE);
            mActivity.findViewById(R.id.match_container).setVisibility(View.VISIBLE);

        }

        if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            new PopulateMatchInfo(mActivity, false).execute(mMatchKey);
        } else {
            // Show notification if we've refreshed data.
            Log.i(Constants.REFRESH_LOG, "Match " + mMatchKey + " refresh complete");
            if (mActivity instanceof RefreshableHostActivity) {
                ((RefreshableHostActivity) mActivity).notifyRefreshComplete((RefreshListener) mActivity);
            }
        }

    }
}
