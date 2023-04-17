package com.thebluealliance.androidclient.views.breakdowns;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MatchBreakdownView2023 extends AbstractMatchBreakdownView {
    @BindView(R.id.breakdown2023_container)                     GridLayout breakdownContainer;

    @BindView(R.id.breakdown_red1)                              TextView red1;
    @BindView(R.id.breakdown_blue1)                             TextView blue1;
    @BindView(R.id.breakdown_red2)                              TextView red2;
    @BindView(R.id.breakdown_blue2)                             TextView blue2;
    @BindView(R.id.breakdown_red3)                              TextView red3;
    @BindView(R.id.breakdown_blue3)                             TextView blue3;
    @BindView(R.id.breakdown2023_red_auto_mobility_robot1)          ImageView red1Mobility;
    @BindView(R.id.breakdown2023_red_auto_mobility_robot2)          ImageView red2Mobility;
    @BindView(R.id.breakdown2023_red_auto_mobility_robot3)          ImageView red3Mobility;
    @BindView(R.id.breakdown2023_red_auto_mobility_bonus)           TextView  redMobilityBonus;
    @BindView(R.id.breakdown2023_blue_auto_mobility_robot1)         ImageView blue1Mobility;
    @BindView(R.id.breakdown2023_blue_auto_mobility_robot2)         ImageView blue2Mobility;
    @BindView(R.id.breakdown2023_blue_auto_mobility_robot3)         ImageView blue3Mobility;
    @BindView(R.id.breakdown2023_blue_auto_mobility_bonus)          TextView  blueMobilityBonus;
    @BindView(R.id.breakdown2023_red_auto_piece_count)          TextView redAutoPieceCount;
    @BindView(R.id.breakdown2023_red_auto_piece_points)         TextView redAutoPiecePoints;
    @BindView(R.id.breakdown2023_blue_auto_piece_count)         TextView blueAutoPieceCount;
    @BindView(R.id.breakdown2023_blue_auto_piece_points)        TextView blueAutoPiecePoints;

    @BindView(R.id.breakdown2023_red_auto_charge_station_robot1)   TextView red1AutoChargeStation;
    @BindView(R.id.breakdown2023_red_auto_charge_station_robot2)   TextView red2AutoChargeStation;
    @BindView(R.id.breakdown2023_red_auto_charge_station_robot3)   TextView red3AutoChargeStation;
    @BindView(R.id.breakdown2023_blue_auto_charge_station_robot1)  TextView blue1AutoChargeStation;
    @BindView(R.id.breakdown2023_blue_auto_charge_station_robot2)  TextView blue2AutoChargeStation;
    @BindView(R.id.breakdown2023_blue_auto_charge_station_robot3)  TextView blue3AutoChargeStation;

    @BindView(R.id.breakdown_auto_total_red)                    TextView redAutoTotal;
    @BindView(R.id.breakdown_auto_total_blue)                   TextView blueAutoTotal;
    @BindView(R.id.breakdown2023_red_teleop_piece_count)   TextView redTeleopPieceCount;
    @BindView(R.id.breakdown2023_blue_teleop_piece_count)  TextView blueTeleopPieceCount;
    @BindView(R.id.breakdown2023_red_teleop_piece_points)  TextView redTeleopPiecePoints;
    @BindView(R.id.breakdown2023_blue_teleop_piece_points) TextView blueTeleopPiecePoints;
    @BindView(R.id.breakdown2023_red_endgame_robot1)            TextView redEndgameRobot1;
    @BindView(R.id.breakdown2023_red_endgame_robot2)            TextView redEndgameRobot2;
    @BindView(R.id.breakdown2023_red_endgame_robot3)            TextView redEndgameRobot3;
    @BindView(R.id.breakdown2023_blue_endgame_robot1)           TextView blueEndgameRobot1;
    @BindView(R.id.breakdown2023_blue_endgame_robot2)           TextView blueEndgameRobot2;
    @BindView(R.id.breakdown2023_blue_endgame_robot3)           TextView blueEndgameRobot3;
    @BindView(R.id.breakdown_teleop_total_red)                  TextView redTeleopTotal;
    @BindView(R.id.breakdown_teleop_total_blue)                 TextView blueTeleopTotal;
    @BindView(R.id.breakdown2023_red_links)                     TextView redLinks;
    @BindView(R.id.breakdown2023_blue_links)                    TextView blueLinks;
    @BindView(R.id.breakdown2023_red_coopertition_criteria)     ImageView redCoopertitionCriteriaMet;
    @BindView(R.id.breakdown2023_blue_coopertition_criteria)    ImageView blueCoopertitionCriteriaMet;
    @BindView(R.id.breakdown2023_red_sustainability_bonus)      TextView redSustainabilityBonus;
    @BindView(R.id.breakdown2023_blue_sustainability_bonus)     TextView blueSustainabilityBonus;
    @BindView(R.id.breakdown2023_red_activation_bonus)          TextView redActivationBonus;
    @BindView(R.id.breakdown2023_blue_activation_bonus)         TextView blueActivationBonus;
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

    public MatchBreakdownView2023(Context context) {
        super(context);
    }

    public MatchBreakdownView2023(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2023(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2023, this, true);
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

        /* Auto Mobility */
        setAutoMobility(redData, redMobilityBonus, red1Mobility, red2Mobility, red3Mobility);
        setAutoMobility(blueData, blueMobilityBonus, blue1Mobility, blue2Mobility, blue3Mobility);

        /* Auto Game Pieces */
        setGamePieces(redData, "auto", redAutoPieceCount, redAutoPiecePoints);
        setGamePieces(blueData, "auto", blueAutoPieceCount, blueAutoPiecePoints);

        /* Auto Charge Station */
        setChargeStation(redData, "auto", red1AutoChargeStation, red2AutoChargeStation, red3AutoChargeStation);
        setChargeStation(blueData, "auto",blue1AutoChargeStation, blue2AutoChargeStation, blue3AutoChargeStation);

        /* Total Auto Points */
        redAutoTotal.setText(getIntDefault(redData, "autoPoints"));
        blueAutoTotal.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Pieces */
        setGamePieces(redData, "teleop", redTeleopPieceCount, redTeleopPiecePoints);
        setGamePieces(blueData, "teleop", blueTeleopPieceCount, blueTeleopPiecePoints);

        /* Endgame Charge Station */
        setChargeStation(redData, "endGame", redEndgameRobot1, redEndgameRobot2, redEndgameRobot3);
        setChargeStation(blueData, "endGame",blueEndgameRobot1, blueEndgameRobot2, blueEndgameRobot3);

        /* Links */
        setLinks(redData, redLinks);
        setLinks(blueData, blueLinks);

        /* Teleop Total */
        redTeleopTotal.setText(getIntDefault(redData, "teleopPoints"));
        blueTeleopTotal.setText(getIntDefault(blueData, "teleopPoints"));

        /* Coopertition Criteria Met */
        setCoopertitionMet(redData, redCoopertitionCriteriaMet);
        setCoopertitionMet(blueData, blueCoopertitionCriteriaMet);

        /* Sustainability Bonus */
        setBonus(redData, "sustainability", redSustainabilityBonus);
        setBonus(blueData, "sustainability", blueSustainabilityBonus);

        /* Activation Bonus */
        setBonus(redData, "activation", redActivationBonus);
        setBonus(blueData, "activation", blueActivationBonus);

        /* Fouls */
        setFouls(blueData, foulsRed);
        setFouls(redData, foulsBlue);

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

    private void setAutoMobility(JsonObject allianceData,
                             TextView bonusView,
                             ImageView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String mobilityAchieved = getIntDefault(allianceData, "mobilityRobot" + robotNumber);
            ImageView teamIcon = robotStatus[robotNumber - 1];
            if ("Yes".equals(mobilityAchieved)) {
                teamIcon.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                teamIcon.setImageResource(R.drawable.ic_close_black_24dp);
            }
        }

        int mobilityPoints = getIntDefaultValue(allianceData, "autoMobilityPoints");
        if (mobilityPoints > 0) {
            bonusView.setVisibility(VISIBLE);
            bonusView.setText(mResources.getString(R.string.breakdown_addition_format, mobilityPoints));
        } else {
            bonusView.setVisibility(GONE);
        }
    }

    private void setGamePieces(JsonObject allianceData,
                               String prefix,
                               TextView pieceCount,
                               TextView piecePoints) {
        int gamePieceCount = getIntDefaultValue(allianceData, prefix + "GamePieceCount");
        pieceCount.setText(Integer.toString(gamePieceCount));

        int gamePiecePoints = getIntDefaultValue(allianceData, prefix + "GamePiecePoints");
        piecePoints.setText(Integer.toString(gamePiecePoints));
    }

    private void setLinks(JsonObject allianceData,
                          TextView linkView) {
        int linkPoints = getIntDefaultValue(allianceData, "linkPoints");

        int links = 0;
        if (linkPoints != 0) {
            links = linkPoints / 5;
        }
        linkView.setText(mResources.getString(R.string.breakdown2023_teleop_links_format, links, linkPoints));
    }

    private void setCoopertitionMet(JsonObject allianceData, ImageView coopView) {
        boolean isAchieved = getBooleanDefault(allianceData, "coopertitionCriteriaMet");
        if (isAchieved) {
            coopView.setImageResource(R.drawable.ic_check_black_24dp);
        } else {
            coopView.setImageResource(R.drawable.ic_close_black_24dp);
        }
    }

    private void setChargeStation(JsonObject allianceData, String prefix, TextView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String endgameStatus = getIntDefault(allianceData, prefix + "ChargeStationRobot" + robotNumber);
            String endgameBrideState = getIntDefault(allianceData, prefix+ "BridgeState");

            int endgamePoints = 0;
            if(Objects.equals(endgameStatus, "Park")) {
                endgamePoints = 2;
            } else if (Objects.equals(endgameStatus, "Docked")) {
                if (Objects.equals(endgameBrideState, "Level")) {
                    endgamePoints = 10;
                    endgameStatus = "Engaged";
                } else {
                    endgamePoints = 6;
                }
                if (Objects.equals(prefix, "auto")) {
                    endgamePoints += 2;
                }
            }
            TextView teamView = robotStatus[robotNumber - 1];
            teamView.setVisibility(VISIBLE);
            if (endgamePoints > 0) {
                teamView.setCompoundDrawables(null,null,null,null);
                teamView.setText(mResources.getString(R.string.breakdown_string_with_addition_format, endgameStatus, endgamePoints));
            } else {
                teamView.setBackgroundResource(R.drawable.ic_close_black_24dp);
            }
        }
    }

    private void setBonus(JsonObject allianceData, String bonusName, TextView view) {
        boolean isAchieved = getBooleanDefault(allianceData, bonusName + "BonusAchieved");
        if (isAchieved) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_black_24dp, 0, 0, 0);
            view.setText(mResources.getString(R.string.breakdown_rp_format, 1));
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_black_24dp, 0, 0, 0);
            view.setText("");
        }
    }

    private void setFouls(JsonObject otherAllianceData, TextView view) {
        int foulPoints = getIntDefaultValue(otherAllianceData, "foulCount") * 5;
        int techFoulPoints = getIntDefaultValue(otherAllianceData, "techFoulCount") * 12;
        view.setText(mResources.getString(R.string.breakdown_foul_tech_format, foulPoints, techFoulPoints));
    }
}
