package com.thebluealliance.androidclient.views;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MatchViewTest {

    private static final int WIDTH_DP = 400;

    private static final String MATCH_16 = "2016test_qm2";
    private static final String MATCH_15_Q = "2015test_qm2";
    private static final String MATCH_15_F = "2015test_f1m1";

    private static final String VID = "abc123";

    private static final String[] RED_TEAMS_2 = {"1124", "254"};
    private static final String[] BLUE_TEAMS_2 = {"469", "5686"};

    private static final String[] RED_TEAMS_3 = {"1124", "254", "330"};
    private static final String[] BLUE_TEAMS_3 = {"469", "5686", "600"};


    @Test
    public void testRenderUnplayed() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "?", "?", "blue", 1463883886L,
                null, MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderBlueWin() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "blue", 1463883886L,
                VID, MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderRedWin() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "40", "30", "blue", 1463883886L,
                VID, MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderTie() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "20", "blue", 1463883886L,
                VID, MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRender2Team() {
        View view = createView(RED_TEAMS_2, BLUE_TEAMS_2, "20", "30", "blue", 1463883886L,
                VID, MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderNoTime() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "blue", 0, VID,
                MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderNoVideo() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "blue", 1463883886L,
                null, MATCH_16, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testNoWinnersIn2015() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "", 1463883886L, VID,
                MATCH_15_Q, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testWinnersIn2015Finals() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "blue", 1463883886L, VID,
                               MATCH_15_F, 0, 0);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void test1RpDot() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "blue", 1463883886L, VID,
                MATCH_15_F, 1, 1);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void test2RpDot() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", "blue", 1463883886L, VID,
                MATCH_15_F, 2, 2);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private MatchView createView(String[] redTeams, String[] blueTeams, String redScore,
                                 String blueScore, String winner, long time, String video,
                                 String matchKey, int redExtraRp, int blueExtraRp) {
        LayoutInflater inflater = LayoutInflater.from(InstrumentationRegistry.getTargetContext());
        View view = inflater.inflate(R.layout.match_view, null, false);

        MatchView matchView = (MatchView) view.findViewById(R.id.match_view);
        matchView.initWithParams(video, "Quals 2", redTeams, blueTeams, redScore, blueScore, winner,
                                 matchKey, time, "", true,
                                 redExtraRp, blueExtraRp);
        return matchView;
    }
}