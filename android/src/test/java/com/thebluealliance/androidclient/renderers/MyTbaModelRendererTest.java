package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.ModelListElement;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MyTbaModelRendererTest {

    private static final String EVENT_KEY = "2015cthar";
    private static final String TEAM_KEY = "frc1124";
    private static final String MATCH_KEY = "2014cmp_f1m1";
    private static final String EVENT_TEAM_KEY = "2015cthar_frc1124";
    private static final String DISTRICT_KEY = "ne";

    @Mock APICache mDatafeed;

    private MyTbaModelRenderer mRenderer;

    public MyTbaModelRendererTest() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mRenderer = new MyTbaModelRenderer(mDatafeed);
    }

    @Test
    public void testRenderEvent() {
        Event event = ModelMaker.getModel(Event.class, EVENT_KEY);
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(event));
        ModelListElement item = mRenderer.renderFromKey(EVENT_KEY, ModelType.MODELS.EVENT);

        assertNotNull(item);
        assertEquals(item.getText(), "2015 Hartford");
        assertEquals(item.getKey(), EVENT_KEY);
        assertEquals(item.getType(), ModelType.MODELS.EVENT);
    }

    @Test
    public void testNullEvent() {
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(null));
        ModelListElement item = mRenderer.renderFromKey(EVENT_KEY, ModelType.MODELS.EVENT);

        assertNotNull(item);
        assertEquals(item.getText(), "2015cthar");
        assertEquals(item.getKey(), EVENT_KEY);
        assertEquals(item.getType(), ModelType.MODELS.EVENT);
    }

    @Test
    public void testRenderTeam() {
        Team team = ModelMaker.getModel(Team.class, TEAM_KEY);
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(team));

        ModelListElement item = mRenderer.renderFromKey(TEAM_KEY, ModelType.MODELS.TEAM);
        assertNotNull(item);
        assertEquals(item.getText(), "UberBots");
        assertEquals(item.getKey(), TEAM_KEY);
        assertEquals(item.getType(), ModelType.MODELS.TEAM);
    }

    @Test
    public void testNullTeam() {
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(null));
        ModelListElement item = mRenderer.renderFromKey(TEAM_KEY, ModelType.MODELS.TEAM);

        assertNotNull(item);
        assertEquals(item.getText(), "frc1124");
        assertEquals(item.getKey(), TEAM_KEY);
        assertEquals(item.getType(), ModelType.MODELS.TEAM);
    }

    @Test
    public void testRenderMatch() {
        Match match = ModelMaker.getModel(Match.class, MATCH_KEY);
        when(mDatafeed.fetchMatch(MATCH_KEY)).thenReturn(Observable.just(match));
        ModelListElement item = mRenderer.renderFromKey(MATCH_KEY, ModelType.MODELS.MATCH);

        assertNotNull(item);
        assertEquals(item.getText(), "2014cmp Finals 1 - 1");
        assertEquals(item.getKey(), MATCH_KEY);
        assertEquals(item.getType(), ModelType.MODELS.MATCH);
    }

    @Test
    public void testNullMatch() {
        when(mDatafeed.fetchMatch(MATCH_KEY)).thenReturn(Observable.just(null));
        ModelListElement item = mRenderer.renderFromKey(MATCH_KEY, ModelType.MODELS.MATCH);

        assertNotNull(item);
        assertEquals(item.getText(), "2014cmp_f1m1");
        assertEquals(item.getKey(), MATCH_KEY);
        assertEquals(item.getType(), ModelType.MODELS.MATCH);
    }

    @Test
    public void testRenderEventTeam() {
        Team team = ModelMaker.getModel(Team.class, TEAM_KEY);
        Event event = ModelMaker.getModel(Event.class, EVENT_KEY);
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(team));
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(event));

        ModelListElement item = mRenderer.renderFromKey(EVENT_TEAM_KEY, ModelType.MODELS.EVENTTEAM);
        assertNotNull(item);
        assertEquals(item.getText(), "UberBots @ 2015 Hartford");
        assertEquals(item.getKey(), EVENT_TEAM_KEY);
        assertEquals(item.getType(), ModelType.MODELS.EVENTTEAM);
    }

    @Test
    public void testNullEventTeam() {
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(null));
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(null));

        ModelListElement item = mRenderer.renderFromKey(EVENT_TEAM_KEY, ModelType.MODELS.EVENTTEAM);
        assertNotNull(item);
        assertEquals(item.getText(), "frc1124 @ 2015cthar");
        assertEquals(item.getKey(), EVENT_TEAM_KEY);
        assertEquals(item.getType(), ModelType.MODELS.EVENTTEAM);
    }

    @Test
    public void testRenderDistrict() {
        District district = ModelMaker.getModel(District.class, "district_ne");
        district.setYear(2015);
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(district));

        ModelListElement item = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.MODELS.DISTRICT);
        assertNotNull(item);
        assertEquals(item.getText(), "2015 New England");
        assertEquals(item.getKey(), DISTRICT_KEY);
        assertEquals(item.getType(), ModelType.MODELS.DISTRICT);
    }

    @Test
    public void testNullDistrict() {
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(null));

        ModelListElement item = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.MODELS.DISTRICT);
        assertNotNull(item);
        assertEquals(item.getText(), DISTRICT_KEY);
        assertEquals(item.getKey(), DISTRICT_KEY);
        assertEquals(item.getType(), ModelType.MODELS.DISTRICT);
    }
}