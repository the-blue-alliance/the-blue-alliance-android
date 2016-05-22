package com.thebluealliance.androidclient.listitems;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class ModelListElementTest extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    public void testRender() {
        View view = getView("Test Model", "test", ModelType.TEAM);
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private View getView(String text, String key, ModelType type) {
        ModelListElement element = new ModelListElement(text, key, type);
        Context targetContext = getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(targetContext);
        return element.getView(targetContext, inflater, null);
    }

}