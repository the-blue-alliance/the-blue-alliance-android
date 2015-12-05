package com.thebluealliance.androidclient.renderers;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;
import com.thebluealliance.androidclient.models.BasicModel.FieldNotDefinedException;
import com.thebluealliance.androidclient.models.Event;

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

    private static final String EVENT_KEY = "2015cthar";

    @Mock APICache mDatafeed;

    private EventRenderer mRenderer;
    private Event mEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mEvent = ModelMaker.getModel(Event.class, EVENT_KEY);
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
    public void testRenderWebcasts() throws FieldNotDefinedException {
        List<WebcastListElement> elements = mRenderer.renderWebcasts(mEvent);
        assertNotNull(elements);
        assertEquals(elements.size(), 1);

        WebcastListElement webcast = elements.get(0);
        assertNotNull(webcast);
        assertEquals(webcast.eventKey, EVENT_KEY);
        assertEquals(webcast.eventName, "Hartford");
        assertEquals(webcast.webcast, mEvent.getWebcasts().get(0));
        assertEquals(webcast.number, 1);
    }

    @Test
    public void testRenderAlliances() throws FieldNotDefinedException {
        List<ListItem> elements = mRenderer.renderAlliances(mEvent);
        assertAllianceList(elements);
    }

    @Test
    public void testRenderAlliancesWithList() throws FieldNotDefinedException {
        List<ListItem> elements = new ArrayList<>();
        mRenderer.renderAlliances(mEvent, elements);
        assertAllianceList(elements);
    }

    private void assertAllianceList(List<ListItem> alliances) throws FieldNotDefinedException {
        assertNotNull(alliances);
        assertEquals(alliances.size(), 8);

        JsonArray jsonData = mEvent.getAlliances();
        for (int i = 0; i < 8; i++) {
            assertTrue(alliances.get(i) instanceof AllianceListElement);
            AllianceListElement alliance = (AllianceListElement)alliances.get(i);
            assertEquals(alliance.eventKey, EVENT_KEY);
            assertEquals(alliance.number, (i + 1));
            assertEquals(alliance.teams, jsonData.get(i).getAsJsonObject().get("picks"));
        }
    }
}