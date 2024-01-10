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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2020Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;
import java.util.Map;

public class MatchBreakdownView2020 extends AbstractMatchBreakdownView {
    private MatchBreakdown2020Binding mBinding;
    private Resources mResources;

    private static final @NonNull Map<String, Integer> ENDGAME_POINTS = ImmutableMap.of(
            "Park", 5,
            "Hang", 25);
    private static final int[] STAGE_DRAWABLES = {R.drawable.baseline_looks_one_black_24,
            R.drawable.baseline_looks_two_black_24,
            R.drawable.baseline_looks_3_black_24};

    public MatchBreakdownView2020(Context context) {
        super(context);
    }

    public MatchBreakdownView2020(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2020(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        mBinding = MatchBreakdown2020Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }


    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2020Container.setVisibility(GONE);
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
        setInitiationLine(redData, mBinding.breakdown2020RedInitLineBonus, mBinding.breakdown2020RedInitLineRobot1, mBinding.breakdown2020RedInitLineRobot2, mBinding.breakdown2020RedInitLineRobot3);
        setInitiationLine(blueData, mBinding.breakdown2020BlueInitLineBonus, mBinding.breakdown2020BlueInitLineRobot1, mBinding.breakdown2020BlueInitLineRobot2, mBinding.breakdown2020BlueInitLineRobot3);

        /* Auto Power Cells */
        setPowerCells(redData, "auto", mBinding.breakdown2020RedAutoLowGoal, mBinding.breakdown2020RedAutoOuterGoal, mBinding.breakdown2020RedAutoInnerGoal, mBinding.breakdown2020RedAutoPowerCell);
        setPowerCells(blueData, "auto", mBinding.breakdown2020BlueAutoLowGoal, mBinding.breakdown2020BlueAutoOuterGoal, mBinding.breakdown2020BlueAutoInnerGoal, mBinding.breakdown2020BlueAutoPowerCell);

        /* Total Auto Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Power Cell */
        setPowerCells(redData, "teleop", mBinding.breakdown2020RedTeleopLowGoal, mBinding.breakdown2020RedTeleopOuterGoal, mBinding.breakdown2020RedTeleopInnerGoal, mBinding.breakdown2020RedTeleopPowerCell);
        setPowerCells(blueData, "teleop", mBinding.breakdown2020BlueTeleopLowGoal, mBinding.breakdown2020BlueTeleopOuterGoal, mBinding.breakdown2020BlueTeleopInnerGoal, mBinding.breakdown2020BlueTeleopPowerCell);

        /* Control Panel */
        mBinding.breakdown2020RedControlPanel.setText(getIntDefault(redData, "controlPanelPoints"));
        mBinding.breakdown2020BlueControlPanel.setText(getIntDefault(blueData, "controlPanelPoints"));

        /* Endgame */
        setEndgame(redData, mBinding.breakdownEndgameTotalRed, mBinding.breakdown2020RedShieldGenerator, mBinding.breakdown2020RedEndgameRobot1, mBinding.breakdown2020RedEndgameRobot2, mBinding.breakdown2020RedEndgameRobot3);
        setEndgame(blueData, mBinding.breakdownEndgameTotalBlue, mBinding.breakdown2020BlueShieldGenerator, mBinding.breakdown2020BlueEndgameRobot1, mBinding.breakdown2020BlueEndgameRobot2, mBinding.breakdown2020BlueEndgameRobot3);

        /* Teleop Total */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Stage Activations */
        setStageActivations(redData, mBinding.breakdown2020RedStage3Rp, mBinding.breakdown2020RedStage1, mBinding.breakdown2020RedStage2, mBinding.breakdown2020RedStage3);
        setStageActivations(blueData, mBinding.breakdown2020BlueStage3Rp, mBinding.breakdown2020BlueStage1, mBinding.breakdown2020BlueStage2, mBinding.breakdown2020BlueStage3);

        /* Generator Operational */
        setGeneratorOperational(redData, mBinding.breakdown2020RedShieldGeneratorRp);
        setGeneratorOperational(blueData, mBinding.breakdown2020BlueShieldGeneratorRp);

        /* Fouls */
        setFouls(redData, blueData, mBinding.breakdownFoulsRed);
        setFouls(blueData, redData, mBinding.breakdownFoulsBlue);

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

        mBinding.breakdown2020Container.setVisibility(View.VISIBLE);

        return true;
    }

