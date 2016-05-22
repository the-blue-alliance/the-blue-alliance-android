package com.thebluealliance.androidclient.views;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.R;

import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class MatchViewTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    private static final String MATCH_16 = "2016test_qm2";
    private static final String MATCH_15_Q = "2015test_qm2";
    private static final String MATCH_15_F = "2015test_f1m1";

    private static final String VID = "abc123";

    private static final String[] RED_TEAMS_2 = {"1124", "254"};
    private static final String[] BLUE_TEAMS_2 = {"469", "5686"};

    private static final String[] RED_TEAMS_3 = {"1124", "254", "330"};
    private static final String[] BLUE_TEAMS_3 = {"469", "5686", "600"};


    public void testRenderUnplayed() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "?", "?", 1463883886L, null, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderBlueWin() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", 1463883886L, VID, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderRedWin() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "40", "30", 1463883886L, VID, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderTie() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "20", 1463883886L, VID, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRender2Team() {
        View view = createView(RED_TEAMS_2, BLUE_TEAMS_2, "20", "30", 1463883886L, VID, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderNoTime() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", 0, VID, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderNoVideo() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", 1463883886L, null, MATCH_16);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testNoWinnersIn2015() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", 1463883886L, VID, MATCH_15_Q);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testWinnersIn2015Finals() {
        View view = createView(RED_TEAMS_3, BLUE_TEAMS_3, "20", "30", 1463883886L, VID, MATCH_15_F);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private MatchView createView(String[] redTeams, String[] blueTeams, String redScore,
                                 String blueScore, long time, String video, String matchKey) {
        LayoutInflater inflater = LayoutInflater.from(getInstrumentation().getTargetContext());
        View view = inflater.inflate(R.layout.match_view, null, false);

        MatchView matchView = (MatchView) view.findViewById(R.id.match_view);
        matchView.initWithParams(video, "Quals 2", redTeams, blueTeams, redScore, blueScore,
                                 matchKey, time, "", true);
        return matchView;
    }
}