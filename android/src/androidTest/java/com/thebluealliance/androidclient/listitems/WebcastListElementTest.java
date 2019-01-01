package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WebcastListElementTest {

    private static final int WIDTH_DP = 400;
    private static JsonObject WEBCAST = new JsonObject();
    static {
        WEBCAST.addProperty("channel", "nefirst_blue");
        WEBCAST.addProperty("type", "twitch");
    }

    @Test
    public void testRender(){
        View view = getView("2016test", "Test Event", WEBCAST, 1);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(String eventKey, String eventName, JsonObject webcast, int number) {
        WebcastListElement element = new WebcastListElement(eventKey, eventName, webcast, number);
        Context targetContext = InstrumentationRegistry.getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }
}