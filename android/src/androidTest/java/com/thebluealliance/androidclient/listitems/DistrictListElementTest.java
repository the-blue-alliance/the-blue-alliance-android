package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.types.DistrictType;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class DistrictListElementTest extends InstrumentationTestCase {

    public static final int WIDTH_DP = 400;

    private static final District DISTRICT = new District();
    static {
        DISTRICT.setAbbreviation("ne");
        DISTRICT.setEnum(DistrictType.NEW_ENGLAND.ordinal());
        DISTRICT.setName("New England");
        DISTRICT.setYear(2016);
        DISTRICT.setKey("2016ne");
    }

    public void testRenderWithMyTba() {
        View view = getView(DISTRICT, 4, true);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

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
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }
}