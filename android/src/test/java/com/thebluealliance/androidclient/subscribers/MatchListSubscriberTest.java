package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.firebase.AllianceAdvancementEvent;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MatchListSubscriberTest {

    @Mock public Database mDb;
    @Mock public EventsTable mEventsTable;
    @Mock public Resources mResources;
    @Mock public EventBus mEventBus;
    @Mock public Event mEvent;

    MatchListSubscriber mSubscriber;
    List<Match> mMatches;
    String[] mKeys;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mDb.getEventsTable()).thenReturn(mEventsTable);
        when(mEventsTable.get("2015necmp")).thenReturn(mEvent);

        when(mResources.getString(R.string.quals_header)).thenReturn("Qualification Matches");
        when(mResources.getString(R.string.quarters_header)).thenReturn("Quarterfinal Matches");
        when(mResources.getString(R.string.semis_header)).thenReturn("Semifinal Matches");
        when(mResources.getString(R.string.finals_header)).thenReturn("Finals Matches");

        mSubscriber = new MatchListSubscriber(mResources, mDb, mEventBus);
        mSubscriber.setEventKey("2015necmp");
        mKeys = new String[]{"2015necmp_qm1", "2015necmp_qf1m1", "2015necmp_qf1m2",
                "2014necmp_qf2m1", "2015necmp_sf1m1", "2015necmp_f1m1"};
        mMatches = ModelMaker.getMultiModelList(Match.class, mKeys);
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMatches);
        verify(mEventBus).post(eq(new LiveEventMatchUpdateEvent(mMatches.get(5), null)));
        verify(mEventBus).post(eq(new AllianceAdvancementEvent(mSubscriber.getAdvancement())));
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMatches);

        /* This event is not live, so matches should be sorted by display order */
        assertEquals(4, data.size());
        assertMatchGroup(data.get(0), 1, R.string.quals_header, 0);
        assertMatchGroup(data.get(1), 3, R.string.quarters_header, 1, 2, 3);
        assertMatchGroup(data.get(2), 1, R.string.semis_header, 4);
        assertMatchGroup(data.get(3), 1, R.string.finals_header, 5);
    }

    @Test
    public void testLiveEventPlayOrderSort() throws BasicModel.FieldNotDefinedException {
        when(mEvent.isHappeningNow()).thenReturn(true);
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMatches);

        /* This event is live, so matches should be sorted by play order */
        assertEquals(4, data.size());
        assertMatchGroup(data.get(0), 1, R.string.quals_header, 0);
        assertMatchGroup(data.get(1), 3, R.string.quarters_header, 1, 3, 2);
        assertMatchGroup(data.get(2), 1, R.string.semis_header, 4);
        assertMatchGroup(data.get(3), 1, R.string.finals_header, 5);
    }

    private void assertMatchGroup(ListGroup group, int size, @StringRes int titleRes, int... indexes) {
        assertEquals(group.getTitle(), mResources.getString(titleRes));
        assertEquals(group.children.size(), size);
        assertEquals(size, indexes.length);
        for (int i = 0; i < group.children.size(); i++) {
            assertEquals(((Match)group.children.get(i)).getKey(), mKeys[indexes[i]]);
        }
    }
}