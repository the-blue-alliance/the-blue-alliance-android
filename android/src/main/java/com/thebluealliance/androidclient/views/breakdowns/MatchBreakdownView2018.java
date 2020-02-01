package com.thebluealliance.androidclient.views.breakdowns;


import android.content.Context;
import android.content.res.Resources;
import androidx.gridlayout.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

public class MatchBreakdownView2018 extends AbstractMatchBreakdownView {
    @BindView(R.id.breakdown2018_container)                     GridLayout breakdownContainer;

    @BindView(R.id.breakdown_red1)                              TextView red1;
    @BindView(R.id.breakdown_blue1)                             TextView blue1;
    @BindView(R.id.breakdown_red2)                              TextView red2;
    @BindView(R.id.breakdown_blue2)                             TextView blue2;
    @BindView(R.id.breakdown_red3)                              TextView red3;
    @BindView(R.id.breakdown_blue3)                             TextView blue3;

    @BindView(R.id.breakdown2018_red_auto_run_robot_1)          ImageView redAutoRunRobot1;
    @BindView(R.id.breakdown2018_blue_auto_run_robot_1)         ImageView blueAutoRunRobot1;
    @BindView(R.id.breakdown2018_red_auto_run_robot_2)          ImageView redAutoRunRobot2;
    @BindView(R.id.breakdown2018_blue_auto_run_robot_2)         ImageView blueAutoRunRobot2;
    @BindView(R.id.breakdown2018_red_auto_run_robot_3)          ImageView redAutoRunRobot3;
    @BindView(R.id.breakdown2018_blue_auto_run_robot_3)         ImageView blueAutoRunRobot3;
    @BindView(R.id.breakdown2018_red_auto_run_points)           TextView redAutoRunPoints;
    @BindView(R.id.breakdown2018_blue_auto_run_points)          TextView blueAutoRunPoints;
    @BindView(R.id.breakdown2018_red_auto_scale_seconds)        TextView redAutoScaleSeconds;
    @BindView(R.id.breakdown2018_blue_auto_scale_seconds)       TextView blueAutoScaleSeconds;
    @BindView(R.id.breakdown2018_red_auto_switch_seconds)       TextView redAutoSwitchSeconds;
    @BindView(R.id.breakdown2018_blue_auto_switch_seconds)      TextView blueAutoSwitchSeconds;
    @BindView(R.id.breakdown2018_red_auto_ownership_points)     TextView redAutoOwnershipPoints;
    @BindView(R.id.breakdown2018_blue_auto_ownership_points)    TextView blueAutoOwnershipPoints;
    @BindView(R.id.breakdown_auto_total_red)                    TextView redAutoTotal;
    @BindView(R.id.breakdown_auto_total_blue)                   TextView blueAutoTotal;

    @BindView(R.id.breakdown2018_teleop_red_scale_seconds)      TextView redTeleopScaleSeconds;
    @BindView(R.id.breakdown2018_teleop_blue_scale_seconds)     TextView blueTeleopScaleSeconds;
    @BindView(R.id.breakdown2018_teleop_red_switch_seconds)     TextView redTeleopSwitchSeconds;
    @BindView(R.id.breakdown2018_teleop_blue_switch_seconds)    TextView blueTeleopSwitchSeconds;
    @BindView(R.id.breakdown2018_teleop_red_ownership_points)   TextView redTeleopOwnershipPoints;
    @BindView(R.id.breakdown2018_teleop_blue_ownership_points)  TextView blueTeleopOwnershipPoints;

    @BindView(R.id.breakdown2018_vault_red_force_cubes)         TextView redVaultForceCubes;
    @BindView(R.id.breakdown2018_vault_blue_force_cubes)        TextView blueVaultForceCubes;
    @BindView(R.id.breakdown2018_vault_red_levitate_cubes)      TextView redVaultLevitateCubes;
    @BindView(R.id.breakdown2018_vault_blue_levitate_cubes)     TextView blueVaultLevitateCubes;
    @BindView(R.id.breakdown2018_vault_red_boost_cubes)         TextView redVaultBoostCubes;
    @BindView(R.id.breakdown2018_vault_blue_boost_cubes)        TextView blueVaultBoostCubes;
    @BindView(R.id.breakdown2018_vault_red_total)               TextView redVaultPoints;
    @BindView(R.id.breakdown2018_vault_blue_total)              TextView blueVaultPoints;

