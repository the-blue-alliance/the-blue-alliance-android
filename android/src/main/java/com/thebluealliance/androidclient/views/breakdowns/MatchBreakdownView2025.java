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

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2025Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MatchBreakdownView2025 extends AbstractMatchBreakdownView {

    private MatchBreakdown2025Binding mBinding;
    private Resources mResources;

    public MatchBreakdownView2025(Context context) {
        super(context);
    }

    public MatchBreakdownView2025(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2025(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        mBinding = MatchBreakdown2025Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2025Container.setVisibility(GONE);
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

        /* Auto Leave */
        setAutoLeave(redData, mBinding.breakdown2025RedAutoLeaveBonus, mBinding.breakdown2025RedAutoLeaveRobot1, mBinding.breakdown2025RedAutoLeaveRobot2, mBinding.breakdown2025RedAutoLeaveRobot3);
        setAutoLeave(blueData, mBinding.breakdown2025BlueAutoLeaveBonus, mBinding.breakdown2025BlueAutoLeaveRobot1, mBinding.breakdown2025BlueAutoLeaveRobot2, mBinding.breakdown2025BlueAutoLeaveRobot3);

        /* Auto Coral */
        setCoralReef(redData, "auto", mBinding.breakdown2025RedAutoCoralPoints, mBinding.breakdown2025RedAutoCoralL1Count, mBinding.breakdown2025RedAutoCoralL2Count, mBinding.breakdown2025RedAutoCoralL3Count, mBinding.breakdown2025RedAutoCoralL4Count);
        setCoralReef(blueData, "auto", mBinding.breakdown2025BlueAutoCoralPoints, mBinding.breakdown2025BlueAutoCoralL1Count, mBinding.breakdown2025BlueAutoCoralL2Count, mBinding.breakdown2025BlueAutoCoralL3Count, mBinding.breakdown2025BlueAutoCoralL4Count);

        /* Total Auto Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Coral */
        setCoralReef(redData, "teleop", mBinding.breakdown2025RedTeleopCoralPoints, mBinding.breakdown2025RedTeleopCoralL1Count, mBinding.breakdown2025RedTeleopCoralL2Count, mBinding.breakdown2025RedTeleopCoralL3Count, mBinding.breakdown2025RedTeleopCoralL4Count);
        setCoralReef(blueData, "teleop", mBinding.breakdown2025BlueTeleopCoralPoints, mBinding.breakdown2025BlueTeleopCoralL1Count, mBinding.breakdown2025BlueTeleopCoralL2Count, mBinding.breakdown2025BlueTeleopCoralL3Count, mBinding.breakdown2025BlueTeleopCoralL4Count);

        /* Processor Algae */
        mBinding.breakdown2025RedTeleopProcessorAlgaeCount.setText(getIntDefault(redData, "wallAlgaeCount"));
        mBinding.breakdown2025BlueTeleopProcessorAlgaeCount.setText(getIntDefault(blueData, "wallAlgaeCount"));

        /* Net Algae */
        mBinding.breakdown2025RedTeleopNetAlgaeCount.setText(getIntDefault(redData, "netAlgaeCount"));
        mBinding.breakdown2025BlueTeleopNetAlgaeCount.setText(getIntDefault(blueData, "netAlgaeCount"));

        /* Algae Points */
        mBinding.breakdown2025RedTeleopAlgaePoints.setText(getIntDefault(redData, "algaePoints"));
        mBinding.breakdown2025BlueTeleopAlgaePoints.setText(getIntDefault(blueData, "algaePoints"));

        /* Endgame Points */
        setEndgame(redData, mBinding.breakdown2025RedEndgameRobot1, mBinding.breakdown2025RedEndgameRobot2, mBinding.breakdown2025RedEndgameRobot3);
        setEndgame(blueData, mBinding.breakdown2025BlueEndgameRobot1, mBinding.breakdown2025BlueEndgameRobot2, mBinding.breakdown2025BlueEndgameRobot3);

        /* Barge Points */
        mBinding.breakdown2025RedBargePoints.setText(getIntDefault(redData, "endGameBargePoints"));
        mBinding.breakdown2025BlueBargePoints.setText(getIntDefault(blueData, "endGameBargePoints"));

        /* Teleop Total */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Coopertition Criteria Met */
        setCoopertitionMet(redData, mBinding.breakdown2025RedCoopertitionCriteria);
        setCoopertitionMet(blueData, mBinding.breakdown2025BlueCoopertitionCriteria);

        /* Auto Bonus */
        setBonus(redData, "auto", mBinding.breakdown2025RedAutoBonus);
        setBonus(blueData, "auto", mBinding.breakdown2025BlueAutoBonus);

        /* Coral Bonus */
        setBonus(redData, "coral", mBinding.breakdown2025RedCoralBonus);
        setBonus(blueData, "coral", mBinding.breakdown2025BlueCoralBonus);

        /* Barge Bonus */
        setBonus(redData, "barge", mBinding.breakdown2025RedBargeBonus);
        setBonus(blueData, "barge", mBinding.breakdown2025BlueBargeBonus);

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

        mBinding.breakdown2025Container.setVisibility(View.VISIBLE);
        return true;
    }

    private void setAutoLeave(JsonObject allianceData,
                              TextView bonusView,
                              ImageView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String leaveAchieved = getIntDefault(allianceData, "autoLineRobot" + robotNumber);
            ImageView teamIcon = robotStatus[robotNumber - 1];
            if ("Yes".equals(leaveAchieved)) {
                teamIcon.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                teamIcon.setImageResource(R.drawable.ic_close_black_24dp);
            }
        }

        int leavePoints = getIntDefaultValue(allianceData, "autoMobilityPoints");
        if (leavePoints > 0) {
            bonusView.setVisibility(VISIBLE);
            bonusView.setText(mResources.getString(R.string.breakdown_addition_format, leavePoints));
        } else {
            bonusView.setVisibility(GONE);
        }
    }

    static final Map<Integer, String> REEF_LEVEL_KEYS = ImmutableMap.<Integer, String>builder()
            .put(0, "botRow")
            .put(1, "midRow")
            .put(2, "topRow")
            .build();
    private void setCoralReef(JsonObject allianceData, String prefix, TextView coralPointsView, TextView troughView, TextView... levelViews) {
        if (levelViews.length != 3) {
            throw new RuntimeException("bad number of reef level views!");
        }

        JsonObject reefData = allianceData.get(prefix + "Reef").getAsJsonObject();
        troughView.setText(getIntDefault(reefData, "trough"));

        for (int level = 0; level < 3; level++) {
            JsonObject reefScoring = reefData.get(REEF_LEVEL_KEYS.get(level)).getAsJsonObject();
            int scoredCoral = 0;
            for(Map.Entry<String, JsonElement> position : reefScoring.entrySet()) {
                if (position.getValue().getAsBoolean()) {
                    scoredCoral++;
                }
            }
            levelViews[level].setText(String.format(Locale.getDefault(), "%d", scoredCoral));
        }

        coralPointsView.setText(getIntDefault(allianceData, prefix + "CoralPoints"));
    }

    private void setEndgame(JsonObject allianceData, TextView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }

        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String apiStatus = getIntDefault(allianceData, "endGameRobot" + robotNumber);

            String endgameStatus = "";
            int endgamePoints = 0;
            if ("Parked".equals(apiStatus)) {
                endgameStatus = "Parked";
                endgamePoints = 2;
            } else if ("ShallowCage".equals(apiStatus)) {
                endgameStatus = "Shallow Cage";
                endgamePoints = 6;
            } else if ("DeepCage".equals(apiStatus)) {
                endgameStatus = "Deep Cage";
                endgamePoints = 12;
            }

            TextView teamView = robotStatus[robotNumber - 1];
            teamView.setVisibility(VISIBLE);
            if (endgamePoints > 0) {
                teamView.setCompoundDrawables(null, null, null, null);
                teamView.setText(mResources.getString(R.string.breakdown_string_with_addition_format, endgameStatus, endgamePoints));
            } else {
                teamView.setText("");
                teamView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_black_24dp, 0, 0, 0);
            }
        }
    }

    private void setCoopertitionMet(JsonObject allianceData, ImageView coopView) {
        boolean isAchieved = getBooleanDefault(allianceData, "coopertitionCriteriaMet");
        if (isAchieved) {
            coopView.setImageResource(R.drawable.ic_check_black_24dp);
        } else {
            coopView.setImageResource(R.drawable.ic_close_black_24dp);
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
        int foulPoints = getIntDefaultValue(otherAllianceData, "foulCount") * 2;
        int techFoulPoints = getIntDefaultValue(otherAllianceData, "techFoulCount") * 6;
        view.setText(mResources.getString(R.string.breakdown_foul_tech_format, foulPoints, techFoulPoints));
    }
}
