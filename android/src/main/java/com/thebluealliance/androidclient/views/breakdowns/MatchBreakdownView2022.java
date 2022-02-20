package com.thebluealliance.androidclient.views.breakdowns;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Map;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

public class MatchBreakdownView2022 extends AbstractMatchBreakdownView {
    @BindView(R.id.breakdown2022_container)                     GridLayout breakdownContainer;

    @BindView(R.id.breakdown_red1)                              TextView red1;
    @BindView(R.id.breakdown_blue1)                             TextView blue1;
    @BindView(R.id.breakdown_red2)                              TextView red2;
    @BindView(R.id.breakdown_blue2)                             TextView blue2;
    @BindView(R.id.breakdown_red3)                              TextView red3;
    @BindView(R.id.breakdown_blue3)                             TextView blue3;

    @BindView(R.id.breakdown2022_red_auto_taxi_robot1)          ImageView red1Taxi;
    @BindView(R.id.breakdown2022_red_auto_taxi_robot2)          ImageView red2Taxi;
    @BindView(R.id.breakdown2022_red_auto_taxi_robot3)          ImageView red3Taxi;
    @BindView(R.id.breakdown2022_red_auto_taxi_bonus)           TextView  redTaxiBonus;
    @BindView(R.id.breakdown2022_blue_auto_taxi_robot1)         ImageView blue1Taxi;
    @BindView(R.id.breakdown2022_blue_auto_taxi_robot2)         ImageView blue2Taxi;
    @BindView(R.id.breakdown2022_blue_auto_taxi_robot3)         ImageView blue3Taxi;
    @BindView(R.id.breakdown2022_blue_auto_taxi_bonus)          TextView  blueTaxiBonus;

    @BindView(R.id.breakdown2022_red_auto_lower_hub)            TextView redAutoLowerHub;
    @BindView(R.id.breakdown2022_red_auto_upper_hub)            TextView redAutoUpperHub;
    @BindView(R.id.breakdown2022_blue_auto_lower_hub)           TextView blueAutoLowerHub;
    @BindView(R.id.breakdown2022_blue_auto_upper_hub)           TextView blueAutoUpperHub;
    @BindView(R.id.breakdown2022_red_auto_cargo)                TextView redAutoCargo;
    @BindView(R.id.breakdown2022_blue_auto_cargo)               TextView blueAutoCargo;

    @BindView(R.id.breakdown2022_red_quintet)                   ImageView redQuintet;
    @BindView(R.id.breakdown2022_blue_quintet)                  ImageView blueQuintet;

    @BindView(R.id.breakdown_auto_total_red)                    TextView redAutoTotal;
    @BindView(R.id.breakdown_auto_total_blue)                   TextView blueAutoTotal;

    @BindView(R.id.breakdown2022_red_teleop_lower_hub)          TextView redTelopLowerHub;
    @BindView(R.id.breakdown2022_red_teleop_upper_hub)          TextView redTeleopUpperHub;
    @BindView(R.id.breakdown2022_blue_teleop_lower_hub)         TextView blueTeleopLowerHub;
    @BindView(R.id.breakdown2022_blue_teleop_upper_hub)         TextView blueTeleopUpperHub;
    @BindView(R.id.breakdown2022_red_teleop_cargo)              TextView redTeleopCargo;
    @BindView(R.id.breakdown2022_blue_teleop_cargo)             TextView blueTeleopCargo;

    @BindView(R.id.breakdown2022_red_endgame_robot1)            TextView redEndgameRobot1;
    @BindView(R.id.breakdown2022_red_endgame_robot2)            TextView redEndgameRobot2;
    @BindView(R.id.breakdown2022_red_endgame_robot3)            TextView redEndgameRobot3;
    @BindView(R.id.breakdown2022_blue_endgame_robot1)           TextView blueEndgameRobot1;
    @BindView(R.id.breakdown2022_blue_endgame_robot2)           TextView blueEndgameRobot2;
    @BindView(R.id.breakdown2022_blue_endgame_robot3)           TextView blueEndgameRobot3;
    @BindView(R.id.breakdown_endgame_total_red)                 TextView redEndgameTotal;
    @BindView(R.id.breakdown_endgame_total_blue)                TextView blueEndgameTotal;

    @BindView(R.id.breakdown_teleop_total_red)                  TextView redTeleopTotal;
    @BindView(R.id.breakdown_teleop_total_blue)                 TextView blueTeleopTotal;

    @BindView(R.id.breakdown2022_red_cargo_bonus)               TextView redCargoBonue;
    @BindView(R.id.breakdown2022_blue_cargo_bonus)              TextView blueCargoBonus;