    @BindView(R.id.breakdown2018_endgame_red_robot_1)           TextView redEndgameRobot1;
    @BindView(R.id.breakdown2018_endgame_blue_robot_1)          TextView blueEndgameRobot1;
    @BindView(R.id.breakdown2018_endgame_red_robot_2)           TextView redEndgameRobot2;
    @BindView(R.id.breakdown2018_endgame_blue_robot_2)          TextView blueEndgameRobot2;
    @BindView(R.id.breakdown2018_endgame_red_robot_3)           TextView redEndgameRobot3;
    @BindView(R.id.breakdown2018_endgame_blue_robot_3)          TextView blueEndgameRobot3;
    @BindView(R.id.breakdown2018_endgame_red_total)             TextView redEndgamePoints;
    @BindView(R.id.breakdown2018_endgame_blue_total)            TextView blueEndgamePoints;

    @BindView(R.id.breakdown_teleop_total_red)                  TextView redTeleopTotal;
    @BindView(R.id.breakdown_teleop_total_blue)                 TextView blueTeleopTotal;

    @BindView(R.id.breakdown2018_red_face_the_boss)             ImageView redFaceTheBoss;
    @BindView(R.id.breakdown2018_blue_face_the_boss)            ImageView blueFaceTheBoss;
    @BindView(R.id.breakdown2018_red_auto_quest)                ImageView redAutoQuest;
    @BindView(R.id.breakdown2018_blue_auto_quest)               ImageView blueAutoQuest;

    @BindView(R.id.breakdown_fouls_red)                         TextView foulsRed;
    @BindView(R.id.breakdown_fouls_blue)                        TextView foulsBlue;
    @BindView(R.id.breakdown_adjust_red)                        TextView adjustRed;
    @BindView(R.id.breakdown_adjust_blue)                       TextView adjustBlue;
    @BindView(R.id.breakdown_total_red)                         TextView totalRed;
    @BindView(R.id.breakdown_total_blue)                        TextView totalBlue;
    @BindView(R.id.breakdown_red_rp)                            TextView rpRed;
    @BindView(R.id.breakdown_blue_rp)                           TextView rpBlue;
    @BindView(R.id.breakdown_rp_header)                         TextView rpHeader;

    public MatchBreakdownView2018(Context context) {
        super(context);
    }

