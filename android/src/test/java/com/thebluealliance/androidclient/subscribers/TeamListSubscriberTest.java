package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamListSubscriberTest {

    TeamListSubscriber mSubscriber;
    List<Team> mTeams;

    @Before
    public void setUp() {
        mSubscriber = new TeamListSubscriber();
        mTeams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mTeams);
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mTeams);

        assertEquals(67, data.size());
        for (int i = 0; i < data.size(); i++) {
            assertTrue(data.get(i) instanceof TeamListElement);
        }
    }
}