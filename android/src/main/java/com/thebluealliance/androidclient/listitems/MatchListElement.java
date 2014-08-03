package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.MatchClickListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.views.MatchView;

import java.io.Serializable;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement implements Serializable {

    private String videoKey, matchTitle, redTeams[], blueTeams[], matchKey, redScore, blueScore, selectedTeamKey, timeString;
    private boolean showVideoIcon, showColumnHeaders;

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey, String timeString, String selectedTeamKey, boolean showVideoIcon, boolean showColumnHeaders) {
        super(matchKey);
        this.videoKey = youTubeVideoKey;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
        this.selectedTeamKey = selectedTeamKey;
        this.timeString = timeString;
        this.showVideoIcon = showVideoIcon;
        this.showColumnHeaders = showColumnHeaders;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        if (convertView == null || !(convertView instanceof MatchView)) {
            convertView = inflater.inflate(R.layout.match_view, null);
        }
        MatchView match = (MatchView) convertView;
        boolean played = false;
        if (!redScore.contains("?") && !blueScore.contains("?")) {
            played = true;
        }

        match.initWithParams(videoKey, matchTitle, redTeams, blueTeams, redScore, blueScore, matchKey, timeString, selectedTeamKey, showVideoIcon);
        match.showColumnHeaders(showColumnHeaders);
        if (played) {
            match.showTime(false);
            match.showScores(true);
        } else {
            match.showTime(true);
            match.showScores(false);
        }
        return match;
    }
}
