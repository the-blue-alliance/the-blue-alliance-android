package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import de.greenrobot.event.EventBus;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MatchListSubscriberTest {

    @Mock public Database mDb;
    @Mock public Resources mResources;
    @Mock public EventBus mEventBus;

    MatchListSubscriber mSubscriber;
    List<Match> mMatches;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockEventsTable(mDb);
        when(mResources.getString(R.string.quals_header)).thenReturn("Qualification Matches");
        when(mResources.getString(R.string.quarters_header)).thenReturn("Quarterfinal Matches");
        when(mResources.getString(R.string.semis_header)).thenReturn("Semifinal Matches");
        when(mResources.getString(R.string.finals_header)).thenReturn("Finals Matches");

        mSubscriber = new MatchListSubscriber(mResources, mDb, mEventBus);
        mMatches = ModelMaker.getMultiModelList(Match.class,
          "2015necmp_qm1", "2015necmp_qf1m1", "2015necmp_sf1m1", "2015necmp_f1m1");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMatches);
        verify(mEventBus).post(any(LiveEventMatchUpdateEvent.class));
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMatches);

        assertEquals(4, data.size());
        assertMatchGroup(0, data.get(0), R.string.quals_header);
        assertMatchGroup(1, data.get(1), R.string.quarters_header);
        assertMatchGroup(2, data.get(2), R.string.semis_header);
        assertMatchGroup(3, data.get(3), R.string.finals_header);
    }

    private void assertMatchGroup(int index, ListGroup group, @StringRes int titleRes) {
        assertEquals(group.getTitle(), mResources.getString(titleRes));
        assertEquals(group.children.size(), 1);
        assertEquals(group.children.get(0), mMatches.get(index));
    }
}