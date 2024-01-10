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

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2023Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Objects;

public class MatchBreakdownView2023 extends AbstractMatchBreakdownView {
    private MatchBreakdown2023Binding mBinding;
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
        mBinding = MatchBreakdown2023Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }


    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2023Container.setVisibility(GONE);
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
        mBinding.breakdownRed1.setText(teamNumberFromKey(redTeams.get(0)));
        mBinding.breakdownRed2.setText(teamNumberFromKey(redTeams.get(1)));
        mBinding.breakdownRed3.setText(teamNumberFromKey(redTeams.get(2)));

        /* Blue Teams */
        mBinding.breakdownBlue1.setText(teamNumberFromKey(blueTeams.get(0)));
        mBinding.breakdownBlue2.setText(teamNumberFromKey(blueTeams.get(1)));
        mBinding.breakdownBlue3.setText(teamNumberFromKey(blueTeams.get(2)));

        /* Auto Mobility */
        setAutoMobility(redData, mBinding.breakdown2023RedAutoMobilityBonus, mBinding.breakdown2023RedAutoMobilityRobot1, mBinding.breakdown2023RedAutoMobilityRobot2, mBinding.breakdown2023RedAutoMobilityRobot3);
        setAutoMobility(blueData, mBinding.breakdown2023BlueAutoMobilityBonus, mBinding.breakdown2023BlueAutoMobilityRobot1, mBinding.breakdown2023BlueAutoMobilityRobot2, mBinding.breakdown2023BlueAutoMobilityRobot3);

        /* Auto Game Pieces */
        setGamePieces(redData, "auto", mBinding.breakdown2023RedAutoPieceCount, mBinding.breakdown2023RedAutoPiecePoints);
        setGamePieces(blueData, "auto", mBinding.breakdown2023BlueAutoPieceCount, mBinding.breakdown2023BlueAutoPiecePoints);

        /* Auto Charge Station */
        setChargeStation(redData, "auto", mBinding.breakdown2023RedAutoChargeStationRobot1, mBinding.breakdown2023RedAutoChargeStationRobot2, mBinding.breakdown2023RedAutoChargeStationRobot3);
        setChargeStation(blueData, "auto", mBinding.breakdown2023BlueAutoChargeStationRobot1, mBinding.breakdown2023BlueAutoChargeStationRobot2, mBinding.breakdown2023BlueAutoChargeStationRobot3);

        /* Total Auto Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Pieces */
        setGamePieces(redData, "teleop", mBinding.breakdown2023RedTeleopPieceCount, mBinding.breakdown2023RedTeleopPiecePoints);
        setGamePieces(blueData, "teleop", mBinding.breakdown2023BlueTeleopPieceCount, mBinding.breakdown2023BlueTeleopPiecePoints);

        /* Endgame Charge Station */
        setChargeStation(redData, "endGame", mBinding.breakdown2023RedEndgameRobot1, mBinding.breakdown2023RedEndgameRobot2, mBinding.breakdown2023RedEndgameRobot3);
        setChargeStation(blueData, "endGame", mBinding.breakdown2023BlueEndgameRobot1, mBinding.breakdown2023BlueEndgameRobot2, mBinding.breakdown2023BlueEndgameRobot3);

        /* Links */
        setLinks(redData, mBinding.breakdown2023RedLinks);
        setLinks(blueData, mBinding.breakdown2023BlueLinks);

        /* Teleop Total */
        mBinding.breakdown2023RedTeleopPiecePoints.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdown2023BlueTeleopPiecePoints.setText(getIntDefault(blueData, "teleopPoints"));

        /* Coopertition Criteria Met */
        setCoopertitionMet(redData, mBinding.breakdown2023RedCoopertitionCriteria);
        setCoopertitionMet(blueData, mBinding.breakdown2023BlueCoopertitionCriteria);

        /* Sustainability Bonus */
        setBonus(redData, "sustainability", mBinding.breakdown2023RedSustainabilityBonus);
        setBonus(blueData, "sustainability", mBinding.breakdown2023BlueSustainabilityBonus);

        /* Activation Bonus */
        setBonus(redData, "activation", mBinding.breakdown2023RedActivationBonus);
        setBonus(blueData, "activation", mBinding.breakdown2023BlueActivationBonus);

        /* Fouls */
        setFouls(blueData, mBinding.breakdownFoulsRed);
        setFouls(redData, mBinding.breakdownFoulsBlue);

        /* Adjustment points */
        mBinding.breakdownAdjustRed.setText(getIntDefault(redData, "adjustPoints"));
        mBinding.breakdownAdjustBlue.setText(getIntDefault(blueData, "adjustPoints"));

        /* Total Points */
        mBinding.breakdownTotalRed.setText(getIntDefault(redData, "totalPoints"));
        mBinding.breakdownTotalBlue.setText(getIntDefault(blueData, "totalPoints"));

        /* Show RPs earned, if needed */
        if (!matchType.isPlayoff()) {
            mBinding.breakdownRedRp.setText(mResources.getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "rp")));
            mBinding.breakdownBlueRp.setText(mResources.getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "rp")));
        } else {
            mBinding.breakdownRedRp.setVisibility(GONE);
            mBinding.breakdownBlueRp.setVisibility(GONE);
            mBinding.breakdownRpHeader.setVisibility(GONE);
        }

        mBinding.breakdown2023Container.setVisibility(View.VISIBLE);

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
            String endgameBrideState = getIntDefault(allianceData, prefix + "BridgeState");

            int endgamePoints = 0;
            if (Objects.equals(endgameStatus, "Park")) {
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
                teamView.setCompoundDrawables(null, null, null, null);
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
