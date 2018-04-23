package com.thebluealliance.androidclient.renderers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;
import com.thebluealliance.api.model.IMatchVideo;

import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.annotation.Nullable;
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
        @Nullable IMatchAlliancesContainer alliances = match.getAlliances();
        @Nullable List<IMatchVideo> videos = match.getVideos();

        String key = match.getKey();
        if (key.isEmpty()) {
            return null;
        }

        String redScore = (alliances == null)
                                ? "-1"
                                : Integer.toString(alliances.getRed().getScore());
        String blueScore = (alliances == null)
                                ? "-1"
                                : Integer.toString(alliances.getBlue().getScore());

        if (Integer.parseInt(redScore) < 0) redScore = "?";
        if (Integer.parseInt(blueScore) < 0) blueScore = "?";

        String youTubeVideoKey = null;
        if (videos != null) {
            for (IMatchVideo video : videos) {
                if ("youtube".equals(video.getType())) {
                    youTubeVideoKey = video.getKey();
                    break;
                }
            }
        }

        String[] redAlliance, blueAlliance;
        // Add teams based on alliance size (or none if there isn't for some reason)
        List<String> redTeams = (alliances != null)
                ? alliances.getRed().getTeamKeys()
                : null;
        if (redTeams != null && redTeams.size() == 3) {
            redAlliance = new String[]{
              redTeams.get(0).substring(3),
              redTeams.get(1).substring(3),
              redTeams.get(2).substring(3)};
        } else if (redTeams != null && redTeams.size() == 2) {
            redAlliance = new String[]{
              redTeams.get(0).substring(3),
              redTeams.get(1).substring(3)};
        } else {
            redAlliance = new String[]{"", "", ""};
        }

        List<String> blueTeams = (alliances != null)
                                ? alliances.getBlue().getTeamKeys()
                                : null;
        if (blueTeams != null && blueTeams.size() == 3) {
            blueAlliance = new String[]{
              blueTeams.get(0).substring(3),
              blueTeams.get(1).substring(3),
              blueTeams.get(2).substring(3)};
        } else if (blueTeams != null && blueTeams.size() == 2) {
            blueAlliance = new String[]{
              blueTeams.get(0).substring(3),
              blueTeams.get(1).substring(3)};
        } else {
            blueAlliance = new String[]{"", "", ""};
        }

        long matchTime = match.getTime() != null ? match.getTime() : -1;

        int redExtraRp = 0;
        int blueExtraRp = 0;

        if(match.getYear() >= 2016) {
            JsonObject scoreBreakdown = JSONHelper.getasJsonObject(match.getScoreBreakdown());
            JsonObject redScoreBreakdown = scoreBreakdown.get("red").getAsJsonObject();
            JsonObject blueScoreBreakdown = scoreBreakdown.get("blue").getAsJsonObject();
            String rpName1 = null;
            String rpName2 = null;
            switch(match.getYear()) {
                case 2016:
                    rpName1 = "teleopDefensesBreached";
                    rpName2 = "teleopTowerCaptured";
                    break;
                case 2017:
                    rpName1 = "kPaRankingPointAchieved";
                    rpName2 = "rotorRankingPointAchieved";
                    break;
                case 2018:
                    rpName1 = "autoQuestRankingPoint";
                    rpName2 = "faceTheBossRankingPoint";
                    break;
            }
            if (rpName1 != null) {
                if (redScoreBreakdown.get(rpName1).getAsBoolean()) {
                    redExtraRp++;
                }
                if (blueScoreBreakdown.get(rpName1).getAsBoolean()) {
                    blueExtraRp++;
                }
            }
            if (rpName2 != null) {
                if (redScoreBreakdown.get(rpName2).getAsBoolean()) {
                    redExtraRp++;
                }
                if (blueScoreBreakdown.get(rpName2).getAsBoolean()) {
                    blueExtraRp++;
                }
            }
        }

        return new MatchListElement(youTubeVideoKey, match.getTitle(mResources, true),
          redAlliance, blueAlliance,
          redScore, blueScore, match.getWinningAlliance(),
          key, matchTime, match.getSelectedTeam(),
          args.showVideo, args.showHeaders, args.showMatchTitle, args.clickable, redExtraRp,
                blueExtraRp);
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
