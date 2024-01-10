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

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2018Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

public class MatchBreakdownView2018 extends AbstractMatchBreakdownView {
    private MatchBreakdown2018Binding mBinding;;

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
        mBinding = MatchBreakdown2018Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2018Container.setVisibility(GONE);
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
        mBinding.breakdownRed1.setText(teamNumberFromKey(redTeams.get(0)));
        mBinding.breakdownRed2.setText(teamNumberFromKey(redTeams.get(1)));
        mBinding.breakdownRed3.setText(teamNumberFromKey(redTeams.get(2)));

        /* Blue Teams */
        mBinding.breakdownBlue1.setText(teamNumberFromKey(blueTeams.get(0)));
        mBinding.breakdownBlue2.setText(teamNumberFromKey(blueTeams.get(1)));
        mBinding.breakdownBlue3.setText(teamNumberFromKey(blueTeams.get(2)));

        /* Auto Run */
        mBinding.breakdown2018RedAutoRunRobot1.setImageResource(getAutoRunResource(getIntDefault(redData, "autoRobot1")));
        mBinding.breakdown2018BlueAutoRunRobot1.setImageResource(getAutoRunResource(getIntDefault(blueData, "autoRobot1")));
        mBinding.breakdown2018RedAutoRunRobot2.setImageResource(getAutoRunResource(getIntDefault(redData, "autoRobot2")));
        mBinding.breakdown2018BlueAutoRunRobot2.setImageResource(getAutoRunResource(getIntDefault(blueData, "autoRobot2")));
        mBinding.breakdown2018RedAutoRunRobot3.setImageResource(getAutoRunResource(getIntDefault(redData, "autoRobot3")));
        mBinding.breakdown2018BlueAutoRunRobot3.setImageResource(getAutoRunResource(getIntDefault(blueData, "autoRobot3")));

        /* Auto Run Points */
        mBinding.breakdown2018RedAutoRunPoints.setText(getIntDefault(redData, "autoRunPoints"));
        mBinding.breakdown2018BlueAutoRunPoints.setText(getIntDefault(blueData, "autoRunPoints"));

        /* Auto Scale Ownership Seconds */
        mBinding.breakdown2018RedAutoScaleSeconds.setText(getIntDefault(redData, "autoScaleOwnershipSec"));
        mBinding.breakdown2018BlueAutoScaleSeconds.setText(getIntDefault(blueData, "autoScaleOwnershipSec"));

        /* Auto Switch Ownership Seconds */
        mBinding.breakdown2018RedAutoSwitchSeconds.setText(getIntDefault(redData, "autoSwitchOwnershipSec"));
        mBinding.breakdown2018BlueAutoSwitchSeconds.setText(getIntDefault(blueData, "autoSwitchOwnershipSec"));

        /* Auto Ownership Points */
        mBinding.breakdown2018RedAutoOwnershipPoints.setText(getIntDefault(redData, "autoOwnershipPoints"));
        mBinding.breakdown2018BlueAutoOwnershipPoints.setText(getIntDefault(blueData, "autoOwnershipPoints"));