    private void setInitiationLine(JsonObject allianceData,
                                   TextView bonusView,
                                   ImageView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String initLineAction = getIntDefault(allianceData, "initLineRobot" + robotNumber);
            ImageView teamIcon = robotStatus[robotNumber - 1];
            if ("Exited".equals(initLineAction)) {
                teamIcon.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                teamIcon.setImageResource(R.drawable.ic_close_black_24dp);
            }
        }

        int initLinePoints = getIntDefaultValue(allianceData, "autoInitLinePoints");
        if (initLinePoints > 0) {
            bonusView.setVisibility(VISIBLE);
            bonusView.setText(mResources.getString(R.string.breakdown_addition_format, initLinePoints));
        } else {
            bonusView.setVisibility(GONE);
        }
    }

    private void setPowerCells(JsonObject allianceData,
                               String prefix,
                               TextView lowGoal,
                               TextView outerGoal,
                               TextView innerGoal,
                               TextView totalPoints) {
        lowGoal.setText(getIntDefault(allianceData, prefix + "CellsBottom"));
        outerGoal.setText(getIntDefault(allianceData, prefix + "CellsOuter"));
        innerGoal.setText(getIntDefault(allianceData, prefix + "CellsInner"));
        totalPoints.setText(getIntDefault(allianceData, prefix + "CellPoints"));
    }

    private void setEndgame(JsonObject allianceData, TextView endgameTotal, TextView rungLevel, TextView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        int numRobotsHanging = 0;
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
            if ("Hang".equals(endgameStatus)) {
                numRobotsHanging++;
            }
        }

        endgameTotal.setText(getIntDefault(allianceData, "endgamePoints"));
        boolean isLevel = "IsLevel".equals(getIntDefault(allianceData, "endgameRungIsLevel"));
        @DrawableRes int rungDrawable;
        if (isLevel) {
            rungDrawable = R.drawable.ic_check_black_24dp;
        } else {
            rungDrawable = R.drawable.ic_close_black_24dp;
        }
        rungLevel.setCompoundDrawablesWithIntrinsicBounds(rungDrawable, 0, 0, 0);

        if (numRobotsHanging > 0 && isLevel) {
            rungLevel.setText(mResources.getString(R.string.breakdown_addition_format, 15));
        } else {
            rungLevel.setText("");
        }
    }

    private void setStageActivations(JsonObject allianceData, TextView stage3Rp, ImageView... stages) {
        if (stages.length != 3) {
            throw new RuntimeException("bad number of status views");
        }

        boolean anyStageActivated = false;
        boolean allStagesActivated = true;
        for (int stage = 1; stage <= 3; stage++) {
            boolean activated = getBooleanDefault(allianceData, "stage" + stage + "Activated");
            anyStageActivated |= activated;
            allStagesActivated &= activated;

            ImageView stageView = stages[stage - 1];
            if (activated) {
                stageView.setVisibility(VISIBLE);
                stageView.setImageResource(STAGE_DRAWABLES[stage - 1]);
            } else {
                stageView.setVisibility(GONE);
            }
        }

        if (!anyStageActivated) {
            ImageView stage1View = stages[0];
            stage1View.setVisibility(VISIBLE);
            stage1View.setImageResource(R.drawable.ic_close_black_24dp);
        }

        if (allStagesActivated) {
            stage3Rp.setText(mResources.getString(R.string.breakdown_rp_format, 1));
        } else {
            stage3Rp.setText("");
        }
    }

    private void setGeneratorOperational(JsonObject allianceData, TextView view) {
        boolean isOperational = getBooleanDefault(allianceData, "shieldOperationalRankingPoint");
        if (isOperational) {
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
