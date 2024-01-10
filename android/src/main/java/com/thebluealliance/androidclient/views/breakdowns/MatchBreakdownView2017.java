package com.thebluealliance.androidclient.views.breakdowns;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.setViewVisibility;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.teamNumberFromKey;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.databinding.MatchBreakdown2017Binding;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

public class MatchBreakdownView2017 extends AbstractMatchBreakdownView {
    private MatchBreakdown2017Binding mBinding;

    private static final String AUTO_ROTOR_FORMAT = "rotor%1$dAuto";
    private static final String TELEOP_ROTOR_FORMAT = "rotor%1$dEngaged";

    public MatchBreakdownView2017(Context context) {
        super(context);
    }

    public MatchBreakdownView2017(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchBreakdownView2017(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void init() {
        mBinding = MatchBreakdown2017Binding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    @Override
    public boolean initWithData(MatchType matchType,
                                String winningAlliance,
                                IMatchAlliancesContainer allianceData,
                                JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
            || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            mBinding.breakdown2017Container.setVisibility(GONE);
            return false;
        }

        Resources res = getResources();
        List<String> redTeams = allianceData.getRed().getTeamKeys();
        List<String> blueTeams = allianceData.getBlue().getTeamKeys();
        JsonObject redData = scoreData.get("red").getAsJsonObject();
        JsonObject blueData = scoreData.get("blue").getAsJsonObject();
        int redRp = 0;
        int blueRp = 0;

        mBinding.breakdownRed1.setText(teamNumberFromKey(redTeams.get(0)));
        mBinding.breakdownRed2.setText(teamNumberFromKey(redTeams.get(1)));
        mBinding.breakdownRed3.setText(teamNumberFromKey(redTeams.get(2)));

        mBinding.breakdownBlue1.setText(teamNumberFromKey(blueTeams.get(0)));
        mBinding.breakdownBlue2.setText(teamNumberFromKey(blueTeams.get(1)));
        mBinding.breakdownBlue3.setText(teamNumberFromKey(blueTeams.get(2)));

        /* Auto Mobility */
        mBinding.breakdown2017RedAutoMobility.setText(getIntDefault(redData, "autoMobilityPoints"));
        mBinding.breakdown2017BlueAutoMobility.setText(getIntDefault(blueData, "autoMobilityPoints"));

        /* Auto Fuel */
        mBinding.breakdown2017RedAutoFuelHigh.setText(getIntDefault(redData, "autoFuelHigh"));
        mBinding.breakdown2017BlueAutoFuelHigh.setText(getIntDefault(blueData, "autoFuelHigh"));
        mBinding.breakdown2017RedAutoFuelLow.setText(getIntDefault(redData, "autoFuelLow"));
        mBinding.breakdown2017BlueAutoFuelLow.setText(getIntDefault(blueData, "autoFuelLow"));
        mBinding.breakdown2017RedAutoPressure.setText(getIntDefault(redData, "autoFuelPoints"));
        mBinding.breakdown2017BlueAutoPressure.setText(getIntDefault(blueData, "autoFuelPoints"));

        /* Auto Rotors */
        setViewVisibility(mBinding.breakdown2017AutoRotor1Red, getBooleanDefault(redData, "rotor1Auto"));
        setViewVisibility(mBinding.breakdown2017AutoRotor2Red, getBooleanDefault(redData, "rotor2Auto"));
        setViewVisibility(mBinding.breakdown2017AutoRotor1Blue, getBooleanDefault(blueData, "rotor1Auto"));
        setViewVisibility(mBinding.breakdown2017AutoRotor2Blue, getBooleanDefault(blueData, "rotor2Auto"));
        mBinding.breakdown2017RedAutoRotor.setText(getIntDefault(redData, "autoRotorPoints"));
        mBinding.breakdown2017BlueAutoRotor.setText(getIntDefault(blueData, "autoRotorPoints"));

        /* Auto Total */
        mBinding.breakdownAutoTotalRed.setText(getIntDefault(redData, "autoPoints"));
        mBinding.breakdownAutoTotalBlue.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Fuel */
        mBinding.breakdown2017RedTeleopFuelHigh.setText(getIntDefault(redData, "teleopFuelHigh"));
        mBinding.breakdown2017RedTeleopFuelLow.setText(getIntDefault(redData, "teleopFuelLow"));
        mBinding.breakdown2017BlueTeleopFuelHigh.setText(getIntDefault(blueData, "teleopFuelHigh"));
        mBinding.breakdown2017BlueTeleopFuelLow.setText(getIntDefault(blueData, "teleopFuelLow"));
        mBinding.breakdown2017RedTeleopPressure.setText(getIntDefault(redData, "teleopFuelPoints"));
        mBinding.breakdown2017BlueTeleopPressure.setText(getIntDefault(blueData, "teleopFuelPoints"));

        /* Teleop Rotors */
        setRotorView(mBinding.breakdown2017TeleopRotor1Red, redData, 1);
        setRotorView(mBinding.breakdown2017TeleopRotor2Red, redData, 2);
        setRotorView(mBinding.breakdown2017TeleopRotor3Red, redData, 3);
        setRotorView(mBinding.breakdown2017TeleopRotor4Red, redData, 4);
        setRotorView(mBinding.breakdown2017TeleopRotor1Blue, blueData, 1);
        setRotorView(mBinding.breakdown2017TeleopRotor2Blue, blueData, 2);
        setRotorView(mBinding.breakdown2017TeleopRotor3Blue, blueData, 3);
        setRotorView(mBinding.breakdown2017TeleopRotor4Blue, blueData, 4);
        mBinding.breakdown2017RedTeleopRotor.setText(getIntDefault(redData, "teleopRotorPoints"));
        mBinding.breakdown2017BlueTeleopRotor.setText(getIntDefault(blueData, "teleopRotorPoints"));

        /* Takeoff Points */
        mBinding.breakdown2017RedTeleopTakeoff.setText(getIntDefault(redData, "teleopTakeoffPoints"));
        mBinding.breakdown2017BlueTeleopTakeoff.setText(getIntDefault(blueData, "teleopTakeoffPoints"));

        /* Teleop Total */
        mBinding.breakdownTeleopTotalRed.setText(getIntDefault(redData, "teleopPoints"));
        mBinding.breakdownTeleopTotalBlue.setText(getIntDefault(blueData, "teleopPoints"));

        /* Pressure Bonus */
        setPressureBonus(res, mBinding.breakdown2017RedPressureIcon, mBinding.breakdown2017RedPressureReached, redData);
        setPressureBonus(res, mBinding.breakdown2017BluePressureIcon, mBinding.breakdown2017BluePressureReached, blueData);

        /* Rotor Bonus */
        setRotorBonus(res, mBinding.breakdown2017RedAllRotorsIcon, mBinding.breakdown2017RedAllRotorsPoints, redData);
        setRotorBonus(res, mBinding.breakdown2017BlueAllRotorsIcon, mBinding.breakdown2017BlueAllRotorsPoints, blueData);

        /* Overall Stuff */
        mBinding.breakdownFoulsRed.setText(res.getString(R.string.breakdown_foul_format_add,
                                       getIntDefaultValue(redData, "foulPoints")));
        mBinding.breakdownFoulsBlue.setText(res.getString(R.string.breakdown_foul_format_add,
                                        getIntDefaultValue(blueData, "foulPoints")));
        mBinding.breakdownAdjustRed.setText(getIntDefault(redData, "adjustPoints"));
        mBinding.breakdownAdjustBlue.setText(getIntDefault(blueData, "adjustPoints"));
        mBinding.breakdownTotalBlue.setText(getIntDefault(redData, "totalPoints"));
        mBinding.breakdownTotalBlue.setText(getIntDefault(blueData, "totalPoints"));

        /* Show RPs earned, if needed */
        boolean showRp = !redData.get("tba_rpEarned").isJsonNull()
                         && !blueData.get("tba_rpEarned").isJsonNull();

        if (showRp) {
            mBinding.breakdownRedRp.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "tba_rpEarned")));
            mBinding.breakdownBlueRp.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "tba_rpEarned")));
        } else {
            mBinding.breakdownRedRp.setVisibility(GONE);
            mBinding.breakdownBlueRp.setVisibility(GONE);
            mBinding.breakdownRpHeader.setVisibility(GONE);
        }

        mBinding.breakdown2017Container.setVisibility(VISIBLE);
        return true;
    }

    private static void setPressureBonus(Resources res, ImageView icon, TextView text, JsonObject data) {
        bonusCommon(res, icon, text, data, "kPaRankingPointAchieved", "kPaBonusPoints");
    }

    private static void setRotorBonus(Resources res, ImageView icon, TextView text, JsonObject data) {
        bonusCommon(res, icon, text, data, "rotorRankingPointAchieved", "rotorBonusPoints");
    }

    private static void bonusCommon(Resources res, ImageView icon, TextView text, JsonObject data,
                                    String rpKey, String bonusKey) {
        boolean rp = getBooleanDefault(data, rpKey);
        int bonus = getIntDefaultValue(data, bonusKey);

        if (rp) {
            icon.setImageResource(R.drawable.ic_check_black_24dp);
            text.setText(res.getString(R.string.breakdown_rp_format, 1));
        } else if (bonus > 0) {
            icon.setImageResource(R.drawable.ic_check_black_24dp);
            text.setText(res.getString(R.string.breakdown_addition_format, bonus));
        } else {
            icon.setImageResource(R.drawable.ic_clear_black_24dp);
            text.setVisibility(GONE);
        }
    }

    private static void setRotorView(ImageView view, JsonObject data, int rotorNum) {
        boolean autoEngaged = false;
        if (rotorNum == 1 || rotorNum == 2) {
            autoEngaged = getBooleanDefault(data, String.format(AUTO_ROTOR_FORMAT, rotorNum));
        }

        if (autoEngaged) {
            view.setImageResource(R.drawable.ic_check_circle_black_24dp);
        } else if (getBooleanDefault(data, String.format(TELEOP_ROTOR_FORMAT, rotorNum))) {
            view.setImageResource(R.drawable.ic_check_black_24dp);
        } else {
            view.setVisibility(GONE);
        }
    }
}
