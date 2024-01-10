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

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2022Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Map;

public class MatchBreakdownView2022 extends AbstractMatchBreakdownView {
    private MatchBreakdown2022Binding mBinding;
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
        mBinding = MatchBreakdown2022Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }


    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2022Container.setVisibility(GONE);
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

        /* Auto Initiation Line*/
        setAutoTaxi(redData, mBinding.breakdown2022RedAutoTaxiBonus, mBinding.breakdown2022RedAutoTaxiRobot1, mBinding.breakdown2022RedAutoTaxiRobot2, mBinding.breakdown2022RedAutoTaxiRobot3);
        setAutoTaxi(blueData, mBinding.breakdown2022BlueAutoTaxiBonus, mBinding.breakdown2022BlueAutoTaxiRobot1, mBinding.breakdown2022BlueAutoTaxiRobot2, mBinding.breakdown2022BlueAutoTaxiRobot3);

        /* Auto Cargo */
        setCargo(redData, "auto", mBinding.breakdown2022RedAutoLowerHub, mBinding.breakdown2022RedAutoUpperHub, mBinding.breakdown2022RedAutoCargo);
        setCargo(blueData, "auto", mBinding.breakdown2022BlueAutoLowerHub, mBinding.breakdown2022BlueAutoUpperHub, mBinding.breakdown2022BlueAutoCargo);

        /* Auto Quintet */
        setQuintet(redData, mBinding.breakdown2022RedQuintet);
        setQuintet(blueData, mBinding.breakdown2022BlueQuintet);

        /* Total Auto Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Cargo */
        setCargo(redData, "teleop", mBinding.breakdown2022RedTeleopLowerHub, mBinding.breakdown2022RedTeleopUpperHub, mBinding.breakdown2022RedTeleopCargo);
        setCargo(blueData, "teleop", mBinding.breakdown2022BlueTeleopLowerHub, mBinding.breakdown2022BlueTeleopUpperHub, mBinding.breakdown2022BlueTeleopUpperHub);

        /* Endgame */
        setEndgame(redData, mBinding.breakdownEndgameTotalRed, mBinding.breakdown2022RedEndgameRobot1, mBinding.breakdown2022RedEndgameRobot2, mBinding.breakdown2022RedEndgameRobot3);
        setEndgame(blueData, mBinding.breakdownEndgameTotalBlue, mBinding.breakdown2022BlueEndgameRobot1, mBinding.breakdown2022BlueEndgameRobot2, mBinding.breakdown2022BlueEndgameRobot3);

        /* Teleop Total */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Cargo Bonus */
        setBonus(redData, "cargo", mBinding.breakdown2022RedCargoBonus);
        setBonus(blueData, "cargo", mBinding.breakdown2022BlueCargoBonus);

        /* Hangar Bonus */
        setBonus(redData, "hangar", mBinding.breakdown2022RedHangarBonus);
        setBonus(blueData, "hangar", mBinding.breakdown2022BlueHangarBonus);

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

        mBinding.breakdown2022Container.setVisibility(View.VISIBLE);

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
        int lowerHubTotal = getIntDefaultValue(allianceData, prefix + "CargoLowerNear")
                + getIntDefaultValue(allianceData, prefix + "CargoLowerFar")
                + getIntDefaultValue(allianceData, prefix + "CargoLowerBlue")
                + getIntDefaultValue(allianceData, prefix + "CargoLowerRed");
        lowerHub.setText(Integer.toString(lowerHubTotal));

        int upperHubTotal = getIntDefaultValue(allianceData, prefix + "CargoUpperNear")
                + getIntDefaultValue(allianceData, prefix + "CargoUpperFar")
                + getIntDefaultValue(allianceData, prefix + "CargoUpperBlue")
                + getIntDefaultValue(allianceData, prefix + "CargoUpperRed");
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

    private void setFouls(JsonObject otherAllianceData, TextView view) {
        int foulPoints = getIntDefaultValue(otherAllianceData, "foulCount") * 4;
        int techFoulPoints = getIntDefaultValue(otherAllianceData, "techFoulCount") * 8;
        view.setText(mResources.getString(R.string.breakdown_foul_tech_format, foulPoints, techFoulPoints));
    }
}
