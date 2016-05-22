package com.thebluealliance.androidclient.listitems;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.testing.ModelMaker;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class CardedAwardListElementTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;
    private static final JsonArray SINGLE_TEAM = new JsonArray();
    private static final JsonArray MULTI_TEAM = new JsonArray();
    private static final JsonArray INDIVIDUAL = new JsonArray();
    private static final JsonArray INDIVIDUAL_NO_TEAM = new JsonArray();
    private static final JsonArray MULTI_INDIVIDUAL = new JsonArray();
    private static final Map<String, Team> TEAM_MAP = new HashMap<>();

    static {
        SINGLE_TEAM.add(buildWinnerDict(null, 1124));
        MULTI_TEAM.add(buildWinnerDict(null, 1124));
        MULTI_TEAM.add(buildWinnerDict(null, 254));
        INDIVIDUAL.add(buildWinnerDict("Foo Bar", 1124));
        INDIVIDUAL_NO_TEAM.add(buildWinnerDict("Foo Bar",  null));
        MULTI_INDIVIDUAL.add(buildWinnerDict("Foo Bar", 1124));
        MULTI_INDIVIDUAL.add(buildWinnerDict("Foo Baz", 254));

        Team team = ModelMaker.getModel(Team.class, "frc1124");
        TEAM_MAP.put("frc1124", team);
        TEAM_MAP.put("frc254", team);
    }

    public void testRenderSingleTeam() {
        View view = getView(null, "Test Award", "2016test", SINGLE_TEAM, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderSelectedTeam() {
        View view = getView(null, "Test Award", "2016test", SINGLE_TEAM, TEAM_MAP, "1124");
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderMultiWinner() {
        View view = getView(null, "Test Award", "2016test", MULTI_TEAM, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderIndividual() {
        View view = getView(null, "Test Award", "2016test", INDIVIDUAL, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderIndividualNoTeam() {
        View view = getView(null, "Test Award", "2016test", INDIVIDUAL_NO_TEAM, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderMultiIndividual() {
        View view = getView(null, "Test Award", "2016test", MULTI_INDIVIDUAL, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(
            APICache datafeed,
            String name,
            String eventKey,
            JsonArray winners,
            Map<String, Team> teams,
            String selectedTeamKey) {
        CardedAwardListElement element = new CardedAwardListElement(datafeed, name, eventKey,
                                                                    winners, teams,
                                                                    selectedTeamKey);
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

    private static JsonObject buildWinnerDict(String awardee, Integer teamNumber) {
        JsonObject winner = new JsonObject();
        winner.add("awardee", awardee != null ? new JsonPrimitive(awardee) : null);
        winner.add("team_number", teamNumber != null ? new JsonPrimitive(teamNumber): null);
        return winner;
    }
}