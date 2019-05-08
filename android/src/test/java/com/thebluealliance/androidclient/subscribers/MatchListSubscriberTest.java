package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;
import androidx.annotation.StringRes;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class MatchListSubscriberTest {

    @Mock public Database mDb;
    @Mock public EventsTable mEventsTable;
    @Mock public Resources mResources;
    @Mock public EventBus mEventBus;
    @Mock public Event mEvent;

    MatchListSubscriber mSubscriber;
    List<Match> mMatches;

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
        mMatches = ModelMaker.getModelList(Match.class, "2015necmp_matches");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMatches);
        verify(mEventBus).post(any(LiveEventMatchUpdateEvent.class));
    }

    @Test
    public void testParsedData()  {
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMatches);

        /* This event is not live, so matches should be sorted by display order */
        assertEquals(4, data.size());
        assertMatchGroup(data.get(0), 120, R.string.quals_header);
        assertMatchGroup(data.get(1), 8, R.string.quarters_header);
        assertMatchGroup(data.get(2), 6, R.string.semis_header);
        assertMatchGroup(data.get(3), 2, R.string.finals_header);
    }

    @Test
    public void testLiveEventPlayOrderSort()  {
        when(mEvent.isHappeningNow()).thenReturn(true);
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMatches);

        /* This event is live, so matches should be sorted by play order */
        assertEquals(4, data.size());
        assertMatchGroup(data.get(0), 120, R.string.quals_header);
        assertMatchGroup(data.get(1), 8, R.string.quarters_header);
        assertMatchGroup(data.get(2), 6, R.string.semis_header);
        assertMatchGroup(data.get(3), 2, R.string.finals_header);
    }

    private void assertMatchGroup(ListGroup group, int size, @StringRes int titleRes) {
        assertEquals(group.getTitle(), mResources.getString(titleRes));
        assertEquals(group.children.size(), size);
    }
}