package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AllianceEventKeyAdder;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.types.ModelType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventRendererTest  {

    private static final String EVENT_KEY = "2016nytr";

    @Mock APICache mDatafeed;

    private EventRenderer mRenderer;
    private List<EventAlliance> mAlliances;
    private Event mEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AllianceEventKeyAdder keyAdder = new AllianceEventKeyAdder("2016nytr");
        mEvent = ModelMaker.getModel(Event.class, EVENT_KEY);
        mAlliances = ModelMaker.getModelList(EventAlliance.class, "2016nytr_alliances_apiv3");
        mAlliances = keyAdder.call(mAlliances);
        mRenderer = new EventRenderer(mDatafeed);
    }

    @Test
    public void testRenderFromKey() {
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(mEvent));
        EventListElement element = mRenderer.renderFromKey(EVENT_KEY, ModelType.EVENT, null);
        assertNotNull(element);
        assertEquals(element.getEventKey(), EVENT_KEY);
        assertEquals(element.showMyTba, false);
    }

    @Test
    public void testNullRenderFromKey() {
        when(mDatafeed.fetchEvent(EVENT_KEY)).thenReturn(Observable.just(null));
        EventListElement element = mRenderer.renderFromKey(EVENT_KEY, ModelType.EVENT, null);
        assertNull(element);
    }

    @Test
    public void testRenderFromModel() {
        EventListElement element = mRenderer.renderFromModel(mEvent, null);
        assertNotNull(element);
        assertEquals(element.getEventKey(), EVENT_KEY);
    }

    @Test
    public void testRenderWebcasts() {
        List<WebcastListElement> elements = mRenderer.renderWebcasts(mEvent);
        assertNotNull(elements);
        assertEquals(elements.size(), 1);

        WebcastListElement webcast = elements.get(0);
        assertNotNull(webcast);
        assertEquals(webcast.eventKey, EVENT_KEY);
        assertEquals(webcast.eventName, "New York Tech Valley");
        assertEquals(webcast.webcast, JSONHelper.getasJsonArray(mEvent.getWebcasts()).get(0));
        assertEquals(webcast.number, 1);
    }

    @Test
    public void testRenderAlliancesWithList() {
        List<ListItem> elements = new ArrayList<>();
        mRenderer.renderAlliances(mAlliances, elements);
        assertAllianceList(elements);
    }

    private void assertAllianceList(List<ListItem> alliances) {
        assertNotNull(alliances);
        assertEquals(alliances.size(), 8);

        for (int i = 0; i < mAlliances.size(); i++) {
            assertTrue(alliances.get(i) instanceof AllianceListElement);
            AllianceListElement alliance = (AllianceListElement)alliances.get(i);
            assertEquals(alliance.eventKey, EVENT_KEY);
            assertEquals(alliance.number, (i + 1));
            assertEquals(alliance.teams, mAlliances.get(i).getPicks());
        }
    }
}