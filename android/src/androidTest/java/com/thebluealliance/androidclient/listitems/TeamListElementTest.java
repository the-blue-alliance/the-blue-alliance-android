package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class TeamListElementTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    public void testRenderWithMyTba() {
        View view = getView("frc1124", 1124, "UberBots", "Avon, CT", true, false);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderWithoutMyTba() {
        View view = getView("frc1124", 1124, "UberBots", "Avon, CT", false, true);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(String key,
                         int number,
                         String name,
                         String location,
                         boolean showLinkToTeamDetails,
                         boolean showMyTbaDetails) {
        TeamListElement element = new TeamListElement(key, number, name, location,
                                                      showLinkToTeamDetails, showMyTbaDetails);
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}