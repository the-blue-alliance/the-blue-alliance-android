package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TeamListElementTest {

    private static final int WIDTH_DP = 400;

    @Test
    public void testRenderWithMyTba() {
        View view = getView("frc1124", 1124, "UberBots", "Avon, CT", true, false);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
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
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}