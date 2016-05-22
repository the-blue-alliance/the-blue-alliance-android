package com.thebluealliance.androidclient.views;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.R;

import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class MatchViewTest extends InstrumentationTestCase {

    public void testRender() {
        MatchView view = createView();
        ViewHelpers.setupView(view)
                   .setExactWidthDp(300)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private MatchView createView() {
        LayoutInflater inflater = LayoutInflater.from(getInstrumentation().getTargetContext());
        View view = inflater.inflate(R.layout.match_view, null, false);

        MatchView matchView = (MatchView) view.findViewById(R.id.match_view);
        matchView.initWithParams("abcd", "Quals 2", new String[]{"1", "2", "3"},
                            new String[]{"4", "5", "6"}, "34", "43", "2016test_qm2", 1463880643L,
                            "", true);
        return matchView;
    }
}