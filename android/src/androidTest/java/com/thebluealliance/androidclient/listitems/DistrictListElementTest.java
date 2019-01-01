package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.models.District;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DistrictListElementTest {

    public static final int WIDTH_DP = 400;

    private static final District DISTRICT = new District();
    static {
        DISTRICT.setAbbreviation("ne");
        DISTRICT.setDisplayName("New England");
        DISTRICT.setYear(2016);
        DISTRICT.setKey("2016ne");
    }

    @Test
    public void testRenderWithMyTba() {
        View view = getView(DISTRICT, 4, true);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderWithoutMyTba() {
        View view = getView(DISTRICT, 4, false);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(District district, int numEvents, boolean showMyTba) {
        DistrictListElement element = new DistrictListElement(district, numEvents, showMyTba);
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }
}