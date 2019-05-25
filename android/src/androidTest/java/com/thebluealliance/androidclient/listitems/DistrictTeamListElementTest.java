package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DistrictTeamListElementTest {

    private static final int WIDTH_DP = 400;

    @Test
    public void testRender() {
        View view = getView("frc1124", "2016ne", "UberBots", 2, 120);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
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
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}