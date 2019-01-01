package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictTeamKey;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(DefaultTestRunner.class)
public class DistrictRankingsSubscriberTest {

    @Mock public Database mDb;

    private DistrictRankingsSubscriber mSubscriber;
    private List<DistrictRanking> mDistrictTeams;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockTeamsTable(mDb);

        AddDistrictTeamKey keyAdder = new AddDistrictTeamKey("2015ne");
        mSubscriber = new DistrictRankingsSubscriber(mDb);
        mDistrictTeams = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings");
        keyAdder.call(mDistrictTeams);
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistrictTeams);
    }

    @Test
    public void testParse()  {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mDistrictTeams);
        DistrictTeamListElement element =
          new DistrictTeamListElement("frc1124", "2015ne", "Team 1124", 26, 157);

        assertNotNull(data);
        assertEquals(data.size(), 179);
        assertTrue(data.get(25).equals(element));
    }
}