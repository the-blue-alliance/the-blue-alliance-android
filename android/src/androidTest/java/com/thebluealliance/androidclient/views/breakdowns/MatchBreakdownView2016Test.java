package com.thebluealliance.androidclient.views.breakdowns;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.testing.ModelMaker;

import android.test.InstrumentationTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class MatchBreakdownView2016Test extends InstrumentationTestCase {

    private static final int WIDTH_DP = 400;

    public void testRenderQualMatch() throws Exception {
        View view = getView("2016necmp_qm1");
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    public void testRenderPlayoffMatch() throws Exception {
        View view = getView("2016necmp_f1m1");
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private MatchBreakdownView2016 getView(String matchJsonFile)
    throws BasicModel.FieldNotDefinedException {
        Match match = ModelMaker.getModel(Match.class, matchJsonFile);
        LayoutInflater inflater = LayoutInflater.from(getInstrumentation().getTargetContext());
        View view = inflater.inflate(R.layout.fragment_match_breakdown, null, false);

        MatchBreakdownView2016 matchView = (MatchBreakdownView2016) view.findViewById(R.id.match_breakdown);
        matchView.initWithData(match.getMatchType(), match.getAlliances(), match.getBreakdown());
        matchView.setVisibility(View.VISIBLE);

        // hide progress bar
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        return matchView;
    }
}