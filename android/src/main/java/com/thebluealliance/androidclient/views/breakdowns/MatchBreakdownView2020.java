package com.thebluealliance.androidclient.views.breakdowns;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
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
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.setViewVisibility;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

public class MatchBreakdownView2020 extends AbstractMatchBreakdownView {
    @BindView(R.id.breakdown2020_container)                     GridLayout breakdownContainer;

    @BindView(R.id.breakdown_red1)                              TextView red1;
    @BindView(R.id.breakdown_blue1)                             TextView blue1;
    @BindView(R.id.breakdown_red2)                              TextView red2;
    @BindView(R.id.breakdown_blue2)                             TextView blue2;
    @BindView(R.id.breakdown_red3)                              TextView red3;
    @BindView(R.id.breakdown_blue3)                             TextView blue3;

    @BindView(R.id.breakdown2020_red_init_line_robot1)          ImageView red1InitLine;
    @BindView(R.id.breakdown2020_red_init_line_robot2)          ImageView red2InitLine;
    @BindView(R.id.breakdown2020_red_init_line_robot3)          ImageView red3InitLine;
    @BindView(R.id.breakdown2020_red_init_line_bonus)           TextView  redInitLineBonus;
    @BindView(R.id.breakdown2020_blue_init_line_robot1)         ImageView blue1InitLine;
    @BindView(R.id.breakdown2020_blue_init_line_robot2)         ImageView blue2InitLine;
    @BindView(R.id.breakdown2020_blue_init_line_robot3)         ImageView blue3InitLine;
    @BindView(R.id.breakdown2020_blue_init_line_bonus)          TextView  blueInitLineBonus;

    @BindView(R.id.breakdown2020_red_auto_low_goal)             TextView redAutoLowGoal;
    @BindView(R.id.breakdown2020_red_auto_outer_goal)           TextView redAutoOuterGoal;
    @BindView(R.id.breakdown2020_red_auto_inner_goal)           TextView redAutoInnerGoal;
    @BindView(R.id.breakdown2020_blue_auto_low_goal)            TextView blueAutoLowGoal;
    @BindView(R.id.breakdown2020_blue_auto_outer_goal)          TextView blueAutoOuterGoal;
    @BindView(R.id.breakdown2020_blue_auto_inner_goal)          TextView blueAutoInnerGoal;
    @BindView(R.id.breakdown2020_red_auto_power_cell)           TextView redAutoPowerCell;
    @BindView(R.id.breakdown2020_blue_auto_power_cell)          TextView blueAutoPowerCell;

    @BindView(R.id.breakdown_auto_total_red)                    TextView redAutoTotal;
    @BindView(R.id.breakdown_auto_total_blue)                   TextView blueAutoTotal;

    @BindView(R.id.breakdown2020_red_teleop_low_goal)           TextView redTeleopLowGoal;
    @BindView(R.id.breakdown2020_red_teleop_outer_goal)         TextView redTeleopOuterGoal;
    @BindView(R.id.breakdown2020_red_teleop_inner_goal)         TextView redTeleopInnerGoal;
    @BindView(R.id.breakdown2020_blue_teleop_low_goal)          TextView blueTelopLowGoal;
    @BindView(R.id.breakdown2020_blue_teleop_outer_goal)        TextView blueTeleopOuterGoal;
    @BindView(R.id.breakdown2020_blue_teleop_inner_goal)        TextView blueTeleopInnerGoal;
    @BindView(R.id.breakdown2020_red_teleop_power_cell)         TextView redTeleopPowerCell;
    @BindView(R.id.breakdown2020_blue_teleop_power_cell)        TextView blueTeleopPowerCell;
    @BindView(R.id.breakdown2020_red_control_panel)             TextView redControlPanel;
    @BindView(R.id.breakdown2020_blue_control_panel)            TextView blueControlPanel;

    @BindView(R.id.breakdown2020_red_endgame_robot1)            TextView redEndgameRobot1;
    @BindView(R.id.breakdown2020_red_endgame_robot2)            TextView redEndgameRobot2;
    @BindView(R.id.breakdown2020_red_endgame_robot3)            TextView redEndgameRobot3;
    @BindView(R.id.breakdown2020_blue_endgame_robot1)           TextView blueEndgameRobot1;
    @BindView(R.id.breakdown2020_blue_endgame_robot2)           TextView blueEndgameRobot2;
    @BindView(R.id.breakdown2020_blue_endgame_robot3)           TextView blueEndgameRobot3;
    @BindView(R.id.breakdown2020_red_shield_generator)          TextView redShieldGenerator;
    @BindView(R.id.breakdown2020_blue_shield_generator)         TextView blueShieldGenerator;
    @BindView(R.id.breakdown_endgame_total_red)                 TextView redEndgameTotal;
    @BindView(R.id.breakdown_endgame_total_blue)                TextView blueEndgameTotal;

    @BindView(R.id.breakdown_teleop_total_red)                  TextView redTeleopTotal;
    @BindView(R.id.breakdown_teleop_total_blue)                 TextView blueTeleopTotal;

    @BindView(R.id.breakdown2020_red_stage1)                    ImageView redStage1;
    @BindView(R.id.breakdown2020_red_stage2)                    ImageView redStage2;
    @BindView(R.id.breakdown2020_red_stage3)                    ImageView redStage3;
    @BindView(R.id.breakdown2020_red_stage3_rp)                 TextView redStage3Rp;
    @BindView(R.id.breakdown2020_blue_stage1)                   ImageView blueStage1;
    @BindView(R.id.breakdown2020_blue_stage2)                   ImageView blueStage2;
    @BindView(R.id.breakdown2020_blue_stage3)                   ImageView blueStage3;
    @BindView(R.id.breakdown2020_blue_stage3_rp)                TextView blueStage3Rp;

