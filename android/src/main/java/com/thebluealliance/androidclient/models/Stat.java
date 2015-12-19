package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import java.text.DecimalFormat;

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
    public StatsListElement render(ModelRendererSupplier supplier) {
        //TODO create StatsRenderer
        String teamNumber = teamKey.substring(3);
        return new StatsListElement(teamKey, teamNumber, teamName, statString, opr, dpr, ccwm);
    }
}
