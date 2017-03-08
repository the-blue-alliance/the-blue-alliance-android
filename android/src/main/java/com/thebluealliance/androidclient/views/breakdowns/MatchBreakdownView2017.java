package com.thebluealliance.androidclient.views.breakdowns;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper
        .getBooleanDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper.getIntDefault;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper
        .getIntDefaultValue;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper
        .setViewVisibility;
import static com.thebluealliance.androidclient.views.breakdowns.MatchBreakdownHelper
        .teamNumberFromKey;

public class MatchBreakdownView2017 extends AbstractMatchBreakdownView {

    @Bind(R.id.breakdown2017_container)                  GridLayout breakdownContainer;

    @Bind(R.id.breakdown_red1)                           TextView red1;
    @Bind(R.id.breakdown_blue1)                          TextView blue1;
    @Bind(R.id.breakdown_red2)                           TextView red2;
    @Bind(R.id.breakdown_blue2)                          TextView blue2;
    @Bind(R.id.breakdown_red3)                           TextView red3;
    @Bind(R.id.breakdown_blue3)                          TextView blue3;

    @Bind(R.id.breakdown2017_red_auto_mobility)          TextView redMobility;
    @Bind(R.id.breakdown2017_blue_auto_mobility)         TextView blueMobility;
    @Bind(R.id.breakdown2017_red_auto_fuel_high)         TextView redAutoFuelHigh;
    @Bind(R.id.breakdown2017_red_auto_fuel_low)          TextView redAutoFuelLow;
    @Bind(R.id.breakdown2017_blue_auto_fuel_high)        TextView blueAutoFuelHigh;
    @Bind(R.id.breakdown2017_blue_auto_fuel_low)         TextView blueAutoFuelLow;
    @Bind(R.id.breakdown2017_red_auto_pressure)          TextView redAutoPressure;
    @Bind(R.id.breakdown2017_blue_auto_pressure)         TextView blueAutoPressure;
    @Bind(R.id.breakdown2017_auto_rotor1_red)            ImageView redAutoRotor1;
    @Bind(R.id.breakdown2017_auto_rotor2_red)            ImageView redAutoRotor2;
    @Bind(R.id.breakdown2017_auto_rotor1_blue)           ImageView blueAutoRotor1;
    @Bind(R.id.breakdown2017_auto_rotor2_blue)           ImageView blueAutoRotor2;
    @Bind(R.id.breakdown2017_red_auto_rotor)             TextView redAutoRotorPoints;
    @Bind(R.id.breakdown2017_blue_auto_rotor)            TextView blueAutoRotorPoints;
    @Bind(R.id.breakdown_auto_total_red)                 TextView redAutoTotal;
    @Bind(R.id.breakdown_auto_total_blue)                TextView blueAutoTotal;

    @Bind(R.id.breakdown2017_red_teleop_fuel_high)       TextView redTeleopFuelHigh;
    @Bind(R.id.breakdown2017_red_teleop_fuel_low)        TextView redTeleopFuelLow;
    @Bind(R.id.breakdown2017_blue_teleop_fuel_high)      TextView blueTeleopFuelHigh;
    @Bind(R.id.breakdown2017_blue_teleop_fuel_low)       TextView blueTeleopFuelLow;
    @Bind(R.id.breakdown2017_red_teleop_pressure)        TextView redTeleopPressure;
    @Bind(R.id.breakdown2017_blue_teleop_pressure)       TextView blueTeleopPressure;

    @Bind(R.id.breakdown2017_teleop_rotor1_red)          ImageView redTeleopRotor1;
    @Bind(R.id.breakdown2017_teleop_rotor2_red)          ImageView redTeleopRotor2;
    @Bind(R.id.breakdown2017_teleop_rotor3_red)          ImageView redTeleopRotor3;
    @Bind(R.id.breakdown2017_teleop_rotor4_red)          ImageView redTeleopRotor4;
    @Bind(R.id.breakdown2017_teleop_rotor1_blue)         ImageView blueTeleopRotor1;
    @Bind(R.id.breakdown2017_teleop_rotor2_blue)         ImageView blueTeleopRotor2;
    @Bind(R.id.breakdown2017_teleop_rotor3_blue)         ImageView blueTeleopRotor3;
    @Bind(R.id.breakdown2017_teleop_rotor4_blue)         ImageView blueTeleopRotor4;
    @Bind(R.id.breakdown2017_red_teleop_rotor)           TextView redTeleopRotor;
    @Bind(R.id.breakdown2017_blue_teleop_rotor)          TextView blueTeleopRotor;

