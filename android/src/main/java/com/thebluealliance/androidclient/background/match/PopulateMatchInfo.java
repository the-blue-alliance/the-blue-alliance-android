package com.thebluealliance.androidclient.background.match;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.util.ArrayList;
import java.util.HashMap;
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
            for(Map.Entry<Match.TYPE, ArrayList<Match>> matchListEntry : matches.entrySet()) {
                ArrayList<Match> matchList= matchListEntry.getValue();
                for(Match match : matchList) {
                    if(match.getKey().equals(mMatchKey)) {
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
            ((TextView) mActivity.findViewById(R.id.red1)).setText(redAllianceTeamKeys.get(0).getAsString().replace("frc", ""));
            ((TextView) mActivity.findViewById(R.id.red2)).setText(redAllianceTeamKeys.get(1).getAsString().replace("frc", ""));
            ((TextView) mActivity.findViewById(R.id.red3)).setText(redAllianceTeamKeys.get(2).getAsString().replace("frc", ""));
            ((TextView) mActivity.findViewById(R.id.red_score)).setText(redAlliance.get("score").getAsString());

            JsonObject blueAlliance = mMatch.getAlliances().getAsJsonObject("blue");
            JsonArray blueAllianceTeamKeys = blueAlliance.getAsJsonArray("teams");
            ((TextView) mActivity.findViewById(R.id.blue1)).setText(blueAllianceTeamKeys.get(0).getAsString().replace("frc", ""));
            ((TextView) mActivity.findViewById(R.id.blue2)).setText(blueAllianceTeamKeys.get(1).getAsString().replace("frc", ""));
            ((TextView) mActivity.findViewById(R.id.blue3)).setText(blueAllianceTeamKeys.get(2).getAsString().replace("frc", ""));
            ((TextView) mActivity.findViewById(R.id.blue_score)).setText(blueAlliance.get("score").getAsString());

            SimpleEvent event = Database.getInstance(mActivity).getEvent(mEventKey);
            if(event != null) {
                ((TextView) mActivity.findViewById(R.id.event_name)).setText(event.getEventName());
            }

            ((TextView) mActivity.findViewById(R.id.match_name)).setText(mMatch.getTitle());

            if (code == APIResponse.CODE.OFFLINECACHE) {
                ((RefreshableHostActivity) mActivity).showWarningMessage(mActivity.getString(R.string.warning_using_cached_data));
            }

            mActivity.findViewById(R.id.progress).setVisibility(View.GONE);
            mActivity.findViewById(R.id.match_container).setVisibility(View.VISIBLE);
        }
    }


}
