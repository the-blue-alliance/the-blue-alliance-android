package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.testing.ModelMaker;
import com.thebluealliance.api.model.IAwardRecipient;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class CardedAwardListElementTest {

    private static final int WIDTH_DP = 400;
    private static final List<IAwardRecipient> SINGLE_TEAM = new ArrayList<>();
    private static final List<IAwardRecipient> MULTI_TEAM = new ArrayList<>();
    private static final List<IAwardRecipient> INDIVIDUAL = new ArrayList<>();
    private static final List<IAwardRecipient> INDIVIDUAL_NO_TEAM = new ArrayList<>();
    private static final List<IAwardRecipient> MULTI_INDIVIDUAL = new ArrayList<>();
    private static final Map<String, Team> TEAM_MAP = new HashMap<>();

    static {
        SINGLE_TEAM.add(buildWinnerDict(null, "frc1124"));
        MULTI_TEAM.add(buildWinnerDict(null, "frc1124"));
        MULTI_TEAM.add(buildWinnerDict(null, "frc254"));
        INDIVIDUAL.add(buildWinnerDict("Foo Bar", "frc1124"));
        INDIVIDUAL_NO_TEAM.add(buildWinnerDict("Foo Bar",  null));
        MULTI_INDIVIDUAL.add(buildWinnerDict("Foo Bar", "frc1124"));
        MULTI_INDIVIDUAL.add(buildWinnerDict("Foo Baz", "frc254"));

        Team team = ModelMaker.getModel(Team.class, "frc1124");
        TEAM_MAP.put("frc1124", team);
        TEAM_MAP.put("frc254", team);
    }

    @Test
    public void testRenderSingleTeam() {
        View view = getView(null, "Test Award", "2016test", SINGLE_TEAM, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderSelectedTeam() {
        View view = getView(null, "Test Award", "2016test", SINGLE_TEAM, TEAM_MAP, "1124");
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderMultiWinner() {
        View view = getView(null, "Test Award", "2016test", MULTI_TEAM, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderIndividual() {
        View view = getView(null, "Test Award", "2016test", INDIVIDUAL, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderIndividualNoTeam() {
        View view = getView(null, "Test Award", "2016test", INDIVIDUAL_NO_TEAM, TEAM_MAP, null);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
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
            List<IAwardRecipient> winners,
            Map<String, Team> teams,
            String selectedTeamKey) {
        CardedAwardListElement element = new CardedAwardListElement(datafeed, name, eventKey,
                                                                    winners, teams,
                                                                    selectedTeamKey);
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

    private static IAwardRecipient buildWinnerDict(String awardee, String teamKey) {
        Award.AwardRecipient winner = new Award.AwardRecipient();
        if (awardee != null) winner.setAwardee(awardee);
        if (teamKey != null) winner.setTeamKey(teamKey);
        return winner;
    }
}