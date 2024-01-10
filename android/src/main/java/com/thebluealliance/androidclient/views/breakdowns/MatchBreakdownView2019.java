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
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2019Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

public class MatchBreakdownView2019 extends AbstractMatchBreakdownView {
    private MatchBreakdown2019Binding mBinding;
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
        mBinding = MatchBreakdown2019Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    @Override
    public boolean initWithData(MatchType matchType, String winningAlliance, IMatchAlliancesContainer allianceData, JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2019Container.setVisibility(GONE);
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

        /* Sandstorm Bonus */
        setSandstormBonus(redData, "1", mBinding.breakdown2019RedSandstormBonusRobot1);
        setSandstormBonus(redData, "2", mBinding.breakdown2019RedSandstormBonusRobot2);
        setSandstormBonus(redData, "3", mBinding.breakdown2019RedSandstormBonusRobot3);
        setSandstormBonus(blueData, "1", mBinding.breakdown2019BlueSandstormBonusRobot1);
        setSandstormBonus(blueData, "2", mBinding.breakdown2019BlueSandstormBonusRobot2);
        setSandstormBonus(blueData, "3", mBinding.breakdown2019BlueSandstormBonusRobot3);
        mBinding.breakdown2019SandstormTotalBlue.setText(getIntDefault(blueData, "sandStormBonusPoints"));
        mBinding.breakdown2019SandstormTotalRed.setText(getIntDefault(redData, "sandStormBonusPoints"));

        /* Scoring Objects */
        setCargoShipScoring(redData, mBinding.breakdown2019RedCargoShipNullPanels, mBinding.breakdown2019RedCargoShipPanels, mBinding.breakdown2019RedCargoShipCargo);
        setRocketScoring(redData, "Near", mBinding.breakdown2019RedRocket1Panels, mBinding.breakdown2019RedRocket1Cargo);
        setRocketScoring(redData, "Far", mBinding.breakdown2019RedRocket2Panels, mBinding.breakdown2019RedRocket2Cargo);

        setCargoShipScoring(blueData, mBinding.breakdown2019BlueCargoShipNullPanels, mBinding.breakdown2019BlueCargoShipPanels, mBinding.breakdown2019BlueCargoShipCargo);
        setRocketScoring(blueData, "Near", mBinding.breakdown2019BlueRocket1Panels, mBinding.breakdown2019BlueRocket1Cargo);
        setRocketScoring(blueData, "Far", mBinding.breakdown2019BlueRocket2Panels, mBinding.breakdown2019BlueRocket2Cargo);

        setTotalPanels(redData, mBinding.breakdown2019RedHatchPanelPoints);
        setTotalCargo(redData, mBinding.breakdown2019RedCargoPoints);
        setTotalPanels(blueData, mBinding.breakdown2019BlueHatchPanelPoints);
        setTotalCargo(blueData, mBinding.breakdown2019BlueCargoPoints);

        /* HAB Climb */
        setHabClimbBonus(redData, "1", mBinding.breakdown2019RedHabRobot1);
        setHabClimbBonus(redData, "2", mBinding.breakdown2019RedHabRobot2);
        setHabClimbBonus(redData, "3", mBinding.breakdown2019RedHabRobot3);
        mBinding.breakdown2019HabTotalRed.setText(getIntDefault(redData, "habClimbPoints"));

        setHabClimbBonus(blueData, "1", mBinding.breakdown2019BlueHabRobot1);
        setHabClimbBonus(blueData, "2", mBinding.breakdown2019BlueHabRobot2);
        setHabClimbBonus(blueData, "3", mBinding.breakdown2019BlueHabRobot3);
        mBinding.breakdown2019HabTotalBlue.setText(getIntDefault(blueData, "habClimbPoints"));

        /* Teleop Total Points */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Ranking Points */
        setRocketRankingPoint(redData, mBinding.breakdown2019RedCompleteRocket);
        setHabRankingPoint(redData, mBinding.breakdown2019RedHabDocking);
        setRocketRankingPoint(blueData, mBinding.breakdown2019BlueCompleteRocket);
        setHabRankingPoint(blueData, mBinding.breakdown2019BlueHabDocking);

        /* Fouls */
        mBinding.breakdownFoulsRed.setText(mResources.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(redData, "foulPoints")));
        mBinding.breakdownFoulsBlue.setText(mResources.getString(R.string.breakdown_foul_format_add, getIntDefaultValue(blueData, "foulPoints")));

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

        mBinding.breakdown2019Container.setVisibility(View.VISIBLE);
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
