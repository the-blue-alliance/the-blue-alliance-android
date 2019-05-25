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
public class EventListElementTest {

    private static final int WIDTH_DP = 400;

    @Test
    public void testRenderWithMyTba() {
        View view = getView("2016test", 2016, "Test Event", "Apr 27, 2016", "New York, NY", true);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
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
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }
}