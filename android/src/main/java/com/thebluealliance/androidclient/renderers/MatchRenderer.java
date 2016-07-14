package com.thebluealliance.androidclient.renderers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.thebluealliance.androidclient.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Singleton;

@Singleton
public class MatchRenderer implements ModelRenderer<Match, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_DEFAULT, RENDER_MATCH_INFO, RENDER_NOTIFICATION})
    public @interface RenderType{}
    public static final int RENDER_DEFAULT = 0;
    public static final int RENDER_MATCH_INFO = 1;
    public static final int RENDER_NOTIFICATION = 2;

    private final APICache mDatafeed;
    private final Resources mResources;

    public MatchRenderer(APICache datafeed, Resources resources) {
        mDatafeed = datafeed;
        mResources = resources;
    }

    @WorkerThread
    @Override
    public @Nullable MatchListElement renderFromKey(String key, ModelType type, Integer args) {
        Match match = mDatafeed.fetchMatch(key).toBlocking().first();
        if (match == null) {
            return null;
        }

        return renderFromModel(match, RENDER_DEFAULT);
    }

    /**
     * Renders a MatchListElement for displaying this match. ASSUMES 3v3 match structure with
     * red/blue alliances Use different render methods for other structures
     */
    @WorkerThread
    @Override
    public @Nullable MatchListElement renderFromModel(Match match, Integer renderMode) {
        RenderArgs args = argsFromMode(renderMode);
        JsonObject alliances;
        try {
            alliances = match.getAlliances();
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required field for match render: Database.Matches.ALLIANCES");
            return null;
        }
        JsonArray videos;
        try {
            videos = match.getVideos();
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required field for match render: Database.Matches.VIDEOS");
            videos = new JsonArray();
        }
        String key = match.getKey();
        if (key.isEmpty()) {
            return null;
        }

        JsonArray redTeams = Match.getRedTeams(alliances),
          blueTeams = Match.getBlueTeams(alliances);
        String redScore = Match.getRedAlliance(alliances).get("score").getAsString(),
          blueScore = Match.getBlueAlliance(alliances).get("score").getAsString();

        if (Integer.parseInt(redScore) < 0) redScore = "?";
        if (Integer.parseInt(blueScore) < 0) blueScore = "?";

        String youTubeVideoKey = null;
        for (int i = 0; i < videos.size(); i++) {
            JsonObject video = videos.get(i).getAsJsonObject();
            if (video.get("type").getAsString().equals("youtube")) {
                youTubeVideoKey = video.get("key").getAsString();
            }
        }

        String[] redAlliance, blueAlliance;
        // Add teams based on alliance size (or none if there isn't for some reason)
        if (redTeams.size() == 3) {
            redAlliance = new String[]{
              redTeams.get(0).getAsString().substring(3),
              redTeams.get(1).getAsString().substring(3),
              redTeams.get(2).getAsString().substring(3)};
        } else if (redTeams.size() == 2) {
            redAlliance = new String[]{
              redTeams.get(0).getAsString().substring(3),
              redTeams.get(1).getAsString().substring(3)};
        } else {
            redAlliance = new String[]{"", "", ""};
        }

        if (blueTeams.size() == 3) {
            blueAlliance = new String[]{
              blueTeams.get(0).getAsString().substring(3),
              blueTeams.get(1).getAsString().substring(3),
              blueTeams.get(2).getAsString().substring(3)};
        } else if (blueTeams.size() == 2) {
            blueAlliance = new String[]{
              blueTeams.get(0).getAsString().substring(3),
              blueTeams.get(1).getAsString().substring(3)};
        } else {
            blueAlliance = new String[]{"", "", ""};
        }

        long matchTime;
        try {
            matchTime = match.getTimeMillis();
        } catch (BasicModel.FieldNotDefinedException e) {
            matchTime = -1;
        }

        return new MatchListElement(youTubeVideoKey, match.getTitle(mResources, true),
          redAlliance, blueAlliance,
          redScore, blueScore,
          key, matchTime, match.getSelectedTeam(),
          args.showVideo, args.showHeaders, args.showMatchTitle, args.clickable);
    }

    @VisibleForTesting
    public static RenderArgs argsFromMode(@RenderType Integer type) {
        int nullSafeValue = type == null ? -1 : type;
        switch (nullSafeValue) {
            default:
            case RENDER_DEFAULT:
                /* Video icon, no header, yes title, yes clickable */
                return new RenderArgs(true, false, true, true);
            case RENDER_MATCH_INFO:
                /* Only show title (used in MatchInfo activity */
                return new RenderArgs(false, true, false, false);
            case RENDER_NOTIFICATION:
                /* Only be clickable - used in GameDay ticker notifications */
                return new RenderArgs(false, false, false, true);
        }
    }

    public static class RenderArgs {
        public final boolean showVideo;
        public final boolean showHeaders;
        public final boolean showMatchTitle;
        public final boolean clickable;

        public RenderArgs(
          boolean showVideo,
          boolean showHeaders,
          boolean showMatchTitle,
          boolean clickable) {
            this.showVideo = showVideo;
            this.showHeaders = showHeaders;
            this.showMatchTitle = showMatchTitle;
            this.clickable = clickable;
        }

    }
}
