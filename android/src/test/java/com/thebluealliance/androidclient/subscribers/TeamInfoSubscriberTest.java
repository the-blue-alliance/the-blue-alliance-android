package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamInfoSubscriberTest {

    TeamInfoSubscriber mSubscriber;
    Team mTeam;

    @Before
    public void setUp() {
        mSubscriber = new TeamInfoSubscriber();
        mTeam = ModelMaker.getModel(Team.class, "frc1124");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mTeam);
    }

    @Test
    public void testParsedData()  {
        TeamInfoBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mTeam);

        assertNotNull(data);
        assertEquals(mTeam.getKey(), data.teamKey);
        assertEquals(mTeam.getName(), data.fullName);
        assertEquals(mTeam.getNickname(), data.nickname);
        assertEquals(mTeam.getLocation(), data.location);
        assertEquals(mTeam.getWebsite(), data.website);
    }
}