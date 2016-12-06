package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

@RunWith(AndroidJUnit4.class)
public class StatsListElementTest {

    private static final int WIDTH_DP = 400;

    @Test
    public void testRender() {
        View view = getView("frc1124", "1124", "UberBots", "11.24", 1.2, 3.4, 5.6);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(String key, String number, String name, String stat, Double opr, Double dpr, Double ccwm) {
        StatsListElement element = new StatsListElement(key, number, name, stat, opr, dpr, ccwm);
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}