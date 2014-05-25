package com.thebluealliance.androidclient.background.match;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateMatchInfo extends AsyncTask<String, Void, APIResponse.CODE> {

    private Activity mActivity;
    private String mEventKey;
    private String mMatchKey;
    private Match mMatch;

    public PopulateMatchInfo(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        mMatchKey = params[0];
        mEventKey = mMatchKey.substring(0, mMatchKey.indexOf("_"));
        try {
            APIResponse<HashMap<Match.TYPE, ArrayList<Match>>> response = DataManager.getEventResults(mActivity, mEventKey);
            HashMap<Match.TYPE, ArrayList<Match>> matches = response.getData();
            // Extract the specified match from the list
            mMatch = null;
            for (Map.Entry<Match.TYPE, ArrayList<Match>> matchListEntry : matches.entrySet()) {
                ArrayList<Match> matchList = matchListEntry.getValue();
                for (Match match : matchList) {
                    if (match.getKey().equals(mMatchKey)) {
                        mMatch = match;
                        break;
                    }
                }
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load team info");
            //some temp data
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        if (code != APIResponse.CODE.NODATA) {
            JsonObject redAlliance = mMatch.getAlliances().getAsJsonObject("red");
            JsonArray redAllianceTeamKeys = redAlliance.getAsJsonArray("teams");

            // Red 1
            TextView red1 = ((TextView) mActivity.findViewById(R.id.red1));
            final String red1Key = redAllianceTeamKeys.get(0).getAsString();
            red1.setText(red1Key.replace("frc", ""));
            red1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(ViewTeamActivity.newInstance(mActivity, red1Key));
                }
            });

            // Red 2
            TextView red2 = ((TextView) mActivity.findViewById(R.id.red2));
            final String red2Key = redAllianceTeamKeys.get(1).getAsString();
            red2.setText(red2Key.replace("frc", ""));
            red2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(ViewTeamActivity.newInstance(mActivity, red2Key));
                }
            });

            // Red 3
            TextView red3 = ((TextView) mActivity.findViewById(R.id.red3));
            final String red3Key = redAllianceTeamKeys.get(2).getAsString();
            red3.setText(red3Key.replace("frc", ""));
            red3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(ViewTeamActivity.newInstance(mActivity, red3Key));
                }
            });

            // Red Score
            JsonElement redScore = redAlliance.get("score");
            TextView red_score = ((TextView) mActivity.findViewById(R.id.red_score));
            red_score.setText(redScore.getAsString());

            JsonObject blueAlliance = mMatch.getAlliances().getAsJsonObject("blue");
            JsonArray blueAllianceTeamKeys = blueAlliance.getAsJsonArray("teams");

            // Blue 1
            TextView blue1 = ((TextView) mActivity.findViewById(R.id.blue1));
            final String blue1Key = blueAllianceTeamKeys.get(0).getAsString();
            blue1.setText(blue1Key.replace("frc", ""));
            blue1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(ViewTeamActivity.newInstance(mActivity, blue1Key));
                }
            });

            // Blue 2
            TextView blue2 = ((TextView) mActivity.findViewById(R.id.blue2));
            final String blue2Key = blueAllianceTeamKeys.get(1).getAsString();
            blue2.setText(blue2Key.replace("frc", ""));
            blue2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(ViewTeamActivity.newInstance(mActivity, blue2Key));
                }
            });

            // Blue 3
            TextView blue3 = ((TextView) mActivity.findViewById(R.id.blue3));
            final String blue3Key = blueAllianceTeamKeys.get(2).getAsString();
            blue3.setText(blue3Key.replace("frc", ""));
            blue3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(ViewTeamActivity.newInstance(mActivity, blue3Key));
                }
            });

            // Blue score
            JsonElement blueScore = blueAlliance.get("score");
            TextView blue_score = ((TextView) mActivity.findViewById(R.id.blue_score));
            blue_score.setText(blueScore.getAsString());

            if(blueScore.getAsInt() > redScore.getAsInt()){
                //blue wins
                mActivity.findViewById(R.id.blue_alliance).setBackgroundColor(mActivity.getResources().getColor(R.color.light_blue));
                blue_score.setBackgroundColor(mActivity.getResources().getColor(R.color.blue));
                blue_score.setTextColor(mActivity.getResources().getColor(R.color.white));
                blue_score.setTypeface(Typeface.DEFAULT_BOLD);
            }else if(blueScore.getAsInt() < redScore.getAsInt()){
                //red wins
                mActivity.findViewById(R.id.red_allaince).setBackgroundColor(mActivity.getResources().getColor(R.color.light_red));
                red_score.setBackgroundColor(mActivity.getResources().getColor(R.color.red));
                red_score.setTextColor(mActivity.getResources().getColor(R.color.white));
                red_score.setTypeface(Typeface.DEFAULT_BOLD);
            }


            SimpleEvent event = Database.getInstance(mActivity).getEvent(mEventKey);
            if (event != null) {
                ((TextView) mActivity.findViewById(R.id.event_name)).setText(event.getEventName());
            }

            ((TextView) mActivity.findViewById(R.id.match_name)).setText(mMatch.getTitle());

            JsonArray videos = mMatch.getVideos();
            Picasso picasso = Picasso.with(mActivity);
            List<ImageView> images = new ArrayList();
            for (int i = 0; i < videos.size(); i++) {
                JsonObject video = videos.get(i).getAsJsonObject();
                if (video.get("type").getAsString().equals("youtube")) {
                    final String videoKey = video.get("key").getAsString();
                    String thumbnailURL = "http://img.youtube.com/vi/" + videoKey + "/0.jpg";
                    ImageView thumbnail = new ImageView(mActivity);
                    thumbnail.setAdjustViewBounds(true);
                    thumbnail.setClickable(true);
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
                            mActivity.startActivity(intent);
                        }
                    });
                    images.add(thumbnail);
                    picasso.load(thumbnailURL).into(thumbnail);
                }
            }
            for(int i = 0; i < images.size(); i++) {
                ImageView thumbnail = images.get(i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Add padding between thumbnails if the list of thumbnail has multiple items
                if(images.size() > 1 && i > 0) {
                    layoutParams.topMargin = Utilities.getPixelsFromDp(mActivity, 16);
                }
                ((LinearLayout) mActivity.findViewById(R.id.video_thumbnail_container)).addView(thumbnail, layoutParams);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                ((RefreshableHostActivity) mActivity).showWarningMessage(mActivity.getString(R.string.warning_using_cached_data));
            }

            mActivity.findViewById(R.id.progress).setVisibility(View.GONE);
            mActivity.findViewById(R.id.match_container).setVisibility(View.VISIBLE);
        }
    }


}
