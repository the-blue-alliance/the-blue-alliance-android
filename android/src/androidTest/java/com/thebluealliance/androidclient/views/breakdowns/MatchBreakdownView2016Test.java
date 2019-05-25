package com.thebluealliance.androidclient.views.breakdowns;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.testing.ModelMaker;
import com.thebluealliance.androidclient.types.MatchType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MatchBreakdownView2016Test  {

    private static final int WIDTH_DP = 400;
    private Gson mGson;

    @Before
    public void setUp() {
        mGson = HttpModule.getGson();
    }

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

        FrameLayout matchView = (FrameLayout) view.findViewById(R.id.match_breakdown);
        assertEquals(1, matchView.getChildCount());
        assertTrue(matchView.getChildAt(0) instanceof MatchBreakdownView2016);
        MatchBreakdownView2016 view2016 = (MatchBreakdownView2016) matchView.getChildAt(0);
        MatchType matchType = MatchType.fromKey(match.getKey());
        view2016.initWithData(matchType, match.getWinningAlliance(), match.getAlliances(),
                              mGson.fromJson(match.getScoreBreakdown(),JsonObject.class));
        view2016.setVisibility(View.VISIBLE);

        // hide progress bar
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        return view2016;
    }
}