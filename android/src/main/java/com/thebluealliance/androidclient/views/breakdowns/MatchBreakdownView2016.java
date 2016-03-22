package com.thebluealliance.androidclient.views.breakdowns;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchBreakdownView2016 extends FrameLayout {

    private GridLayout breakdownContainer;
    private TextView
            red1,               blue1,
            red2,               blue2,
            red3,               blue3,
            redAutoBoulder,     blueAutoBoulder,
            redAutoReach,       blueAutoReach,
            redAutoCross,       blueAutoCross,
            redAutoTotal,       blueAutoTotal,
            redDefense1Cross,   blueDefense1Cross,
            redDefense2Name,    blueDefense2Name,
            redDefense2Cross,   blueDefense2Cross,
            redDefense3Name,    blueDefense3Name,
            redDefense3Cross,   blueDefense3Cross,
            redDefense4Name,    blueDefense4Name,
            redDefense4Cross,   blueDefense4Cross,
            redDefense5Name,    blueDefense5Name,
            redDefense5Cross,   blueDefense5Cross,
            redTeleopCross,     blueTeleopCross,
            redTeleBoulderHigh, blueTeleBoulderHigh,
            redTeleBoulderLow,  blueTeleBoulderLow,
            redTeleBoulder,     blueTeleBoulder,
            redTowerChallenge,  blueTowerChallenge,
            redTowerScale,      blueTowerScale,
            redTeleop,          blueTeleop,
            redBreach,          blueBreach,
            redCapture,         blueCapture,
            redFoul,            blueFoul,
            redAdjust,          blueAdjust,
            redTotal,           blueTotal,
            redRanking,         blueRanking;
    private ImageView
            redBreachIcon,      blueBreachIcon,
            redCaptureIcon,     blueCaptureIcon;

    public MatchBreakdownView2016(Context context) {
        super(context);
        init();
    }

    public MatchBreakdownView2016(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MatchBreakdownView2016(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Inflate the layout
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2016, this, true);

        breakdownContainer = (GridLayout) findViewById(R.id.breakdown2016_container);

        red1 = (TextView) findViewById(R.id.breakdown_red1);
        red2 = (TextView) findViewById(R.id.breakdown_red2);
        red3 = (TextView) findViewById(R.id.breakdown_red3);
        blue1 = (TextView) findViewById(R.id.breakdown_blue1);
        blue2 = (TextView) findViewById(R.id.breakdown_blue2);
        blue3 = (TextView) findViewById(R.id.breakdown_blue3);

        redAutoBoulder = (TextView) findViewById(R.id.breakdown_auto_boulder_red);
        blueAutoBoulder = (TextView) findViewById(R.id.breakdown_auto_boulder_blue);
        redAutoReach = (TextView) findViewById(R.id.breakdown_auto_reach_red);
        blueAutoReach = (TextView) findViewById(R.id.breakdown_auto_reach_blue);
        redAutoCross = (TextView) findViewById(R.id.breakdown_auto_cross_red);
        blueAutoCross = (TextView) findViewById(R.id.breakdown_auto_cross_blue);
        redAutoTotal = (TextView) findViewById(R.id.breakdown_auto_total_red);
        blueAutoTotal = (TextView) findViewById(R.id.breakdown_auto_total_blue);

        redDefense1Cross = (TextView) findViewById(R.id.breakdown_red_defense1_cross);
        blueDefense1Cross = (TextView) findViewById(R.id.breakdown_blue_defense1_cross);

        redDefense2Name = (TextView) findViewById(R.id.breakdown_red_defense2_name);
        redDefense2Cross = (TextView) findViewById(R.id.breakdown_red_defense2_cross);
        blueDefense2Name = (TextView) findViewById(R.id.breakdown_blue_defense2_name);
        blueDefense2Cross = (TextView) findViewById(R.id.breakdown_blue_defense2_cross);

        redDefense3Name = (TextView) findViewById(R.id.breakdown_red_defense3_name);
        redDefense3Cross = (TextView) findViewById(R.id.breakdown_red_defense3_cross);
        blueDefense3Name = (TextView) findViewById(R.id.breakdown_blue_defense3_name);
        blueDefense3Cross = (TextView) findViewById(R.id.breakdown_blue_defense3_cross);

        redDefense4Name = (TextView) findViewById(R.id.breakdown_red_defense4_name);
        redDefense4Cross = (TextView) findViewById(R.id.breakdown_red_defense4_cross);
        blueDefense4Name = (TextView) findViewById(R.id.breakdown_blue_defense4_name);
        blueDefense4Cross = (TextView) findViewById(R.id.breakdown_blue_defense4_cross);

        redDefense5Name = (TextView) findViewById(R.id.breakdown_red_defense5_name);
        redDefense5Cross = (TextView) findViewById(R.id.breakdown_red_defense5_cross);
        blueDefense5Name = (TextView) findViewById(R.id.breakdown_blue_defense5_name);
        blueDefense5Cross = (TextView) findViewById(R.id.breakdown_blue_defense5_cross);

        redTeleopCross = (TextView) findViewById(R.id.breakdown_teleop_cross_red);
        blueTeleopCross = (TextView) findViewById(R.id.breakdown_teleop_cross_blue);
        redTeleBoulderHigh = (TextView) findViewById(R.id.breakdown_teleop_high_boulder_red);
        blueTeleBoulderHigh = (TextView) findViewById(R.id.breakdown_teleop_high_boulder_blue);
        redTeleBoulderLow = (TextView) findViewById(R.id.breakdown_teleop_low_boulder_red);
        blueTeleBoulderLow = (TextView) findViewById(R.id.breakdown_teleop_low_boulder_blue);
        redTeleBoulder = (TextView) findViewById(R.id.breakdown_teleop_total_boulder_red);
        blueTeleBoulder = (TextView) findViewById(R.id.breakdown_teleop_total_boulder_blue);
        redTowerChallenge = (TextView) findViewById(R.id.breakdown_teleop_challenge_red);
        blueTowerChallenge = (TextView) findViewById(R.id.breakdown_teleop_challenge_blue);
        redTowerScale = (TextView) findViewById(R.id.breakdown_teleop_scale_red);
        blueTowerScale = (TextView) findViewById(R.id.breakdown_teleop_scale_blue);
        redTeleop = (TextView) findViewById(R.id.breakdown_teleop_total_red);
        blueTeleop = (TextView) findViewById(R.id.breakdown_teleop_total_blue);

        redBreachIcon = (ImageView) findViewById(R.id.breakdown2016_breach_icon_red);
        blueBreachIcon = (ImageView) findViewById(R.id.breakdown2016_breach_icon_blue);
        redBreach = (TextView) findViewById(R.id.breakdown2016_breach_points_red);
        blueBreach = (TextView) findViewById(R.id.breakdown2016_breach_points_blue);
        redCaptureIcon = (ImageView) findViewById(R.id.breakdown2016_capture_icon_red);
        blueCaptureIcon = (ImageView) findViewById(R.id.breakdown2016_capture_icon_blue);
        redCapture = (TextView) findViewById(R.id.breakdown2016_capture_points_red);
        blueCapture = (TextView) findViewById(R.id.breakdown2016_capture_points_blue);
        redFoul = (TextView) findViewById(R.id.breakdown_fouls_red);
        blueFoul = (TextView) findViewById(R.id.breakdown_fouls_blue);
        redAdjust = (TextView) findViewById(R.id.breakdown_adjust_red);
        blueAdjust = (TextView) findViewById(R.id.breakdown_adjust_blue);
        redTotal = (TextView) findViewById(R.id.breakdown_total_red);
        blueTotal = (TextView) findViewById(R.id.breakdown_total_blue);
        redRanking = (TextView) findViewById(R.id.breakdown2016_red_rp);
        blueRanking = (TextView) findViewById(R.id.breakdown2016_blue_rp);
    }

    public void initWithData(MatchType matchType, JsonObject allianceData, JsonObject scoredata) {
        if (scoredata == null || scoredata.entrySet().isEmpty()
                || allianceData == null || !allianceData.has("red") || !allianceData.has("blue")) {
            breakdownContainer.setVisibility(GONE);
            return;
        }

        int redRp = 0;
        int blueRp = 0;
        JsonObject redAlliance = allianceData.get("red").getAsJsonObject();
        JsonArray redTeams = redAlliance.get("teams").getAsJsonArray();
        JsonObject blueAlliance = allianceData.get("blue").getAsJsonObject();
        JsonArray blueTeams = blueAlliance.get("teams").getAsJsonArray();
        JsonObject redData = scoredata.get("red").getAsJsonObject();
        JsonObject blueData = scoredata.get("blue").getAsJsonObject();

        red1.setText(teamNumberFromKey(redTeams.get(0).getAsString()));
        red2.setText(teamNumberFromKey(redTeams.get(1).getAsString()));
        red3.setText(teamNumberFromKey(redTeams.get(2).getAsString()));

        blue1.setText(teamNumberFromKey(blueTeams.get(0).getAsString()));
        blue2.setText(teamNumberFromKey(blueTeams.get(1).getAsString()));
        blue3.setText(teamNumberFromKey(blueTeams.get(2).getAsString()));

        redAutoBoulder.setText(getAutoBoulder(redData));
        blueAutoBoulder.setText(getAutoBoulder(blueData));
        redAutoReach.setText(getIntDefault(redData, "autoReachPoints"));
        blueAutoReach.setText(getIntDefault(blueData, "autoReachPoints"));
        redAutoCross.setText(getIntDefault(redData, "autoCrossingPoints"));
        blueAutoCross.setText(getIntDefault(blueData, "autoCrossingPoints"));
        redAutoTotal.setText(getIntDefault(redData, "autoPoints"));
        blueAutoTotal.setText(getIntDefault(blueData, "autoPoints"));

        redDefense1Cross.setText(getCrossValue(redData, "position1crossings"));
        blueDefense1Cross.setText(getCrossValue(blueData, "position1crossings"));

        redDefense2Name.setText(getDefenseName(redData, "position2"));
        redDefense2Cross.setText(getCrossValue(redData, "position2crossings"));
        blueDefense2Name.setText(getDefenseName(blueData, "position2"));
        blueDefense2Cross.setText(getCrossValue(blueData, "position2crossings"));

        redDefense3Name.setText(getDefenseName(redData, "position3"));
        redDefense3Cross.setText(getCrossValue(redData, "position3crossings"));
        blueDefense3Name.setText(getDefenseName(blueData, "position3"));
        blueDefense3Cross.setText(getCrossValue(blueData, "position3crossings"));

        redDefense4Name.setText(getDefenseName(redData, "position4"));
        redDefense4Cross.setText(getCrossValue(redData, "position4crossings"));
        blueDefense4Name.setText(getDefenseName(blueData, "position4"));
        blueDefense4Cross.setText(getCrossValue(blueData, "position4crossings"));

        redDefense5Name.setText(getDefenseName(redData, "position5"));
        redDefense5Cross.setText(getCrossValue(redData, "position5crossings"));
        blueDefense5Name.setText(getDefenseName(blueData, "position5"));
        blueDefense5Cross.setText(getCrossValue(blueData, "position5crossings"));

        redTeleopCross.setText(getIntDefault(redData, "teleopCrossingPoints"));
        blueTeleopCross.setText(getIntDefault(blueData, "teleopCrossingPoints"));
        redTeleBoulderHigh.setText(getIntDefault(redData, "teleopBouldersHigh"));
        blueTeleBoulderHigh.setText(getIntDefault(blueData, "teleopBouldersHigh"));
        redTeleBoulderLow.setText(getIntDefault(redData, "teleopBouldersLow"));
        blueTeleBoulderLow.setText(getIntDefault(blueData, "teleopBouldersLow"));
        redTeleBoulder.setText(getIntDefault(redData, "teleopBoulderPoints"));
        blueTeleBoulder.setText(getIntDefault(blueData, "teleopBoulderPoints"));
        redTowerChallenge.setText(getIntDefault(redData, "teleopChallengePoints"));
        blueTowerChallenge.setText(getIntDefault(blueData, "teleopChallengePoints"));
        redTowerScale.setText(getIntDefault(redData, "teleopScalePoints"));
        blueTowerScale.setText(getIntDefault(blueData, "teleopScalePoints"));
        redTeleop.setText(getTeleopTotal(redData));
        blueTeleop.setText(getTeleopTotal(blueData));

        boolean redBreachSuccess = getBooleanDefault(redData, "teleopDefensesBreached");
        redBreachIcon.setBackgroundResource(redBreachSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);

        boolean blueBreachSuccess = getBooleanDefault(blueData, "teleopDefensesBreached");
        blueBreachIcon.setBackgroundResource(blueBreachSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);

        int redBreachPoints = getIntDefaultValue(redData, "breachPoints");
        if (redBreachSuccess && matchType == MatchType.QUAL) {
            redRp++;
            redBreach.setText(getContext().getString(R.string.breakdown2016_rp_format, 1));
            redBreach.setVisibility(VISIBLE);
        } else if (redBreachPoints > 0) {
            redBreach.setText(getContext().getString(R.string.breakdown2016_addition_format, redBreachPoints));
            redBreach.setVisibility(VISIBLE);
        } else {
            redBreach.setVisibility(GONE);
        }

        int blueBreachPoints = getIntDefaultValue(blueData, "breachPoints");
        if (blueBreachSuccess && matchType == MatchType.QUAL) {
            blueRp++;
            blueBreach.setText(getContext().getString(R.string.breakdown2016_rp_format, 1));
            blueBreach.setVisibility(VISIBLE);
        } else if (blueBreachPoints > 0) {
            blueBreach.setText(getContext().getString(R.string.breakdown2016_addition_format, blueBreachPoints));
            blueBreach.setVisibility(VISIBLE);
        } else {
            blueBreach.setVisibility(GONE);
        }

        boolean redCaptureSuccess = getBooleanDefault(redData, "teleopTowerCaptured");
        redCaptureIcon.setBackgroundResource(redCaptureSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);
        boolean blueCaptureSuccess = getBooleanDefault(blueData, "teleopTowerCaptured");
        blueCaptureIcon.setBackgroundResource(blueCaptureSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);

        int redCapturePoints = getIntDefaultValue(redData, "capturePoints");
        if (redCaptureSuccess && matchType == MatchType.QUAL) {
            redRp++;
            redCapture.setText(getContext().getString(R.string.breakdown2016_rp_format, 1));
            redCapture.setVisibility(VISIBLE);
        } else if (redCapturePoints > 0) {
            redCapture.setText(getContext().getString(R.string.breakdown2016_addition_format, redCapturePoints));
            redCapture.setVisibility(VISIBLE);
        } else {
            redCapture.setVisibility(GONE);
        }

        int blueCapturePoints = getIntDefaultValue(blueData, "capturePoints");
        if (blueCaptureSuccess && matchType == MatchType.QUAL) {
            blueRp++;
            blueCapture.setText(getContext().getString(R.string.breakdown2016_rp_format, 1));
            blueCapture.setVisibility(VISIBLE);
        } else if (blueCapturePoints > 0) {
            blueCapture.setText(getContext().getString(R.string.breakdown2016_addition_format, blueCapturePoints));
            blueCapture.setVisibility(VISIBLE);
        } else {
            blueCapture.setVisibility(GONE);
        }

        redFoul.setText(getContext().getString(R.string.breakdown2016_foul_format, getIntDefaultValue(redData, "foulPoints")));
        blueFoul.setText(getContext().getString(R.string.breakdown2016_foul_format, getIntDefaultValue(blueData, "foulPoints")));
        redAdjust.setText(getIntDefault(redData, "adjustPoints"));
        blueAdjust.setText(getIntDefault(blueData, "adjustPoints"));
        redTotal.setText(getIntDefault(redData, "totalPoints"));
        blueTotal.setText(getIntDefault(blueData, "totalPoints"));

        int redScore = getIntDefaultValue(redAlliance, "score");
        int blueScore = getIntDefaultValue(blueAlliance, "score");
        if (redScore > blueScore) {
            redRp += 2;
        } else if (blueScore > redScore) {
            blueRp += 2;
        } else {
            redRp++;
            blueRp++;
        }

        if (matchType == MatchType.QUAL) {
            redRanking.setText(getContext().getString(R.string.breakdown2016_total_rp, redRp));
            blueRanking.setText(getContext().getString(R.string.breakdown2016_total_rp, blueRp));
        } else {
            redRanking.setVisibility(GONE);
            blueRanking.setVisibility(GONE);
            findViewById(R.id.breakdown2016_rp_header).setVisibility(GONE);
        }
    }

    private static String getIntDefault(JsonObject data, String key) {
        if (data.has(key)) {
            return data.get(key).getAsString();
        } else {
            return "0";
        }
    }

    private static int getIntDefaultValue(JsonObject data, String key) {
        if (data.has(key)) {
            return data.get(key).getAsInt();
        } else {
            return 0;
        }
    }
    private static boolean getBooleanDefault(JsonObject data, String key) {
        return data.has(key) && data.get(key).getAsBoolean();
    }

    private static String getTeleopTotal(JsonObject data) {
        return Integer.toString(getIntDefaultValue(data, "teleopCrossingPoints")
                + getIntDefaultValue(data, "teleopBoulderPoints")
                + getIntDefaultValue(data, "teleopChallengePoints")
                + getIntDefaultValue(data, "teleopScalePoints"));
    }

    private static String getAutoBoulder(JsonObject data) {
        return Integer.toString(getIntDefaultValue(data, "autoBouldersHigh")
                + getIntDefaultValue(data, "autoBouldersLow"));
    }

    private String getCrossValue(JsonObject data, String key) {
        int crossCount = getIntDefaultValue(data, key);
        return getContext().getString(R.string.breakdown2016_cross_format, crossCount);
    }

    private static @StringRes int getDefenseName(JsonObject data, String key) {
        if (data.has(key)) {
            String name = data.get(key).getAsString();
            switch(name) {
                case "A_ChevalDeFrise": return R.string.defense2016_cdf;
                case "A_Portcullis": return R.string.defense2016_portcullis;
                case "B_Ramparts": return R.string.defense2016_ramparts;
                case "B_Moat": return R.string.defense2016_moat;
                case "C_SallyPort": return R.string.defense2016_sally_port;
                case "C_Drawbridge": return R.string.defense2016_drawbridge;
                case "D_RoughTerrain": return R.string.defense2016_rough_terrain;
                case "D_RockWall": return R.string.defense2016_rock_wall;
                default: return R.string.defense2016_unknown;
            }
        }
        return R.string.defense2016_unknown;
    }

    private static String teamNumberFromKey(String teamKey) {
        return teamKey.substring(3);
    }
}