    @BindView(R.id.breakdown2022_red_hangar_bonus)              TextView redHangarBonus;
    @BindView(R.id.breakdown2022_blue_hangar_bonus)             TextView blueHangarBonus;

    @BindView(R.id.breakdown_fouls_red)                         TextView foulsRed;
    @BindView(R.id.breakdown_fouls_blue)                        TextView foulsBlue;
    @BindView(R.id.breakdown_adjust_red)                        TextView adjustRed;
    @BindView(R.id.breakdown_adjust_blue)                       TextView adjustBlue;
    @BindView(R.id.breakdown_total_red)                         TextView totalRed;
    @BindView(R.id.breakdown_total_blue)                        TextView totalBlue;
    @BindView(R.id.breakdown_red_rp)                            TextView rpRed;
    @BindView(R.id.breakdown_blue_rp)                           TextView rpBlue;
    @BindView(R.id.breakdown_rp_header)                         TextView rpHeader;

    private Resources mResources;

    private static final @NonNull Map<String, Integer> ENDGAME_POINTS = ImmutableMap.of(
            "Low", 4,
            "Mid", 6,
            "High", 10,
            "Traversal", 15);

    public MatchBreakdownView2022(Context context) {
        super(context);
    }

    public MatchBreakdownView2022(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2022(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2022, this, true);
        ButterKnife.bind(this);
    }


    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            breakdownContainer.setVisibility(GONE);
            return false;
        }

        /* Resources */
        mResources = getResources();

        /* Alliance data */
        List<String> redTeams = allianceData.getRed().getTeamKeys();
        List<String> blueTeams = allianceData.getBlue().getTeamKeys();
        JsonObject redData = scoreData.get("red").getAsJsonObject();
        JsonObject blueData = scoreData.get("blue").getAsJsonObject();

        /* Red Teams */
        red1.setText(teamNumberFromKey(redTeams.get(0)));
        red2.setText(teamNumberFromKey(redTeams.get(1)));
        red3.setText(teamNumberFromKey(redTeams.get(2)));

        /* Blue Teams */
        blue1.setText(teamNumberFromKey(blueTeams.get(0)));
        blue2.setText(teamNumberFromKey(blueTeams.get(1)));
        blue3.setText(teamNumberFromKey(blueTeams.get(2)));

        /* Auto Initiation Line*/
        setAutoTaxi(redData, redTaxiBonus, red1Taxi, red2Taxi, red3Taxi);
        setAutoTaxi(blueData, blueTaxiBonus, blue1Taxi, blue2Taxi, blue3Taxi);

        /* Auto Cargo */
        setCargo(redData, "auto", redAutoLowerHub, redAutoUpperHub, redAutoCargo);
        setCargo(blueData, "auto", blueAutoLowerHub, blueAutoUpperHub, blueAutoCargo);

        /* Auto Quintet */
        setQuintet(redData, redQuintet);
        setQuintet(blueData, blueQuintet);

        /* Total Auto Points */
        redAutoTotal.setText(getIntDefault(redData, "autoPoints"));
        blueAutoTotal.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Cargo */
        setCargo(redData, "teleop", redTelopLowerHub, redTeleopUpperHub, redTeleopCargo);
        setCargo(blueData, "teleop", blueTeleopLowerHub, blueTeleopUpperHub, blueTeleopCargo);

        /* Endgame */
        setEndgame(redData, redEndgameTotal, redEndgameRobot1, redEndgameRobot2, redEndgameRobot3);
        setEndgame(blueData, blueEndgameTotal, blueEndgameRobot1, blueEndgameRobot2, blueEndgameRobot3);

        /* Teleop Total */
        redTeleopTotal.setText(getIntDefault(redData, "teleopPoints"));
        blueTeleopTotal.setText(getIntDefault(blueData, "teleopPoints"));

        /* Cargo Bonus */
        setBonus(redData, "cargo", redCargoBonue);
        setBonus(blueData, "cargo", blueCargoBonus);

        /* Hangar Bonus */
        setBonus(redData, "hangar", redHangarBonus);
        setBonus(blueData, "hangar", blueHangarBonus);

        /* Fouls */
        setFouls(redData, blueData, foulsRed);
        setFouls(blueData, redData, foulsBlue);

        /* Adjustment points */
        adjustRed.setText(getIntDefault(redData, "adjustPoints"));
        adjustBlue.setText(getIntDefault(blueData, "adjustPoints"));

        /* Total Points */
        totalRed.setText(getIntDefault(redData, "totalPoints"));
        totalBlue.setText(getIntDefault(blueData, "totalPoints"));

        /* Show RPs earned, if needed */
        if (!matchType.isPlayoff()) {
            rpRed.setText(mResources.getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "rp")));
            rpBlue.setText(mResources.getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "rp")));
        } else {
            rpRed.setVisibility(GONE);
            rpBlue.setVisibility(GONE);
            rpHeader.setVisibility(GONE);
        }

        breakdownContainer.setVisibility(View.VISIBLE);

        return true;
    }

    private void setAutoTaxi(JsonObject allianceData,
                             TextView bonusView,
                             ImageView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String taxiAchieved = getIntDefault(allianceData, "taxiRobot" + robotNumber);
            ImageView teamIcon = robotStatus[robotNumber - 1];
            if ("Yes".equals(taxiAchieved)) {
                teamIcon.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                teamIcon.setImageResource(R.drawable.ic_close_black_24dp);
            }
        }

        int taxiPoints = getIntDefaultValue(allianceData, "autoTaxiPoints");
        if (taxiPoints > 0) {
            bonusView.setVisibility(VISIBLE);
            bonusView.setText(mResources.getString(R.string.breakdown_addition_format, taxiPoints));
        } else {
            bonusView.setVisibility(GONE);
        }
    }

    private void setCargo(JsonObject allianceData,
                          String prefix,
                          TextView lowerHub,
                          TextView upperHub,
                          TextView totalPoints) {
        int lowerHubTotal = getIntDefaultValue(allianceData, prefix + "CargoLowerNear") +
                getIntDefaultValue(allianceData, prefix + "CargoLowerFar") +
                getIntDefaultValue(allianceData, prefix + "CargoLowerBlue") +
                getIntDefaultValue(allianceData, prefix + "CargoLowerRed");
        lowerHub.setText(Integer.toString(lowerHubTotal));

        int upperHubTotal = getIntDefaultValue(allianceData, prefix + "CargoUpperNear") +
                getIntDefaultValue(allianceData, prefix + "CargoUpperFar") +
                getIntDefaultValue(allianceData, prefix + "CargoUpperBlue") +
                getIntDefaultValue(allianceData, prefix + "CargoUpperRed");
        upperHub.setText(Integer.toString(upperHubTotal));

        totalPoints.setText(getIntDefault(allianceData, prefix + "CargoPoints"));
    }

    private void setQuintet(JsonObject allianceData, ImageView quintetView) {
        boolean isAchieved = getBooleanDefault(allianceData, "quintetAchieved");
        if (isAchieved) {
            quintetView.setBackgroundResource(R.drawable.ic_check_black_24dp);
        } else {
            quintetView.setBackgroundResource(R.drawable.ic_close_black_24dp);
        }
    }

    private void setEndgame(JsonObject allianceData, TextView endgameTotal, TextView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String endgameStatus = getIntDefault(allianceData, "endgameRobot" + robotNumber);
            Integer endgamePoints =  ENDGAME_POINTS.containsKey(endgameStatus) ? ENDGAME_POINTS.get(endgameStatus) : 0;
            TextView teamView = robotStatus[robotNumber - 1];
            if (endgamePoints != null && endgamePoints > 0) {
                teamView.setVisibility(VISIBLE);
                teamView.setText(mResources.getString(R.string.breakdown_string_with_addition_format, endgameStatus, endgamePoints));
            } else {
                teamView.setVisibility(VISIBLE);
                teamView.setText(endgameStatus);
            }
        }

        endgameTotal.setText(getIntDefault(allianceData, "endgamePoints"));
    }

    private void setBonus(JsonObject allianceData, String bonusName, TextView view) {
        boolean isAchieved = getBooleanDefault(allianceData, bonusName + "BonusRankingPoint");
        if (isAchieved) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_black_24dp, 0, 0, 0);
            view.setText(mResources.getString(R.string.breakdown_rp_format, 1));
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_black_24dp, 0, 0, 0);
            view.setText("");
        }
    }

    private void setFouls(JsonObject allianceData, JsonObject otherAllianceData, TextView view) {
        int foulPoints = getIntDefaultValue(otherAllianceData, "foulCount") * 3;
        int techFoulPoints = getIntDefaultValue(otherAllianceData, "techFoulCount") * 15;
        boolean foulRpAwarded = getBooleanDefault(allianceData, "tba_shieldEnergizedRankingPointFromFoul");
        if (foulRpAwarded) {
            view.setText(mResources.getString(R.string.breakdown_foul_tech_rp_format, foulPoints, techFoulPoints, 1));
        } else {
            view.setText(mResources.getString(R.string.breakdown_foul_tech_format, foulPoints, techFoulPoints));
        }
    }
}