        /* Auto Total Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Switch Ownership Seconds */
        mBinding.breakdown2018TeleopRedSwitchSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(redData, "teleopSwitchOwnershipSec"), getIntDefault(redData, "teleopSwitchBoostSec")));
        mBinding.breakdown2018TeleopBlueSwitchSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(blueData, "teleopSwitchOwnershipSec"), getIntDefault(blueData, "teleopSwitchBoostSec")));

        /* Teleop Scale Ownership Seconds */
        mBinding.breakdown2018TeleopRedScaleSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(redData, "teleopScaleOwnershipSec"), getIntDefault(redData, "teleopScaleBoostSec")));
        mBinding.breakdown2018TeleopBlueScaleSeconds.setText(res.getString(R.string.breakdown2018_scale_switch_boost_points, getIntDefault(blueData, "teleopScaleOwnershipSec"), getIntDefault(blueData, "teleopScaleBoostSec")));

        /* Teleop Ownership Points */
        mBinding.breakdown2018TeleopRedOwnershipPoints.setText(getIntDefault(redData, "teleopOwnershipPoints"));
        mBinding.breakdown2018TeleopRedOwnershipPoints.setText(getIntDefault(blueData, "teleopOwnershipPoints"));

        /* Teleop Cubes Played */
        mBinding.breakdown2018VaultRedForceCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(redData, "vaultForceTotal"), getIntDefault(redData, "vaultForcePlayed")));
        mBinding.breakdown2018VaultBlueForceCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(blueData, "vaultForceTotal"), getIntDefault(blueData, "vaultForcePlayed")));
        mBinding.breakdown2018VaultRedLevitateCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(redData, "vaultLevitateTotal"), getIntDefault(redData, "vaultLevitatePlayed")));
        mBinding.breakdown2018VaultBlueLevitateCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(blueData, "vaultLevitateTotal"), getIntDefault(blueData, "vaultLevitatePlayed")));
        mBinding.breakdown2018VaultRedBoostCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(redData, "vaultBoostTotal"), getIntDefault(redData, "vaultBoostPlayed")));
        mBinding.breakdown2018VaultBlueBoostCubes.setText(res.getString(R.string.breakdown2018_cubes_total_played, getIntDefault(blueData, "vaultBoostTotal"), getIntDefault(blueData, "vaultBoostPlayed")));

        /* Teleop Vault Points */
        mBinding.breakdown2018VaultRedTotal.setText(getIntDefault(redData, "vaultPoints"));
        mBinding.breakdown2018VaultBlueTotal.setText(getIntDefault(blueData, "vaultPoints"));

        /* Endgame */
        mBinding.breakdown2018EndgameRedRobot1.setText(getIntDefault(redData, "endgameRobot1"));
        mBinding.breakdown2018EndgameBlueRobot1.setText(getIntDefault(blueData, "endgameRobot1"));
        mBinding.breakdown2018EndgameRedRobot2.setText(getIntDefault(redData, "endgameRobot2"));
        mBinding.breakdown2018EndgameBlueRobot2.setText(getIntDefault(blueData, "endgameRobot2"));
        mBinding.breakdown2018EndgameRedRobot3.setText(getIntDefault(redData, "endgameRobot3"));
        mBinding.breakdown2018EndgameBlueRobot3.setText(getIntDefault(blueData, "endgameRobot3"));

        /* Endgame Points */
        mBinding.breakdown2018EndgameRedTotal.setText(getIntDefault(redData, "endgamePoints"));
        mBinding.breakdown2018EndgameBlueTotal.setText(getIntDefault(blueData, "endgamePoints"));

        /* Teleop Total Points */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Auto Quest */
        mBinding.breakdown2018RedAutoQuest.setImageResource(getRankingPointResource(getBooleanDefault(redData, "autoQuestRankingPoint")));
        mBinding.breakdown2018BlueAutoQuest.setImageResource(getRankingPointResource(getBooleanDefault(blueData, "autoQuestRankingPoint")));

        /* Face the Boss */
        mBinding.breakdown2018RedFaceTheBoss.setImageResource(getRankingPointResource(getBooleanDefault(redData, "faceTheBossRankingPoint")));
        mBinding.breakdown2018BlueFaceTheBoss.setImageResource(getRankingPointResource(getBooleanDefault(blueData, "faceTheBossRankingPoint")));

        /* Fouls */
        mBinding.breakdownFoulsRed.setText(res.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(redData, "foulPoints")));
        mBinding.breakdownFoulsBlue.setText(res.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(blueData, "foulPoints")));

        /* Adjustment points */
        mBinding.breakdownAdjustRed.setText(getIntDefault(redData, "adjustPoints"));
        mBinding.breakdownAdjustBlue.setText(getIntDefault(blueData, "adjustPoints"));

        /* Total Points */
        mBinding.breakdownTotalRed.setText(getIntDefault(redData, "totalPoints"));
        mBinding.breakdownTotalBlue.setText(getIntDefault(blueData, "totalPoints"));

        /* Show RPs earned, if needed */
        if (!matchType.isPlayoff()) {
            mBinding.breakdownRedRp.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "rp")));
            mBinding.breakdownBlueRp.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "rp")));
        } else {
            mBinding.breakdownRedRp.setVisibility(GONE);
            mBinding.breakdownBlueRp.setVisibility(GONE);
            mBinding.breakdownRpHeader.setVisibility(GONE);
        }

        mBinding.breakdown2018Container.setVisibility(View.VISIBLE);
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
