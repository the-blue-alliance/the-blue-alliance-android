package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.StatsListElement;

import java.text.DecimalFormat;

/**
 * File created by phil on 6/3/14.
 */
public class Stat implements RenderableModel {

    String teamKey, teamName, location, statString;
    Double opr, dpr, ccwm;

    public static DecimalFormat displayFormat = new DecimalFormat("#.##");

    public Stat(String teamKey, String teamName, String location, String statString, Double opr, Double dpr, Double ccwm) {
        this.teamKey = teamKey;
        this.teamName = teamName;
        this.location = location;
        this.statString = statString;
        this.opr = opr;
        this.dpr = dpr;
        this.ccwm = ccwm;
    }

    @Override
    public StatsListElement render() {
        String teamNumber = teamKey.substring(3);
        return new StatsListElement(teamKey, teamNumber, teamName, statString, opr, dpr, ccwm);
    }
}
