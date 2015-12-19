package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.views.MatchView;

import java.io.Serializable;
import java.util.Arrays;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement implements Serializable {

    public final String videoKey, matchTitle, redTeams[], blueTeams[], matchKey, redScore, blueScore, selectedTeamKey;
    public final long time;
    public final boolean showVideoIcon, showColumnHeaders, showMatchTitle, clickable;

    // utility constructor for rendering UpcomingMatchNotification
    public MatchListElement(String[] redTeams, String[] blueTeams, String matchKey, long time, String selectedTeamKey) {
        this("", "", redTeams, blueTeams, "?", "?", matchKey, time, selectedTeamKey, false, false, false, true);
    }

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey, long time, String selectedTeamKey, boolean showVideoIcon, boolean showColumnHeaders, boolean showMatchTitle, boolean clickable) {
        super(matchKey);
        this.videoKey = youTubeVideoKey;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
        this.selectedTeamKey = selectedTeamKey;
        this.time = time;
        this.showVideoIcon = showVideoIcon;
        this.showColumnHeaders = showColumnHeaders;
        this.showMatchTitle = showMatchTitle;
        this.clickable = clickable;
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

        match.initWithParams(videoKey, matchTitle, redTeams, blueTeams, redScore, blueScore, matchKey, time, selectedTeamKey, showVideoIcon);
        match.showColumnHeaders(showColumnHeaders);
        if (played) {
            match.showTime(false);
            match.showScores(true);
        } else {
            match.showTime(true);
            match.showScores(false);
        }
        if (time == -1) {
            match.showTime(false);
        }
        match.setClickToShowDetails(clickable);
        match.showMatchTitle(showMatchTitle);
        return match;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MatchListElement)) {
            return false;
        }
        MatchListElement element = (MatchListElement) o;

        return videoKey.equals(element.videoKey) &&
          matchTitle.equals(element.matchTitle) &&
          Arrays.equals(redTeams, element.redTeams) &&
          Arrays.equals(blueTeams, element.blueTeams) &&
          redScore.equals(element.redScore) &&
          blueScore.equals(element.blueScore) &&
          matchKey.equals(element.matchKey) &&
          selectedTeamKey == null
            ? element.selectedTeamKey == null
            : selectedTeamKey.equals(element.selectedTeamKey) &&
          time == element.time &&
          showVideoIcon == element.showVideoIcon &&
          showColumnHeaders == element.showColumnHeaders &&
          showMatchTitle == element.showMatchTitle &&
          clickable == element.clickable;
    }
}