    @BindView(R.id.breakdown2020_red_shield_generator_rp)       TextView redShieldGeneratorRp;
    @BindView(R.id.breakdown2020_blue_shield_generator_rp)       TextView blueShieldGeneratorRp;

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

    private static @NonNull Map<String, Integer> ENDGAME_POINTS = ImmutableMap.of(
            "Park", 5,
            "Hang", 25);
    private static int[] STAGE_DRAWABLES = {R.drawable.baseline_looks_one_black_24,
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
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2020, this, true);
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
        setInitiationLine(redData, redInitLineBonus, red1InitLine, red2InitLine, red3InitLine);
        setInitiationLine(blueData, blueInitLineBonus, blue1InitLine, blue2InitLine, blue3InitLine);

        /* Auto Power Cells */
        setPowerCells(redData, "auto", redAutoLowGoal, redAutoOuterGoal, redAutoInnerGoal, redAutoPowerCell);
        setPowerCells(blueData, "auto", blueAutoLowGoal, blueAutoOuterGoal, blueAutoInnerGoal, blueAutoPowerCell);

        /* Total Auto Points */
        redAutoTotal.setText(getIntDefault(redData, "autoPoints"));
        blueAutoTotal.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Power Cell */
        setPowerCells(redData, "teleop", redTeleopLowGoal, redTeleopOuterGoal, redTeleopInnerGoal, redTeleopPowerCell);
        setPowerCells(blueData, "teleop", blueTelopLowGoal, blueTeleopOuterGoal, blueTeleopInnerGoal, blueTeleopPowerCell);

        /* Control Panel */
        redControlPanel.setText(getIntDefault(redData, "controlPanelPoints"));
        blueControlPanel.setText(getIntDefault(blueData, "controlPanelPoints"));

        /* Endgame */
        setEndgame(redData, redEndgameTotal, redShieldGenerator, redEndgameRobot1, redEndgameRobot2, redEndgameRobot3);
        setEndgame(blueData, blueEndgameTotal, blueShieldGenerator, blueEndgameRobot1, blueEndgameRobot2, blueEndgameRobot3);

        /* Teleop Total */
        redTeleopTotal.setText(getIntDefault(redData, "teleopPoints"));
        blueTeleopInnerGoal.setText(getIntDefault(blueData, "teleopPoints"));

        /* Stage Activations */
        setStageActivations(redData, redStage3Rp, redStage1, redStage2, redStage3);
        setStageActivations(blueData, blueStage3Rp, blueStage1, blueStage2, blueStage3);

        /* Generator Operational */
        setGeneratorOperational(redData, redShieldGeneratorRp);
        setGeneratorOperational(blueData, blueShieldGeneratorRp);

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
                teamIcon.setBackgroundResource(R.drawable.ic_check_black_24dp);
            } else {
                teamIcon.setBackgroundResource(R.drawable.ic_close_black_24dp);
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
        totalPoints.setText(getIntDefault(allianceData, prefix + "ellPoints"));
    }

    private void setEndgame(JsonObject allianceData, TextView endgameTotal, TextView rungLevel, TextView... robotStatus) {
        if (robotStatus.length != 3) {
            throw new RuntimeException("bad number of status views");
        }
        int numRobotsHanging = 0;
        for (int robotNumber = 1; robotNumber <= 3; robotNumber++) {
            String endgameStatus = getIntDefault(allianceData, "endgameRobot" + robotNumber);
            Integer endgamePoints =  ENDGAME_POINTS.containsKey(endgameStatus) ? ENDGAME_POINTS.get(endgameStatus) : 0;
            TextView teamView = robotStatus[robotNumber = 1];
            if (endgamePoints != null && endgamePoints > 0) {
                teamView.setVisibility(VISIBLE);
                teamView.setText(mResources.getString(R.string.breakdown_string_with_addition_format, endgameStatus, endgamePoints));
            } else {
                teamView.setVisibility(GONE);
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
                stageView.setBackgroundResource(STAGE_DRAWABLES[stage - 1]);
            } else {
                stageView.setVisibility(GONE);
            }
        }

        if (!anyStageActivated) {
            ImageView stage1View = stages[0];
            stage1View.setVisibility(VISIBLE);
            stage1View.setBackgroundResource(R.drawable.ic_close_black_24dp);
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
            view.setBackgroundResource(R.drawable.ic_check_black_24dp);
            view.setText(mResources.getString(R.string.breakdown_rp_format, 1));
        } else {
            view.setBackgroundResource(R.drawable.ic_close_black_24dp);
            view.setText("");
        }
    }

    private void setFouls(JsonObject allianceData, JsonObject otherAllianceData, TextView view) {
        int foulPoints = getIntDefaultValue(otherAllianceData, "foulCount") * 3;
        int techFoulPoints = getIntDefaultValue(otherAllianceData, "techFoulCount") * 15;
        boolean foulRpAwarded = getBooleanDefault(allianceData, "tba_foulRp");
        if (foulRpAwarded) {
            view.setText(mResources.getString(R.string.breakdown_foul_tech_rp_format, foulPoints, techFoulPoints, 1));
        } else {
            view.setText(mResources.getString(R.string.breakdown_foul_tech_format, foulPoints, techFoulPoints));
        }
    }
}
