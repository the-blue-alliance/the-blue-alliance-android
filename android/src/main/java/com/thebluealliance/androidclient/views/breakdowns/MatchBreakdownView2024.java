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
import com.thebluealliance.androidclient.databinding.MatchBreakdown2024Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Locale;

public class MatchBreakdownView2024 extends AbstractMatchBreakdownView {

    private MatchBreakdown2024Binding mBinding;
    private Resources mResources;

    public MatchBreakdownView2024(Context context) {
        super(context);
    }

    public MatchBreakdownView2024(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2024(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        mBinding = MatchBreakdown2024Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2024Container.setVisibility(GONE);
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
        setAutoLeave(redData, mBinding.breakdown2024RedAutoLeaveBonus, mBinding.breakdown2024RedAutoLeaveRobot1, mBinding.breakdown2024RedAutoLeaveRobot2, mBinding.breakdown2024RedAutoLeaveRobot3);
        setAutoLeave(blueData, mBinding.breakdown2024BlueAutoLeaveBonus, mBinding.breakdown2024BlueAutoLeaveRobot1, mBinding.breakdown2024BlueAutoLeaveRobot2, mBinding.breakdown2024BlueAutoLeaveRobot3);

        /* Auto Amp */
        mBinding.breakdown2024RedAutoAmpCount.setText(getIntDefault(redData, "autoAmpNoteCount"));
        mBinding.breakdown2024BlueAutoAmpCount.setText(getIntDefault(blueData, "autoAmpNoteCount"));

        /* Auto Speaker */
        mBinding.breakdown2024RedAutoSpeakerCount.setText(getIntDefault(redData, "autoSpeakerNoteCount"));
        mBinding.breakdown2024BlueAutoSpeakerCount.setText(getIntDefault(blueData, "autoSpeakerNoteCount"));

        /* Auto Note */
        mBinding.breakdown2024RedAutoNotePoints.setText(getIntDefault(redData, "autoTotalNotePoints"));
        mBinding.breakdown2024BlueAutoNotePoints.setText(getIntDefault(blueData, "autoTotalNotePoints"));

        /* Total Auto Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Amp */
        mBinding.breakdown2024RedTeleopAmpCount.setText(getIntDefault(redData, "teleopAmpNoteCount"));
        mBinding.breakdown2024BlueTeleopAmpCount.setText(getIntDefault(blueData, "teleopAmpNoteCount"));

        /* Teleop Speaker */
        setTeleopSpeaker(redData, mBinding.breakdown2024RedTeleopSpeakerCountUnamplified, mBinding.breakdown2024RedTeleopSpeakerCountAmplified);
        setTeleopSpeaker(blueData, mBinding.breakdown2024BlueTeleopSpeakerCountUnamplified, mBinding.breakdown2024BlueTeleopSpeakerCountAmplified);

        /* Teleop Note Points */
        mBinding.breakdown2024RedTeleopNotePoints.setText(getIntDefault(redData, "teleopTotalNotePoints"));
        mBinding.breakdown2024BlueTeleopNotePoints.setText(getIntDefault(blueData, "teleopTotalNotePoints"));

        /* Endgame Points */
        setEndgame(redData, mBinding.breakdown2024RedEndgameRobot1, mBinding.breakdown2024RedEndgameRobot2, mBinding.breakdown2024RedEndgameRobot3);
        setEndgame(blueData, mBinding.breakdown2024BlueEndgameRobot1, mBinding.breakdown2024BlueEndgameRobot2, mBinding.breakdown2024BlueEndgameRobot3);

        /* Harmony Point s*/
        mBinding.breakdown2024RedHarmony.setText(getIntDefault(redData, "endGameHarmonyPoints"));
        mBinding.breakdown2024BlueHarmony.setText(getIntDefault(blueData, "endGameHarmonyPoints"));

        /* Trap Points */
        mBinding.breakdown2024RedTrap.setText(getIntDefault(redData, "endGameNoteInTrapPoints"));
        mBinding.breakdown2024BlueTrap.setText(getIntDefault(blueData, "endGameNoteInTrapPoints"));

        /* Teleop Total */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Coopertition Criteria Met */
        setCoopertitionMet(redData, mBinding.breakdown2024RedCoopertitionCriteria);
        setCoopertitionMet(blueData, mBinding.breakdown2024BlueCoopertitionCriteria);

        /* Melody Bonus */
        setBonus(redData, "melody", mBinding.breakdown2024RedMelodyBonus);
        setBonus(blueData, "melody", mBinding.breakdown2024BlueMelodyBonus);

        /* Ensemble Bonus */
        setBonus(redData, "ensemble", mBinding.breakdown2024RedEnsembleBonus);
        setBonus(blueData, "ensemble", mBinding.breakdown2024BlueEnsembleBonus);

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

        mBinding.breakdown2024Container.setVisibility(View.VISIBLE);
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

        int leavePoints = getIntDefaultValue(allianceData, "autoLeavePoints");
        if (leavePoints > 0) {
            bonusView.setVisibility(VISIBLE);
            bonusView.setText(mResources.getString(R.string.breakdown_addition_format, leavePoints));
        } else {
            bonusView.setVisibility(GONE);
        }
    }

    private void setTeleopSpeaker(JsonObject allianceData, TextView unamplifiedView, TextView amplifiedView) {
        int unamplifiedNoteCount = getIntDefaultValue(allianceData, "teleopSpeakerNoteCount");
        int amplifiedNoteCount = getIntDefaultValue(allianceData, "teleopSpeakerNoteAmplifiedCount");

        unamplifiedView.setText(String.format(Locale.getDefault(), "%d", unamplifiedNoteCount));
        amplifiedView.setText(String.format(Locale.getDefault(), "%d", amplifiedNoteCount));
    }

    private void setEndgame(JsonObject allianceData, TextView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }

        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String stageStatus = getIntDefault(allianceData, "endGameRobot" + robotNumber);

            String endgameStatus = "";
            int endgamePoints = 0;
            if ("Parked".equals(stageStatus)) {
                endgameStatus = "Parked";
                endgamePoints = 1;
            } else if (stageStatus.contains("Stage")) {
                // one of StageLeft, CenterStage, StageRight
                boolean spotlightResult = getBooleanDefault(allianceData, "mic" + stageStatus);
                if (spotlightResult) {
                    endgameStatus = "Spotlit";
                    endgamePoints = 4;
                } else {
                    endgameStatus = "Onstage";
                    endgamePoints = 3;
                }
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
        int techFoulPoints = getIntDefaultValue(otherAllianceData, "techFoulCount") * 5;
        view.setText(mResources.getString(R.string.breakdown_foul_tech_format, foulPoints, techFoulPoints));
    }
}
