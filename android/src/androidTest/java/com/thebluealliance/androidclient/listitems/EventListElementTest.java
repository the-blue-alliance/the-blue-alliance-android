package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class EventListElementTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    public void testRenderWithMyTba() {
        View view = getView("2016test", 2016, "Test Event", "Apr 27, 2016", "New York, NY", true);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderWithoutMyTba() {
        View view = getView("2016test", 2016, "Test Event", "Apr 27, 2016", "New York, NY", false);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(
            String key,
            int year,
            String name,
            String dates,
            String location,
            boolean showMyTba) {
        EventListElement element = new EventListElement(key, year, name, dates,
                                                                 location, showMyTba);
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }
}