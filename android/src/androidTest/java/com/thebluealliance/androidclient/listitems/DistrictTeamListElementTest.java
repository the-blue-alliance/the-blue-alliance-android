package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class DistrictTeamListElementTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    public void testRender() {
        View view = getView("frc1124", "2016ne", "UberBots", 2, 120);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderNoName() {
        View view = getView("frc1124", "2016ne", "", 2, 120);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(String teamKey, String districtKey, String teamName, int rank, int points) {
        DistrictTeamListElement element = new DistrictTeamListElement(teamKey, districtKey,
                                                                      teamName, rank, points);
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}