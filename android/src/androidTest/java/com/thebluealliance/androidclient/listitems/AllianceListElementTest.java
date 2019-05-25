package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AllianceListElementTest {

    private static final int WIDTH_DP = 400;

    private static final List<String> TEAM_LIST_3 = new ArrayList<>();
    private static final List<String> TEAM_LIST_4 = new ArrayList<>();

    static {
        TEAM_LIST_3.add("frc1124");
        TEAM_LIST_3.add("frc254");
        TEAM_LIST_3.add("frc330");

        TEAM_LIST_4.add("frc1124");
        TEAM_LIST_4.add("frc254");
        TEAM_LIST_4.add("frc330");
        TEAM_LIST_4.add("frc4334");
    }

    @Test
    public void testRender3Team() {
        View view = getView("2016test", "Alliance 1", 1, TEAM_LIST_3, PlayoffAdvancement.SEMI);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRender4Team() {
        View view = getView("2016test", "Alliance 1", 1, TEAM_LIST_4, PlayoffAdvancement.QUARTER);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(
            String eventKey,
            String allianceName,
            int number,
            List<String> teams,
            PlayoffAdvancement advancement) {
        AllianceListElement element = new AllianceListElement(eventKey, allianceName, number,
                                                              teams, advancement);
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}