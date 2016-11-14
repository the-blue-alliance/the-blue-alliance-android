package com.thebluealliance.androidclient.views.breakdowns;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.testing.ModelMaker;
import com.thebluealliance.androidclient.types.MatchType;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;

@RunWith(AndroidJUnit4.class)
public class MatchBreakdownView2016Test  {

    private static final int WIDTH_DP = 400;

    @Test
    public void testRenderQualMatch() throws Exception {
        View view = getView("2016necmp_qm1");
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    @Test
    public void testRenderPlayoffMatch() throws Exception {
        View view = getView("2016necmp_f1m1");
        ViewHelpers.setupView(view)
                   .setExactWidthDp(WIDTH_DP)
                   .layout();

        Screenshot.snap(view)
                  .record();
    }

    private MatchBreakdownView2016 getView(String matchJsonFile) {
        Match match = ModelMaker.getModel(Match.class, matchJsonFile);
        LayoutInflater inflater = LayoutInflater.from(InstrumentationRegistry.getTargetContext());
        View view = inflater.inflate(R.layout.fragment_match_breakdown, null, false);

        MatchBreakdownView2016 matchView = (MatchBreakdownView2016) view.findViewById(R.id.match_breakdown);
        MatchType matchType = MatchType.fromKey(match.getKey());
        matchView.initWithData(matchType, match.getAlliancesJson(), match.getScoreBreakdownJson());
        matchView.setVisibility(View.VISIBLE);

        // hide progress bar
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        return matchView;
    }
}