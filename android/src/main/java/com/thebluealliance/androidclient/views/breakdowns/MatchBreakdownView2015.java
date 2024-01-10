package com.thebluealliance.androidclient.views.breakdowns;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2015Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

public class MatchBreakdownView2015 extends AbstractMatchBreakdownView {
    private MatchBreakdown2015Binding mBinding;

    private static final int ROBOT_SET_POINTS = 4;
    private static final int TOTE_SET_POINTS = 6;
    private static final int CONTAINER_SET_POINTS = 8;
    private static final int TOTE_STACK_POINTS = 20;
    private static final int COOP_SET_POINTS = 20;
    private static final int COOP_STACK_POINTS = 40;

    public MatchBreakdownView2015(Context context) {
        super(context);
    }

    public MatchBreakdownView2015(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2015(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        mBinding = MatchBreakdown2015Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public boolean initWithData(MatchType matchType,
                                String winningAlliance,
                                IMatchAlliancesContainer allianceData,
                                JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
            || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2015Container.setVisibility(GONE);
            return false;
        }

        List<String> redTeams = allianceData.getRed().getTeamKeys();
        List<String> blueTeams = allianceData.getBlue().getTeamKeys();
        JsonObject redData = scoreData.get("red").getAsJsonObject();
        JsonObject blueData = scoreData.get("blue").getAsJsonObject();

        mBinding.breakdownRed1.setText(teamNumberFromKey(redTeams.get(0)));
        mBinding.breakdownRed2.setText(teamNumberFromKey(redTeams.get(1)));
        mBinding.breakdownRed3.setText(teamNumberFromKey(redTeams.get(2)));

        mBinding.breakdownBlue1.setText(teamNumberFromKey(blueTeams.get(0)));
        mBinding.breakdownBlue2.setText(teamNumberFromKey(blueTeams.get(1)));
        mBinding.breakdownBlue3.setText(teamNumberFromKey(blueTeams.get(2)));

        /* Auto Totes */
        if (redData.get("tote_set").getAsBoolean()) {
            mBinding.breakdown2015AutoTotePointsRed.setText(getContext().getString(R.string.breakdown_addition_format, TOTE_SET_POINTS));
            mBinding.breakdown2015AutoTotesIconRed.setImageResource(R.drawable.ic_more_horiz_black_24dp);
        } else if (redData.get("tote_stack").getAsBoolean()) {
            mBinding.breakdown2015AutoTotePointsRed.setText(getContext().getString(R.string.breakdown_addition_format, TOTE_STACK_POINTS));
            mBinding.breakdown2015AutoTotesIconRed.setImageResource(R.drawable.ic_menu_black_24dp);
        } else {
            mBinding.breakdown2015AutoTotePointsRed.setVisibility(GONE);
            mBinding.breakdown2015AutoTotesIconRed.setImageResource(R.drawable.ic_clear_black_24dp);
        }
        if (blueData.get("tote_set").getAsBoolean()) {
            mBinding.breakdown2015AutoTotePointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, TOTE_SET_POINTS));
            mBinding.breakdown2015AutoTotesIconBlue.setImageResource(R.drawable.ic_more_horiz_black_24dp);
        } else if (blueData.get("tote_stack").getAsBoolean()) {
            mBinding.breakdown2015AutoTotePointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, TOTE_STACK_POINTS));
            mBinding.breakdown2015AutoTotesIconBlue.setImageResource(R.drawable.ic_menu_black_24dp);
        } else {
            mBinding.breakdown2015AutoTotePointsBlue.setVisibility(GONE);
            mBinding.breakdown2015AutoTotesIconBlue.setImageResource(R.drawable.ic_clear_black_24dp);
        }

        /* Auto Containers */
        if (redData.get("container_set").getAsBoolean()) {
            mBinding.breakdown2015AutoContainerPointsRed.setText(getContext().getString(R.string.breakdown_addition_format, CONTAINER_SET_POINTS));
            mBinding.breakdown2015AutoContainersIconRed.setImageResource(R.drawable.ic_more_horiz_black_24dp);
        } else {
            mBinding.breakdown2015AutoContainerPointsRed.setVisibility(GONE);
            mBinding.breakdown2015AutoContainersIconRed.setImageResource(R.drawable.ic_clear_black_24dp);
        }
        if (blueData.get("container_set").getAsBoolean()) {
            mBinding.breakdown2015AutoContainerPointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, CONTAINER_SET_POINTS));
            mBinding.breakdown2015AutoContainersIconBlue.setImageResource(R.drawable.ic_more_horiz_black_24dp);
        } else {
            mBinding.breakdown2015AutoContainerPointsBlue.setVisibility(GONE);
            mBinding.breakdown2015AutoContainersIconBlue.setImageResource(R.drawable.ic_clear_black_24dp);
        }

        /* Auto Robots */
        if (redData.get("robot_set").getAsBoolean()) {
            mBinding.breakdown2015AutoRobotPointsRed.setText(getContext().getString(R.string.breakdown_addition_format,  ROBOT_SET_POINTS));
            mBinding.breakdown2015AutoRobotsIconRed.setImageResource(R.drawable.ic_more_horiz_black_24dp);
        } else {
            mBinding.breakdown2015AutoRobotPointsRed.setVisibility(GONE);
            mBinding.breakdown2015AutoRobotsIconRed.setImageResource(R.drawable.ic_clear_black_24dp);
        }
        if (blueData.get("robot_set").getAsBoolean()) {
            mBinding.breakdown2015AutoRobotPointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, ROBOT_SET_POINTS));
            mBinding.breakdown2015AutoRobotsIconBlue.setImageResource(R.drawable.ic_more_horiz_black_24dp);
        } else {
            mBinding.breakdown2015AutoRobotPointsBlue.setVisibility(GONE);
            mBinding.breakdown2015AutoRobotsIconBlue.setImageResource(R.drawable.ic_clear_black_24dp);
        }

        /* Auto Points */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "auto_points"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "auto_points"));

        /* Teleop Points */
        int redTote = getIntDefaultValue(redData, "tote_points");
        int blueTote = getIntDefaultValue(blueData, "tote_points");
        int redContainer = getIntDefaultValue(redData, "container_points");
        int blueContainer = getIntDefaultValue(blueData, "container_points");
        int redLitter = getIntDefaultValue(redData, "litter_points");
        int blueLitter = getIntDefaultValue(blueData, "litter_points");
        mBinding.breakdown2015TotePointsRed.setText(getIntDefault(redData, "tote_points"));
        mBinding.breakdown2015TotePointsBlue.setText(getIntDefault(blueData, "tote_points"));
        mBinding.breakdown2015ContainerPointsRed.setText(getIntDefault(redData, "container_points"));
        mBinding.breakdown2015ContainerPointsBlue.setText(getIntDefault(blueData, "container_points"));
        mBinding.breakdown2015LitterPointsRed.setText(getIntDefault(redData, "litter_points"));
        mBinding.breakdown2015LitterPointsBlue.setText(getIntDefault(blueData, "litter_points"));

        mBinding.breakdownTeleopTotalRed.setText(getResources().getString(R.string.breakdown_number_format,
                                                        (redTote + redContainer + redLitter)));
        mBinding.breakdownTeleopTotalBlue.setText(getResources().getString(R.string.breakdown_number_format,
                                                         (blueTote + blueContainer + blueLitter)));

        /* Coop */
        if (matchType.isPlayoff()) {
            mBinding.breakdown2015CoopIconRed.setVisibility(GONE);
            mBinding.breakdown2015CoopIconBlue.setVisibility(GONE);
            mBinding.breakdown2015CoopPointsRed.setVisibility(GONE);
            mBinding.breakdown2015CoopPointsBlue.setVisibility(GONE);
            mBinding.breakdown2015CoopPointsRed.setText("0");
            mBinding.breakdown2015CoopPointsBlue.setText("0");
        } else if (scoreData.get("coopertition").getAsString().equals("Stack")){
            mBinding.breakdown2015CoopIconRed.setImageResource(R.drawable.ic_menu_black_24dp);
            mBinding.breakdown2015CoopIconBlue.setImageResource(R.drawable.ic_menu_black_24dp);
            mBinding.breakdown2015CoopPointsRed.setText(getContext().getString(R.string.breakdown_addition_format, COOP_STACK_POINTS));
            mBinding.breakdown2015CoopPointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, COOP_STACK_POINTS));
        } else if (scoreData.get("coopertition").getAsString().equals("Set")) {
            mBinding.breakdown2015CoopIconRed.setImageResource(R.drawable.ic_more_horiz_black_24dp);
            mBinding.breakdown2015CoopIconBlue.setImageResource(R.drawable.ic_more_horiz_black_24dp);
            mBinding.breakdown2015CoopPointsRed.setText(getContext().getString(R.string.breakdown_addition_format, COOP_SET_POINTS));
            mBinding.breakdown2015CoopPointsBlue.setText(getContext().getString(R.string.breakdown_addition_format, COOP_SET_POINTS));
        } else {
            mBinding.breakdown2015CoopIconRed.setImageResource(R.drawable.ic_clear_black_24dp);
            mBinding.breakdown2015CoopIconBlue.setImageResource(R.drawable.ic_clear_black_24dp);
        }

        /* Other Values */
        mBinding.breakdownFoulsRed.setText(getResources().getString(R.string.breakdown_foul_format_subtract,
                                                  getIntDefaultValue(redData, "foul_points")));
        mBinding.breakdownFoulsBlue.setText(getResources().getString(R.string.breakdown_foul_format_subtract,
                                                   getIntDefaultValue(blueData, "foul_points")));
        mBinding.breakdownAdjustRed.setText(getIntDefault(redData, "adjust_points"));
        mBinding.breakdownAdjustBlue.setText(getIntDefault(blueData, "adjust_points"));
        mBinding.breakdownTotalRed.setText(getIntDefault(redData, "total_points"));
        mBinding.breakdownTotalBlue.setText(getIntDefault(blueData, "total_points"));

        return true;
    }
}
