package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.renderers.MatchRenderer.RenderArgs;
import com.thebluealliance.androidclient.renderers.MatchRenderer.RenderType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import rx.Observable;

import static com.thebluealliance.androidclient.renderers.MatchRenderer.RENDER_DEFAULT;
import static com.thebluealliance.androidclient.renderers.MatchRenderer.RENDER_MATCH_INFO;
import static com.thebluealliance.androidclient.renderers.MatchRenderer.RENDER_NOTIFICATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(ParameterizedRobolectricTestRunner.class)
public class MatchRendererTest {

    @Mock APICache mDatafeed;

    private String mMatchKey;
    private Match mMatch;
    private MatchRenderer mRenderer;

    @ParameterizedRobolectricTestRunner.Parameters(name = "MatchKey = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
          {"2015necmp_qm1"},
          {"2015necmp_qf1m1"},
          {"2015necmp_sf1m1"},
          {"2015necmp_f1m1"}
        });
    }

    public MatchRendererTest(String matchKey) {
        mMatchKey = matchKey;
    }

    @Before
    public void SetUp() {
        MockitoAnnotations.initMocks(this);
        mRenderer = new MatchRenderer(mDatafeed);
        mMatch = ModelMaker.getModel(Match.class, mMatchKey);
    }

    @Test
    public void testRenderFromKey() {
        when(mDatafeed.fetchMatch(mMatchKey)).thenReturn(Observable.just(mMatch));
        MatchListElement element = mRenderer.renderFromKey(mMatchKey, ModelType.MATCH, null);
        RenderArgs expectedArgs = MatchRenderer.argsFromMode(RENDER_DEFAULT);
        assertMatch(element, mMatch, expectedArgs);
    }

    @Test
    public void testNullRenderFromKey() {
        when(mDatafeed.fetchMatch(mMatchKey)).thenReturn(Observable.just(null));
        MatchListElement element = mRenderer.renderFromKey(mMatchKey, ModelType.MATCH, null);
        assertNull(element);
    }

    @Test
    public void testRenderFromModel() {
        @RenderType int[] types = {RENDER_DEFAULT, RENDER_NOTIFICATION, RENDER_MATCH_INFO};
        for (int i = 0; i < types.length; i++) {
            MatchListElement element = mRenderer.renderFromModel(mMatch, types[i]);
            RenderArgs expectedArgs = MatchRenderer.argsFromMode(types[i]);
            assertMatch(element, mMatch, expectedArgs);
        }
    }

    private void assertMatch(MatchListElement actual, Match expected, RenderArgs params) {
        assertNotNull(actual);
        assertEquals(actual.getKey(), expected.getKey());
        assertEquals(actual.clickable, params.clickable);
        assertEquals(actual.showColumnHeaders, params.showHeaders);
        assertEquals(actual.showMatchTitle, params.showMatchTitle);
        assertEquals(actual.showVideoIcon, params.showVideo);
    }
}