    @Bind(R.id.breakdown2017_red_teleop_takeoff)         TextView redTeleopTakeoff;
    @Bind(R.id.breakdown2017_blue_teleop_takeoff)        TextView blueTeleopTakeoff;
    @Bind(R.id.breakdown_teleop_total_red)               TextView redTeleopTotal;
    @Bind(R.id.breakdown_teleop_total_blue)              TextView blueTeleopTotal;

    @Bind(R.id.breakdown2017_red_pressure_icon)          ImageView redPressureIcon;
    @Bind(R.id.breakdown2017_blue_pressure_icon)         ImageView bluePressureIcon;
    @Bind(R.id.breakdown2017_red_pressure_reached)       TextView redPressureBonus;
    @Bind(R.id.breakdown2017_blue_pressure_reached)      TextView bluePressureBonus;

    @Bind(R.id.breakdown2017_red_all_rotors_icon)        ImageView redRotorsIcon;
    @Bind(R.id.breakdown2017_blue_all_rotors_icon)       ImageView blueRotorsIcon;
    @Bind(R.id.breakdown2017_red_all_rotors_points)      TextView redRotorBonus;
    @Bind(R.id.breakdown2017_blue_all_rotors_points)     TextView blueRotorBonus;

    @Bind(R.id.breakdown_fouls_red)                      TextView foulsRed;
    @Bind(R.id.breakdown_fouls_blue)                     TextView foulsBlue;
    @Bind(R.id.breakdown_adjust_red)                     TextView adjustRed;
    @Bind(R.id.breakdown_adjust_blue)                    TextView adjustBlue;
    @Bind(R.id.breakdown_total_red)                      TextView totalRed;
    @Bind(R.id.breakdown_total_blue)                     TextView totalBlue;
    @Bind(R.id.breakdown_red_rp)                         TextView rpRed;
    @Bind(R.id.breakdown_blue_rp)                        TextView rpBlue;
    @Bind(R.id.breakdown_rp_header)                      TextView rpHeader;

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
        LayoutInflater.from(getContext()).inflate(R.layout.match_breakdown_2017, this, true);
        ButterKnife.bind(this);
    }

    @Override
    public boolean initWithData(MatchType matchType,
                                String winningAlliance,
                                IMatchAlliancesContainer allianceData,
                                JsonObject scoreData) {
        if (scoreData == null || scoreData.entrySet().isEmpty()
            || allianceData == null || allianceData.getRed() == null || allianceData.getBlue() == null) {
            breakdownContainer.setVisibility(GONE);
            return false;
        }

        Resources res = getResources();
        List<String> redTeams = allianceData.getRed().getTeamKeys();
        List<String> blueTeams = allianceData.getBlue().getTeamKeys();
        JsonObject redData = scoreData.get("red").getAsJsonObject();
        JsonObject blueData = scoreData.get("blue").getAsJsonObject();
        int redRp = 0;
        int blueRp = 0;

        red1.setText(teamNumberFromKey(redTeams.get(0)));
        red2.setText(teamNumberFromKey(redTeams.get(1)));
        red3.setText(teamNumberFromKey(redTeams.get(2)));

        blue1.setText(teamNumberFromKey(blueTeams.get(0)));
        blue2.setText(teamNumberFromKey(blueTeams.get(1)));
        blue3.setText(teamNumberFromKey(blueTeams.get(2)));

        /* Auto Mobility */
        redMobility.setText(getIntDefault(redData, "autoMobilityPoints"));
        blueMobility.setText(getIntDefault(blueData, "autoMobilityPoints"));

        /* Auto Fuel */
        redAutoFuelHigh.setText(getIntDefault(redData, "autoFuelHigh"));
        blueAutoFuelHigh.setText(getIntDefault(blueData, "autoFuelHigh"));
        redAutoFuelLow.setText(getIntDefault(redData, "autoFuelLow"));
        blueAutoFuelLow.setText(getIntDefault(blueData, "autoFuelLow"));
        redAutoPressure.setText(getIntDefault(redData, "autoFuelPoints"));
        blueAutoPressure.setText(getIntDefault(blueData, "autoFuelPoints"));

        /* Auto Rotors */
        setViewVisibility(redAutoRotor1, getBooleanDefault(redData, "rotor1Auto"));
        setViewVisibility(redAutoRotor2, getBooleanDefault(redData, "rotor2Auto"));
        setViewVisibility(blueAutoRotor1, getBooleanDefault(blueData, "rotor1Auto"));
        setViewVisibility(blueAutoRotor2, getBooleanDefault(blueData, "rotor2Auto"));
        redAutoRotorPoints.setText(getIntDefault(redData, "autoRotorPoints"));
        blueAutoRotorPoints.setText(getIntDefault(blueData, "autoRotorPoints"));

        /* Auto Total */
        redAutoTotal.setText(getIntDefault(redData, "autoPoints"));
        blueAutoTotal.setText(getIntDefault(blueData, "autoPoints"));

        /* Teleop Fuel */
        redTeleopFuelHigh.setText(getIntDefault(redData, "teleopFuelHigh"));
        redTeleopFuelLow.setText(getIntDefault(redData, "teleopFuelLow"));
        blueTeleopFuelHigh.setText(getIntDefault(blueData, "teleopFuelHigh"));
        blueTeleopFuelLow.setText(getIntDefault(blueData, "teleopFuelLow"));
        redTeleopPressure.setText(getIntDefault(redData, "teleopFuelPoints"));
        blueTeleopPressure.setText(getIntDefault(blueData, "teleopFuelPoints"));

        /* Teleop Rotors */
        setRotorView(redTeleopRotor1, redData, 1);
        setRotorView(redTeleopRotor2, redData, 2);
        setRotorView(redTeleopRotor3, redData, 3);
        setRotorView(redTeleopRotor4, redData, 4);
        setRotorView(blueTeleopRotor1, blueData, 1);
        setRotorView(blueTeleopRotor2, blueData, 2);
        setRotorView(blueTeleopRotor3, blueData, 3);
        setRotorView(blueTeleopRotor4, blueData, 4);
        redTeleopRotor.setText(getIntDefault(redData, "teleopRotorPoints"));
        blueTeleopRotor.setText(getIntDefault(blueData, "teleopRotorPoints"));

        /* Takeoff Points */
        redTeleopTakeoff.setText(getIntDefault(redData, "teleopTakeoffPoints"));
        blueTeleopTakeoff.setText(getIntDefault(blueData, "teleopTakeoffPoints"));

        /* Teleop Total */
        redTeleopTotal.setText(getIntDefault(redData, "teleopPoints"));
        blueTeleopTotal.setText(getIntDefault(blueData, "teleopPoints"));

        /* Pressure Bonus */
        setPressureBonus(res, redPressureIcon, redPressureBonus, redData);
        setPressureBonus(res, bluePressureIcon, bluePressureBonus, blueData);

        /* Rotor Bonus */
        setRotorBonus(res, redRotorsIcon, redRotorBonus, redData);
        setRotorBonus(res, blueRotorsIcon, blueRotorBonus, blueData);

        /* Overall Stuff */
        foulsRed.setText(res.getString(R.string.breakdown_foul_format_add,
                                       getIntDefaultValue(redData, "foulPoints")));
        foulsBlue.setText(res.getString(R.string.breakdown_foul_format_add,
                                        getIntDefaultValue(blueData, "foulPoints")));
        adjustRed.setText(getIntDefault(redData, "adjustPoints"));
        adjustBlue.setText(getIntDefault(blueData, "adjustPoints"));
        totalRed.setText(getIntDefault(redData, "totalPoints"));
        totalBlue.setText(getIntDefault(blueData, "totalPoints"));

        /* Show RPs earned, if needed */
        boolean showRp = !redData.get("tba_rpEarned").isJsonNull()
                         && !blueData.get("tba_rpEarned").isJsonNull();

        if (showRp) {
            rpRed.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(redData, "tba_rpEarned")));
            rpBlue.setText(res.getString(R.string.breakdown_total_rp, getIntDefaultValue(blueData, "tba_rpEarned")));
        } else {
            rpRed.setVisibility(GONE);
            rpBlue.setVisibility(GONE);
            rpHeader.setVisibility(GONE);
        }

        breakdownContainer.setVisibility(VISIBLE);
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
            icon.setBackgroundResource(R.drawable.ic_check_black_24dp);
            text.setText(res.getString(R.string.breakdown_rp_format, 1));
        } else if (bonus > 0) {
            icon.setBackgroundResource(R.drawable.ic_check_black_24dp);
            text.setText(res.getString(R.string.breakdown_addition_format, bonus));
        } else {
            icon.setBackgroundResource(R.drawable.ic_clear_black_24dp);
            text.setVisibility(GONE);
        }
    }

    private static void setRotorView(ImageView view, JsonObject data, int rotorNum) {
        boolean autoEngaged = false;
        if (rotorNum == 1 || rotorNum == 2) {
            autoEngaged = getBooleanDefault(data, String.format(AUTO_ROTOR_FORMAT, rotorNum));
        }

        if (autoEngaged) {
            view.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
        } else if (getBooleanDefault(data, String.format(TELEOP_ROTOR_FORMAT, rotorNum))) {
            view.setBackgroundResource(R.drawable.ic_check_black_24dp);
        } else {
            view.setVisibility(GONE);
        }
    }
}
