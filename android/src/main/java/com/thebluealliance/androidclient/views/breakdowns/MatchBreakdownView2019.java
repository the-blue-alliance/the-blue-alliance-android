package com.thebluealliance.androidclient.views.breakdowns;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

public class MatchBreakdownView2019 extends AbstractMatchBreakdownView {
    @Bind(R.id.breakdown2019_container)                     GridLayout breakdownContainer;

    @Bind(R.id.breakdown_red1)                              TextView red1;
    @Bind(R.id.breakdown_blue1)                             TextView blue1;
    @Bind(R.id.breakdown_red2)                              TextView red2;
    @Bind(R.id.breakdown_blue2)                             TextView blue2;
    @Bind(R.id.breakdown_red3)                              TextView red3;
    @Bind(R.id.breakdown_blue3)                             TextView blue3;

    @Bind(R.id.breakdown2019_red_sandstorm_bonus_robot_1)   TextView redSandstormBonusRobot1;
    @Bind(R.id.breakdown2019_blue_sandstorm_bonus_robot_1)  TextView blueSandstormBonusRobot1;
    @Bind(R.id.breakdown2019_red_sandstorm_bonus_robot_2)   TextView redSandstormBonusRobot2;
    @Bind(R.id.breakdown2019_blue_sandstorm_bonus_robot_2)  TextView blueSandstormBonusRobot2;
    @Bind(R.id.breakdown2019_red_sandstorm_bonus_robot_3)   TextView redSandstormBonusRobot3;
    @Bind(R.id.breakdown2019_blue_sandstorm_bonus_robot_3)  TextView blueSandstormBonusRobot3;
    @Bind(R.id.breakdown2019_sandstorm_total_red)           TextView redSandstormTotal;
    @Bind(R.id.breakdown2019_sandstorm_total_blue)          TextView blueSandstormTotal;


    @Bind(R.id.breakdown2019_red_cargo_ship_panels)         TextView redCargoShipPanels;
    @Bind(R.id.breakdown2019_red_cargo_ship_null_panels)    TextView redCargoShipNullPanels;
    @Bind(R.id.breakdown2019_red_cargo_ship_cargo)          TextView redCargoShipCargo;
    @Bind(R.id.breakdown2019_red_rocket_1_panels)           TextView redRocket1Panels;
    @Bind(R.id.breakdown2019_red_rocket_1_cargo)            TextView redRocket1Cargo;
    @Bind(R.id.breakdown2019_red_rocket_2_panels)           TextView redRocket2Panels;
    @Bind(R.id.breakdown2019_red_rocket_2_cargo)            TextView redRocket2Cargo;

    @Bind(R.id.breakdown2019_blue_cargo_ship_panels)        TextView blueCargoShipPanels;
    @Bind(R.id.breakdown2019_blue_cargo_ship_null_panels)   TextView blueCargoShipNullPanels;
    @Bind(R.id.breakdown2019_blue_cargo_ship_cargo)         TextView blueCargoShipCargo;
    @Bind(R.id.breakdown2019_blue_rocket_1_panels)          TextView blueRocket1Panels;
    @Bind(R.id.breakdown2019_blue_rocket_1_cargo)           TextView blueRocket1Cargo;
    @Bind(R.id.breakdown2019_blue_rocket_2_panels)          TextView blueRocket2Panels;
    @Bind(R.id.breakdown2019_blue_rocket_2_cargo)           TextView blueRocket2Cargo;

    @Bind(R.id.breakdown2019_red_hatch_panel_points)        TextView redHatchPanelPoints;
    @Bind(R.id.breakdown2019_blue_hatch_panel_points)       TextView blueHatchPanelPoints;
    @Bind(R.id.breakdown2019_red_cargo_points)              TextView redCargoPoints;
    @Bind(R.id.breakdown2019_blue_cargo_points)             TextView blueCargoPoints;

    @Bind(R.id.breakdown2019_red_hab_robot_1)               TextView redHabRobot1;
    @Bind(R.id.breakdown2019_blue_hab_robot_1)              TextView blueHabRobot1;
    @Bind(R.id.breakdown2019_red_hab_robot_2)               TextView redHabRobot2;
    @Bind(R.id.breakdown2019_blue_hab_robot_2)              TextView blueHabRobot2;
    @Bind(R.id.breakdown2019_red_hab_robot_3)               TextView redHabRobot3;
    @Bind(R.id.breakdown2019_blue_hab_robot_3)              TextView blueHabRobot3;
    @Bind(R.id.breakdown2019_hab_total_red)                 TextView redHabTotal;
    @Bind(R.id.breakdown2019_hab_total_blue)                TextView blueHabTotal;

    @Bind(R.id.breakdown_teleop_total_red)                  TextView redTeleopTotal;
    @Bind(R.id.breakdown_teleop_total_blue)                 TextView blueTeleopTotal;

    @Bind(R.id.breakdown2019_red_complete_rocket)           TextView redCompleteRocket;
    @Bind(R.id.breakdown2019_blue_complete_rocket)          TextView blueCompleteRocket;
    @Bind(R.id.breakdown2019_red_hab_docking)               TextView redHabDocking;
    @Bind(R.id.breakdown2019_blue_hab_docking)              TextView blueHabDocking;

