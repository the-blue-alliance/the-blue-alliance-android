package com.thebluealliance.androidclient.views.breakdowns;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2016Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

public class MatchBreakdownView2016 extends AbstractMatchBreakdownView {
    private MatchBreakdown2016Binding mBinding;

    public MatchBreakdownView2016(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MatchBreakdownView2016(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2016(Context context) {
        super(context);
    }

    @Override
    void init() {
        mBinding = MatchBreakdown2016Binding.inflate(LayoutInflater.from(getContext()), this, true);
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2016, this, true);
    }

    public boolean initWithData(MatchType matchType,
                                String winningAlliance,
                                IMatchAlliancesContainer allianceData,
                                JsonObject scoredata) {
        if (scoredata == null || scoredata.entrySet().isEmpty()
                || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2016Container.setVisibility(GONE);
            return false;
        }

        List<String> redTeams = allianceData.getRed().getTeamKeys();
        List<String> blueTeams = allianceData.getBlue().getTeamKeys();
        JsonObject redData = scoredata.get("red").getAsJsonObject();
        JsonObject blueData = scoredata.get("blue").getAsJsonObject();

        mBinding.breakdownRed1.setText(MatchBreakdownHelper.teamNumberFromKey(redTeams.get(0)));
        mBinding.breakdownRed2.setText(MatchBreakdownHelper.teamNumberFromKey(redTeams.get(1)));
        mBinding.breakdownRed3.setText(MatchBreakdownHelper.teamNumberFromKey(redTeams.get(2)));

        mBinding.breakdownBlue1.setText(MatchBreakdownHelper.teamNumberFromKey(blueTeams.get(0)));
        mBinding.breakdownBlue2.setText(MatchBreakdownHelper.teamNumberFromKey(blueTeams.get(1)));
        mBinding.breakdownBlue3.setText(MatchBreakdownHelper.teamNumberFromKey(blueTeams.get(2)));

        mBinding.breakdownAutoBoulderRed.setText(getAutoBoulder(redData));
        mBinding.breakdownAutoBoulderBlue.setText(getAutoBoulder(blueData));
        mBinding.breakdownAutoReachRed.setText(MatchBreakdownHelper.getIntDefault(redData, "autoReachPoints"));
        mBinding.breakdownAutoReachBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "autoReachPoints"));
        mBinding.breakdownAutoCrossRed.setText(MatchBreakdownHelper.getIntDefault(redData, "autoCrossingPoints"));
        mBinding.breakdownAutoCrossBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "autoCrossingPoints"));
        mBinding.breakdownAutoTotalRed.setText(MatchBreakdownHelper.getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "autoPoints"));

        mBinding.breakdownRedDefense1Name.setText(getCrossValue(redData, "position1"));
        mBinding.breakdownRedDefense1Cross.setText(getCrossValue(redData, "position1crossings"));
        mBinding.breakdownBlueDefense1Name.setText(getCrossValue(blueData, "position1"));
        mBinding.breakdownBlueDefense1Cross.setText(getCrossValue(blueData, "position1crossings"));

        mBinding.breakdownRedDefense2Name.setText(getDefenseName(redData, "position2"));
        mBinding.breakdownRedDefense2Cross.setText(getCrossValue(redData, "position2crossings"));
        mBinding.breakdownBlueDefense2Name.setText(getDefenseName(blueData, "position2"));
        mBinding.breakdownBlueDefense2Cross.setText(getCrossValue(blueData, "position2crossings"));

        mBinding.breakdownRedDefense3Name.setText(getDefenseName(redData, "position3"));
        mBinding.breakdownRedDefense3Cross.setText(getCrossValue(redData, "position3crossings"));
        mBinding.breakdownBlueDefense3Name.setText(getDefenseName(blueData, "position3"));
        mBinding.breakdownBlueDefense3Cross.setText(getCrossValue(blueData, "position3crossings"));

        mBinding.breakdownRedDefense4Name.setText(getDefenseName(redData, "position4"));
        mBinding.breakdownRedDefense4Cross.setText(getCrossValue(redData, "position4crossings"));
        mBinding.breakdownBlueDefense4Name.setText(getDefenseName(blueData, "position4"));
        mBinding.breakdownBlueDefense4Cross.setText(getCrossValue(blueData, "position4crossings"));

        mBinding.breakdownRedDefense5Name.setText(getDefenseName(redData, "position5"));
        mBinding.breakdownRedDefense5Cross.setText(getCrossValue(redData, "position5crossings"));
        mBinding.breakdownBlueDefense5Name.setText(getDefenseName(blueData, "position5"));
        mBinding.breakdownBlueDefense5Cross.setText(getCrossValue(blueData, "position5crossings"));

        mBinding.breakdownTeleopCrossRed.setText(MatchBreakdownHelper.getIntDefault(redData, "teleopCrossingPoints"));
        mBinding.breakdownTeleopCrossBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "teleopCrossingPoints"));
        mBinding.breakdownTeleopHighBoulderRed.setText(MatchBreakdownHelper.getIntDefault(redData, "teleopBouldersHigh"));
        mBinding.breakdownTeleopHighBoulderBlue.setText(MatchBreakdownHelper
                                            .getIntDefault(blueData, "teleopBouldersHigh"));
        mBinding.breakdownTeleopLowBoulderRed.setText(MatchBreakdownHelper.getIntDefault(redData, "teleopBouldersLow"));
        mBinding.breakdownTeleopLowBoulderBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "teleopBouldersLow"));
        mBinding.breakdownTeleopTotalBoulderRed.setText(MatchBreakdownHelper.getIntDefault(redData, "teleopBoulderPoints"));
        mBinding.breakdownTeleopTotalBoulderBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "teleopBoulderPoints"));
        mBinding.breakdownTeleopChallengeRed.setText(MatchBreakdownHelper
                                          .getIntDefault(redData, "teleopChallengePoints"));
        mBinding.breakdownTeleopChallengeBlue.setText(MatchBreakdownHelper
                                           .getIntDefault(blueData, "teleopChallengePoints"));
        mBinding.breakdownTeleopScaleRed.setText(MatchBreakdownHelper.getIntDefault(redData, "teleopScalePoints"));
        mBinding.breakdownTeleopScaleBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "teleopScalePoints"));
        mBinding.breakdownTeleopTotalRed.setText(getTeleopTotal(redData));
        mBinding.breakdownTeleopTotalBlue.setText(getTeleopTotal(blueData));

        boolean redBreachSuccess = MatchBreakdownHelper
                .getBooleanDefault(redData, "teleopDefensesBreached");
        mBinding.breakdown2016BreachIconRed.setImageResource(redBreachSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);

        boolean blueBreachSuccess = MatchBreakdownHelper
                .getBooleanDefault(blueData, "teleopDefensesBreached");
        mBinding.breakdown2016BreachIconBlue.setImageResource(blueBreachSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);

        int redBreachPoints = MatchBreakdownHelper.getIntDefaultValue(redData, "breachPoints");
        if (redBreachSuccess && matchType == MatchType.QUAL) {
            mBinding.breakdown2016BreachPointsRed.setText(getContext().getString(R.string.breakdown_rp_format, 1));
            mBinding.breakdown2016BreachPointsRed.setVisibility(VISIBLE);
        } else if (redBreachPoints > 0) {
            mBinding.breakdown2016BreachPointsRed.setText(getContext().getString(R.string.breakdown_addition_format, redBreachPoints));
            mBinding.breakdown2016BreachPointsRed.setVisibility(VISIBLE);
        } else {
            mBinding.breakdown2016BreachPointsRed.setVisibility(GONE);
        }

        int blueBreachPoints = MatchBreakdownHelper.getIntDefaultValue(blueData, "breachPoints");
        if (blueBreachSuccess && matchType == MatchType.QUAL) {
            mBinding.breakdown2016BreachPointsBlue.setText(getContext().getString(R.string.breakdown_rp_format, 1));
            mBinding.breakdown2016BreachPointsBlue.setVisibility(VISIBLE);
        } else if (blueBreachPoints > 0) {
            mBinding.breakdown2016BreachPointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, blueBreachPoints));
            mBinding.breakdown2016BreachPointsBlue.setVisibility(VISIBLE);
        } else {
            mBinding.breakdown2016BreachPointsBlue.setVisibility(GONE);
        }

        boolean redCaptureSuccess = MatchBreakdownHelper
                .getBooleanDefault(redData, "teleopTowerCaptured");
        mBinding.breakdown2016CaptureIconRed.setImageResource(redCaptureSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);
        boolean blueCaptureSuccess = MatchBreakdownHelper
                .getBooleanDefault(blueData, "teleopTowerCaptured");
        mBinding.breakdown2016CaptureIconBlue.setImageResource(blueCaptureSuccess
            ? R.drawable.ic_done_black_24dp
            : R.drawable.ic_clear_black_24dp);

        int redCapturePoints = MatchBreakdownHelper.getIntDefaultValue(redData, "capturePoints");
        if (redCaptureSuccess && matchType == MatchType.QUAL) {
            mBinding.breakdown2016CapturePointsRed.setText(getContext().getString(R.string.breakdown_rp_format, 1));
            mBinding.breakdown2016CapturePointsRed.setVisibility(VISIBLE);
        } else if (redCapturePoints > 0) {
            mBinding.breakdown2016CapturePointsRed.setText(getContext().getString(R.string.breakdown_addition_format, redCapturePoints));
            mBinding.breakdown2016CapturePointsRed.setVisibility(VISIBLE);
        } else {
            mBinding.breakdown2016CapturePointsRed.setVisibility(GONE);
        }

        int blueCapturePoints = MatchBreakdownHelper.getIntDefaultValue(blueData, "capturePoints");
        if (blueCaptureSuccess && matchType == MatchType.QUAL) {
            mBinding.breakdown2016CapturePointsBlue.setText(getContext().getString(R.string.breakdown_rp_format, 1));
            mBinding.breakdown2016CapturePointsBlue.setVisibility(VISIBLE);
        } else if (blueCapturePoints > 0) {
            mBinding.breakdown2016CapturePointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, blueCapturePoints));
            mBinding.breakdown2016CapturePointsBlue.setVisibility(VISIBLE);
        } else {
            mBinding.breakdown2016CapturePointsBlue.setVisibility(GONE);
        }

        mBinding.breakdownFoulsRed.setText(getContext().getString(R.string.breakdown_foul_format_add, MatchBreakdownHelper
                .getIntDefaultValue(redData, "foulPoints")));
        mBinding.breakdownFoulsBlue.setText(getContext().getString(R.string.breakdown_foul_format_add, MatchBreakdownHelper
                .getIntDefaultValue(blueData, "foulPoints")));
        mBinding.breakdownAdjustRed.setText(MatchBreakdownHelper.getIntDefault(redData, "adjustPoints"));
        mBinding.breakdownAdjustBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "adjustPoints"));
        mBinding.breakdownTotalRed.setText(MatchBreakdownHelper.getIntDefault(redData, "totalPoints"));
        mBinding.breakdownTotalBlue.setText(MatchBreakdownHelper.getIntDefault(blueData, "totalPoints"));

         /* Show RPs earned, if needed */
        boolean showRp = !redData.get("tba_rpEarned").isJsonNull()
                         && !blueData.get("tba_rpEarned").isJsonNull();
        if (showRp) {
            mBinding.breakdownRedRp.setText(getContext().getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "tba_rpEarned")));
            mBinding.breakdownBlueRp.setText(getContext().getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "tba_rpEarned")));
        } else {
            mBinding.breakdownRedRp.setVisibility(GONE);
            mBinding.breakdownBlueRp.setVisibility(GONE);
            findViewById(R.id.breakdown_rp_header).setVisibility(GONE);
        }

        mBinding.breakdown2016Container.setVisibility(VISIBLE);
        return true;
    }

    private static String getTeleopTotal(JsonObject data) {
        return Integer.toString(MatchBreakdownHelper.getIntDefaultValue(data, "teleopCrossingPoints")
                                + MatchBreakdownHelper
                                        .getIntDefaultValue(data, "teleopBoulderPoints")
                                + MatchBreakdownHelper
                                        .getIntDefaultValue(data, "teleopChallengePoints")
                                + MatchBreakdownHelper.getIntDefaultValue(data, "teleopScalePoints"));
    }

    private static String getAutoBoulder(JsonObject data) {
        return Integer.toString(MatchBreakdownHelper.getIntDefaultValue(data, "autoBouldersHigh")
                                + MatchBreakdownHelper.getIntDefaultValue(data, "autoBouldersLow"));
    }

    private String getCrossValue(JsonObject data, String key) {
        int crossCount = MatchBreakdownHelper.getIntDefaultValue(data, key);
        return getContext().getString(R.string.breakdown2016_cross_format, crossCount);
    }

    private static @StringRes int getDefenseName(JsonObject data, String key) {
        if (data.has(key)) {
            String name = data.get(key).getAsString();
            switch(name) {
                case "A_ChevalDeFrise": return R.string.defense2016_cdf;
                case "A_Portcullis": return R.string.defense2016_portcullis;
                case "B_Ramparts": return R.string.defense2016_ramparts;
                case "B_Moat": return R.string.defense2016_moat;
                case "C_SallyPort": return R.string.defense2016_sally_port;
                case "C_Drawbridge": return R.string.defense2016_drawbridge;
                case "D_RoughTerrain": return R.string.defense2016_rough_terrain;
                case "D_RockWall": return R.string.defense2016_rock_wall;
                default: return R.string.defense2016_unknown;
            }
        }
        return R.string.defense2016_unknown;
    }

}
