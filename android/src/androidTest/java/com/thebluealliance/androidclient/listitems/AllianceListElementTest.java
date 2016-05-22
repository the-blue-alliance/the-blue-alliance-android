package com.thebluealliance.androidclient.listitems;

import com.google.gson.JsonArray;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class AllianceListElementTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    private static final JsonArray TEAM_LIST_3 = new JsonArray();
    private static final JsonArray TEAM_LIST_4 = new JsonArray();

    static {
        TEAM_LIST_3.add("frc1124");
        TEAM_LIST_3.add("frc254");
        TEAM_LIST_3.add("frc330");

        TEAM_LIST_4.add("frc1124");
        TEAM_LIST_4.add("frc254");
        TEAM_LIST_4.add("frc330");
        TEAM_LIST_4.add("frc4334");
    }

    public void testRender3Team() {
        View view = getView("2016test", 1, TEAM_LIST_3, PlayoffAdvancement.SEMI);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRender4Team() {
        View view = getView("2016test", 1, TEAM_LIST_4, PlayoffAdvancement.QUARTER);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(
            String eventKey,
            int number,
            JsonArray teams,
            PlayoffAdvancement advancement) {
        AllianceListElement element = new AllianceListElement(eventKey, number, teams, advancement);
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}