    @Bind(R.id.breakdown_fouls_red)                         TextView foulsRed;
    @Bind(R.id.breakdown_fouls_blue)                        TextView foulsBlue;
    @Bind(R.id.breakdown_adjust_red)                        TextView adjustRed;
    @Bind(R.id.breakdown_adjust_blue)                       TextView adjustBlue;
    @Bind(R.id.breakdown_total_red)                         TextView totalRed;
    @Bind(R.id.breakdown_total_blue)                        TextView totalBlue;
    @Bind(R.id.breakdown_red_rp)                            TextView rpRed;
    @Bind(R.id.breakdown_blue_rp)                           TextView rpBlue;
    @Bind(R.id.breakdown_rp_header)                         TextView rpHeader;

    private Resources mResources;

    public MatchBreakdownView2019(Context context) {
        super(context);
    }

    public MatchBreakdownView2019(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2019(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2019, this, true);
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

        /* Sandstorm Bonus */
        setSandstormBonus(redData, "1", redSandstormBonusRobot1);
        setSandstormBonus(redData, "2", redSandstormBonusRobot2);
        setSandstormBonus(redData, "3", redSandstormBonusRobot3);
        setSandstormBonus(blueData, "1", blueSandstormBonusRobot1);
        setSandstormBonus(blueData, "2", blueSandstormBonusRobot2);
        setSandstormBonus(blueData, "3", blueSandstormBonusRobot3);
        blueSandstormTotal.setText(getIntDefault(blueData, "sandStormBonusPoints"));
        redSandstormTotal.setText(getIntDefault(redData, "sandStormBonusPoints"));

        /* Scoring Objects */
        setCargoShipScoring(redData, redCargoShipNullPanels, redCargoShipPanels, redCargoShipCargo);
        setRocketScoring(redData, "Near", redRocket1Panels, redRocket1Cargo);
        setRocketScoring(redData, "Far", redRocket2Panels, redRocket2Cargo);

        setCargoShipScoring(blueData, blueCargoShipNullPanels, blueCargoShipPanels, blueCargoShipCargo);
        setRocketScoring(blueData, "Near", blueRocket1Panels, blueRocket1Cargo);
        setRocketScoring(blueData, "Far", blueRocket2Panels, blueRocket2Cargo);

        setTotalPanels(redData, redHatchPanelPoints);
        setTotalCargo(redData, redCargoPoints);
        setTotalPanels(blueData, blueHatchPanelPoints);
        setTotalCargo(blueData, blueCargoPoints);

        /* HAB Climb */
        setHabClimbBonus(redData, "1", redHabRobot1);
        setHabClimbBonus(redData, "2", redHabRobot2);
        setHabClimbBonus(redData, "3", redHabRobot3);
        redHabTotal.setText(getIntDefault(redData, "habClimbPoints"));

        setHabClimbBonus(blueData, "1", blueHabRobot1);
        setHabClimbBonus(blueData, "2", blueHabRobot2);
        setHabClimbBonus(blueData, "3", blueHabRobot3);
        blueHabTotal.setText(getIntDefault(blueData, "habClimbPoints"));

        /* Teleop Total Points */
        redTeleopTotal.setText(getIntDefault(redData, "teleopPoints"));
        blueTeleopTotal.setText(getIntDefault(blueData, "teleopPoints"));

        /* Ranking Points */
        setRocketRankingPoint(redData, redCompleteRocket);
        setHabRankingPoint(redData, redHabDocking);
        setRocketRankingPoint(blueData, blueCompleteRocket);
        setHabRankingPoint(blueData, blueHabDocking);

        /* Fouls */
        foulsRed.setText(mResources.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(redData, "foulPoints")));
        foulsBlue.setText(mResources.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(blueData, "foulPoints")));

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

    private void setSandstormBonus(JsonObject allianceData, String robotNumber, TextView view) {
        String habLineAction = getIntDefault(allianceData, "habLineRobot" + robotNumber);
        String preMatchLevel = getIntDefault(allianceData, "preMatchLevelRobot" + robotNumber);
        @DrawableRes int bonusResource;
        int bonusPoints;
        if (!habLineAction.equals("CrossedHabLineInSandstorm")) {
            bonusPoints = 0;
            bonusResource = R.drawable.ic_close_black_24dp;
        } else {
            switch (preMatchLevel) {
                case "HabLevel1":
                    bonusPoints = 3;
                    bonusResource = R.drawable.baseline_looks_one_black_24;
                    break;
                case "HabLevel2":
                    bonusPoints = 6;
                    bonusResource = R.drawable.baseline_looks_two_black_24;
                    break;
                default:
                    bonusPoints = 0;
                    bonusResource = R.drawable.ic_close_black_24dp;
                    break;
            }
        }

        String bonusText = (bonusPoints != 0) ? mResources.getString(R.string.breakdown_addition_format, bonusPoints) : "";
        view.setText(bonusText);
        view.setCompoundDrawablesWithIntrinsicBounds(bonusResource, 0, 0, 0);
    }

    private void setCargoShipScoring(JsonObject allianceData, TextView nullPanels, TextView panels, TextView cargo) {
        int nullPanelCount = 0;
        int panelCount = 0;
        int cargoCount = 0;
        for (int i = 1; i <= 8; i++) {
            String initialValue = getIntDefault(allianceData, "preMatchBay" + i);
            String scoreValue = getIntDefault(allianceData, "bay" + i);
            if (initialValue.contains("Panel")) {
                nullPanelCount++;
            } else if (scoreValue.contains("Panel")) {
                panelCount++;
            }
            if (scoreValue.contains("Cargo")) {
                cargoCount++;
            }
        }

        nullPanels.setText(String.format("%1$s", nullPanelCount));
        panels.setText(String.format("%1$s", panelCount));
        cargo.setText(String.format("%1$s", cargoCount));
    }

    private static String[] rocketKeys = {"topLeftRocket", "topRightRocket", "midLeftRocket", "midRightRocket", "lowLeftRocket", "lowRightRocket"};
    private void setRocketScoring(JsonObject allianceData, String rocket /* Near or Far */, TextView panels, TextView cargo) {
        int panelCount = 0;
        int cargoCount = 0;
        for (String key : rocketKeys) {
            String scoreValue = getIntDefault(allianceData,  key + rocket);
            if (scoreValue.contains("Panel")) {
                panelCount++;
            }
            if (scoreValue.contains("Cargo")) {
                cargoCount++;
            }
        }

        panels.setText(String.format("%1$s", panelCount));
        cargo.setText(String.format("%1$s", cargoCount));
    }

    private void setTotalPanels(JsonObject allianceData, TextView view) {
        int panelScore = getIntDefaultValue(allianceData, "hatchPanelPoints");
        int panelCount = panelScore / 2;

        if (panelScore == 0) {
            view.setText(mResources.getString(R.string.breakdown_number_format, panelScore));
        } else {
            view.setText(mResources.getString(R.string.breakdown_number_with_addition_format, panelCount, panelScore));
        }
    }

    private void setTotalCargo(JsonObject allianceData, TextView view) {
        int panelScore = getIntDefaultValue(allianceData, "cargoPoints");
        int panelCount = panelScore / 3;

        if (panelScore == 0) {
            view.setText(mResources.getString(R.string.breakdown_number_format, panelScore));
        } else {
            view.setText(mResources.getString(R.string.breakdown_number_with_addition_format, panelCount, panelScore));
        }
    }

    private void setHabClimbBonus(JsonObject allianceData, String robotNumber, TextView view) {
        String endgameState = getIntDefault(allianceData, "endgameRobot" + robotNumber);
        @DrawableRes int bonusResource;
        int bonusPoints;
        switch (endgameState) {
            case "HabLevel3":
                bonusResource = R.drawable.baseline_looks_3_black_24;
                bonusPoints = 12;
                break;
            case "HabLevel2":
                bonusResource = R.drawable.baseline_looks_two_black_24;
                bonusPoints = 6;
                break;
            case "HabLevel1":
                bonusResource = R.drawable.baseline_looks_one_black_24;
                bonusPoints = 3;
                break;
            default:
                bonusResource = R.drawable.ic_close_black_24dp;
                bonusPoints = 0;
                break;
        }

        String bonusText = (bonusPoints != 0) ? mResources.getString(R.string.breakdown_addition_format, bonusPoints) : "";
        view.setText(bonusText);
        view.setCompoundDrawablesWithIntrinsicBounds(bonusResource, 0, 0, 0);
    }

    private void setRocketRankingPoint(JsonObject allianceData, TextView view) {
        @DrawableRes int resource;
        String additionText;

        boolean nearRocket = getBooleanDefault(allianceData, "completedRocketNear");
        boolean farRocket = getBooleanDefault(allianceData, "completedRocketFar");
        boolean rankingPoint = getBooleanDefault(allianceData, "completeRocketRankingPoint");
        if (nearRocket && farRocket) {
            resource = R.drawable.baseline_done_all_black_24;
        } else if (nearRocket || farRocket) {
            resource = R.drawable.ic_check_black_24dp;
        } else {
            resource = R.drawable.ic_close_black_24dp;
        }


        if (rankingPoint) {
            additionText = mResources.getString(R.string.breakdown_rp_format, 1);
        } else {
            additionText = "";
        }

        view.setText(additionText);
        view.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
    }

    private void setHabRankingPoint(JsonObject allianceData, TextView view) {
        @DrawableRes int resource;
        String additionText;
        int habPoints = getIntDefaultValue(allianceData, "habClimbPoints");
        boolean habRp = getBooleanDefault(allianceData, "habDockingRankingPoint");
        if (habPoints >= 15 || habRp) {
            resource = R.drawable.ic_check_black_24dp;
        } else {
            resource = R.drawable.ic_close_black_24dp;
        }

        if (habRp) {
            additionText = mResources.getString(R.string.breakdown_rp_format, 1);
        } else {
            additionText = "";
        }

        view.setText(additionText);
        view.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
    }
}