    public MatchBreakdownView2018(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2018(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2018, this, true);
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
        Resources res = getResources();

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

        /* Auto Run */
        redAutoRunRobot1.setImageResource(getAutoRunResource(getIntDefault(redData, "autoRobot1")));
        blueAutoRunRobot1.setImageResource(getAutoRunResource(getIntDefault(blueData, "autoRobot1")));
        redAutoRunRobot2.setImageResource(getAutoRunResource(getIntDefault(redData, "autoRobot2")));
        blueAutoRunRobot2.setImageResource(getAutoRunResource(getIntDefault(blueData, "autoRobot2")));
        redAutoRunRobot3.setImageResource(getAutoRunResource(getIntDefault(redData, "autoRobot3")));
        blueAutoRunRobot3.setImageResource(getAutoRunResource(getIntDefault(blueData, "autoRobot3")));

        /* Auto Run Points */
        redAutoRunPoints.setText(getIntDefault(redData, "autoRunPoints"));
        blueAutoRunPoints.setText(getIntDefault(blueData, "autoRunPoints"));

        /* Auto Scale Ownership Seconds */
        redAutoScaleSeconds.setText(getIntDefault(redData, "autoScaleOwnershipSec"));
        blueAutoScaleSeconds.setText(getIntDefault(blueData, "autoScaleOwnershipSec"));

        /* Auto Switch Ownership Seconds */
        redAutoSwitchSeconds.setText(getIntDefault(redData, "autoSwitchOwnershipSec"));
        blueAutoSwitchSeconds.setText(getIntDefault(blueData, "autoSwitchOwnershipSec"));

        /* Auto Ownership Points */
        redAutoOwnershipPoints.setText(getIntDefault(redData, "autoOwnershipPoints"));
        blueAutoOwnershipPoints.setText(getIntDefault(blueData, "autoOwnershipPoints"));

        /* Auto Total Points */
        redAutoTotal.setText(getIntDefault(redData, "autoPoints"));
        blueAutoTotal.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Switch Ownership Seconds */
        redTeleopSwitchSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(redData, "teleopSwitchOwnershipSec"), getIntDefault(redData, "teleopSwitchBoostSec")));
        blueTeleopSwitchSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(blueData, "teleopSwitchOwnershipSec"), getIntDefault(blueData, "teleopSwitchBoostSec")));

        /* Teleop Scale Ownership Seconds */
        redTeleopScaleSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(redData, "teleopScaleOwnershipSec"), getIntDefault(redData, "teleopScaleBoostSec")));
        blueTeleopScaleSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(blueData, "teleopScaleOwnershipSec"), getIntDefault(blueData, "teleopScaleBoostSec")));

        /* Teleop Ownership Points */
        redTeleopOwnershipPoints.setText(getIntDefault(redData, "teleopOwnershipPoints"));
        blueTeleopOwnershipPoints.setText(getIntDefault(blueData, "teleopOwnershipPoints"));

        /* Teleop Cubes Played */
        redVaultForceCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(redData, "vaultForceTotal"), getIntDefault(redData, "vaultForcePlayed")));
        blueVaultForceCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(blueData, "vaultForceTotal"), getIntDefault(blueData, "vaultForcePlayed")));
        redVaultLevitateCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(redData, "vaultLevitateTotal"), getIntDefault(redData, "vaultLevitatePlayed")));
        blueVaultLevitateCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(blueData, "vaultLevitateTotal"), getIntDefault(blueData, "vaultLevitatePlayed")));
        redVaultBoostCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(redData, "vaultBoostTotal"), getIntDefault(redData, "vaultBoostPlayed")));
        blueVaultBoostCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(blueData, "vaultBoostTotal"), getIntDefault(blueData, "vaultBoostPlayed")));

        /* Teleop Vault Points */
        redVaultPoints.setText(getIntDefault(redData, "vaultPoints"));
        blueVaultPoints.setText(getIntDefault(blueData, "vaultPoints"));

        /* Endgame */
        redEndgameRobot1.setText(getIntDefault(redData, "endgameRobot1"));
        blueEndgameRobot1.setText(getIntDefault(blueData, "endgameRobot1"));
        redEndgameRobot2.setText(getIntDefault(redData, "endgameRobot2"));
        blueEndgameRobot2.setText(getIntDefault(blueData, "endgameRobot2"));
        redEndgameRobot3.setText(getIntDefault(redData, "endgameRobot3"));
        blueEndgameRobot3.setText(getIntDefault(blueData, "endgameRobot3"));

        /* Endgame Points */
        redEndgamePoints.setText(getIntDefault(redData, "endgamePoints"));
        blueEndgamePoints.setText(getIntDefault(blueData, "endgamePoints"));

        /* Teleop Total Points */
        redTeleopTotal.setText(getIntDefault(redData, "teleopPoints"));
        blueTeleopTotal.setText(getIntDefault(blueData, "teleopPoints"));

        /* Auto Quest */
        redAutoQuest.setImageResource(getRankingPointResource(getBooleanDefault(redData, "autoQuestRankingPoint")));
        blueAutoQuest.setImageResource(getRankingPointResource(getBooleanDefault(blueData, "autoQuestRankingPoint")));

        /* Face the Boss */
        redFaceTheBoss.setImageResource(getRankingPointResource(getBooleanDefault(redData, "faceTheBossRankingPoint")));
        blueFaceTheBoss.setImageResource(getRankingPointResource(getBooleanDefault(blueData, "faceTheBossRankingPoint")));

        /* Fouls */
        foulsRed.setText(res.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(redData, "foulPoints")));
        foulsBlue.setText(res.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(blueData, "foulPoints")));

        /* Adjustment points */
        adjustRed.setText(getIntDefault(redData, "adjustPoints"));
        adjustBlue.setText(getIntDefault(blueData, "adjustPoints"));

        /* Total Points */
        totalRed.setText(getIntDefault(redData, "totalPoints"));
        totalBlue.setText(getIntDefault(blueData, "totalPoints"));

        /* Show RPs earned, if needed */
        if (!matchType.isPlayoff()) {
            rpRed.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "rp")));
            rpBlue.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "rp")));
        } else {
            rpRed.setVisibility(GONE);
            rpBlue.setVisibility(GONE);
            rpHeader.setVisibility(GONE);
        }

        breakdownContainer.setVisibility(View.VISIBLE);
        return true;
    }

    private int getRankingPointResource(boolean achievedRankingPoint) {
        if (achievedRankingPoint)
            return R.drawable.ic_check_black_24dp;
        else
            return R.drawable.ic_close_black_24dp;
    }

    private int getAutoRunResource(String autoRunAction) {
        if (autoRunAction.equals("AutoRun"))
            return R.drawable.ic_check_black_24dp;
        else
            return R.drawable.ic_close_black_24dp;
    }
}
