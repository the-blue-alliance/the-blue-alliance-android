package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.ListItem;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
    @Mock EventRenderer mEventRenderer;
    @Mock TeamRenderer mTeamRenderer;
    @Mock MatchRenderer mMatchRenderer;
    @Mock DistrictRenderer mDistrictRenderer;

    private MyTbaModelRenderer mRenderer;

    public MyTbaModelRendererTest() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mRenderer = new MyTbaModelRenderer(mDatafeed, mEventRenderer, mTeamRenderer, mMatchRenderer, mDistrictRenderer);
    }

    @Test
    public void testRenderEvent() {
        Event event = ModelMaker.getModel(Event.class, EVENT_KEY);
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(event));
        mRenderer.renderFromKey(EVENT_KEY, ModelType.EVENT);

        verify(mEventRenderer).renderFromModel(event, null);
    }

    @Test
    public void testNullEvent() {
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(null));
        ListItem item = mRenderer.renderFromKey(EVENT_KEY, ModelType.EVENT);

        verifyZeroInteractions(mEventRenderer);
        assertNotNull(item);
        assertTrue(item instanceof ModelListElement);
        assertEquals(((ModelListElement)item).getText(), "2015cthar");
        assertEquals(((ModelListElement)item).getKey(), EVENT_KEY);
        assertEquals(((ModelListElement)item).getType(), ModelType.EVENT);
    }

    @Test
    public void testRenderTeam() {
        Team team = ModelMaker.getModel(Team.class, TEAM_KEY);
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(team));

        mRenderer.renderFromKey(TEAM_KEY, ModelType.TEAM);
        verify(mTeamRenderer).renderFromModel(team, TeamRenderer.RENDER_BASIC);
    }

    @Test
    public void testNullTeam() {
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(null));
        ListItem item = mRenderer.renderFromKey(TEAM_KEY, ModelType.TEAM);

        assertNotNull(item);
        assertTrue(item instanceof ModelListElement);
        assertEquals(((ModelListElement)item).getText(), "frc1124");
        assertEquals(((ModelListElement)item).getKey(), TEAM_KEY);
        assertEquals(((ModelListElement)item).getType(), ModelType.TEAM);
    }

    @Test
    public void testRenderMatch() {
        Match match = ModelMaker.getModel(Match.class, MATCH_KEY);
        when(mDatafeed.fetchMatch(MATCH_KEY)).thenReturn(Observable.just(match));

        mRenderer.renderFromKey(MATCH_KEY, ModelType.MATCH);
        verify(mMatchRenderer).renderFromModel(match, MatchRenderer.RENDER_DEFAULT);
    }

    @Test
    public void testNullMatch() {
        when(mDatafeed.fetchMatch(MATCH_KEY)).thenReturn(Observable.just(null));
        ListItem item = mRenderer.renderFromKey(MATCH_KEY, ModelType.MATCH);

        assertNotNull(item);
        assertTrue(item instanceof ModelListElement);
        assertEquals(((ModelListElement)item).getText(), "2014cmp_f1m1");
        assertEquals(((ModelListElement)item).getKey(), MATCH_KEY);
        assertEquals(((ModelListElement)item).getType(), ModelType.MATCH);
    }

    @Test
    public void testRenderEventTeam() {
        Team team = ModelMaker.getModel(Team.class, TEAM_KEY);
        Event event = ModelMaker.getModel(Event.class, EVENT_KEY);
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(team));
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(event));

        ListItem item = mRenderer.renderFromKey(EVENT_TEAM_KEY, ModelType.EVENTTEAM);
        assertNotNull(item);
        assertTrue(item instanceof ModelListElement);
        assertEquals(((ModelListElement)item).getText(), "UberBots @ 2015 Hartford");
        assertEquals(((ModelListElement)item).getKey(), EVENT_TEAM_KEY);
        assertEquals(((ModelListElement)item).getType(), ModelType.EVENTTEAM);
    }

    @Test
    public void testNullEventTeam() {
        when(mDatafeed.fetchTeam(TEAM_KEY)).thenReturn(Observable.just(null));
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(null));

        ListItem item = mRenderer.renderFromKey(EVENT_TEAM_KEY, ModelType.EVENTTEAM);
        assertNotNull(item);
        assertTrue(item instanceof ModelListElement);
        assertEquals(((ModelListElement)item).getText(), "frc1124 @ 2015cthar");
        assertEquals(((ModelListElement)item).getKey(), EVENT_TEAM_KEY);
        assertEquals(((ModelListElement)item).getType(), ModelType.EVENTTEAM);
    }

    @Test
    public void testRenderDistrict() {
        District district = ModelMaker.getModel(District.class, "district_ne");
        district.setYear(2015);
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(district));

        ListItem item = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.DISTRICT);
        verify(mDistrictRenderer).renderFromKey(DISTRICT_KEY, ModelType.DISTRICT);
    }

    @Test
    public void testNullDistrict() {
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(null));

        ListItem item = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.DISTRICT);
        assertNotNull(item);
        assertTrue(item instanceof ModelListElement);
        assertEquals(((ModelListElement)item).getText(), DISTRICT_KEY);
        assertEquals(((ModelListElement)item).getKey(), DISTRICT_KEY);
        assertEquals(((ModelListElement)item).getType(), ModelType.DISTRICT);
    }
}