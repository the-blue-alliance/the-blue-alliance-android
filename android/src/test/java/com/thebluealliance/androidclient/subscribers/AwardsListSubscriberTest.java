package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AwardsListSubscriberTest {

    private AwardsListSubscriber mSubscriber;
    private List<Award> mAwards;

    @Before
    public void setUp() {
        Database db = mock(Database.class);
        TeamsTable teams = mock(TeamsTable.class);
        Team team = mock(Team.class);

        mSubscriber = new AwardsListSubscriber(db);
        mAwards = ModelMaker.getModelList(Award.class, "2015necmp_awards");

        when(db.getTeamsTable()).thenReturn(teams);
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mAwards);
    }

    @Test
    public void testParse() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mAwards);

        assertEquals(data.size(), 4);
        for (int i = 0; i < data.size(); i++) {
            assertTrue(data.get(i) instanceof CardedAwardListElement);
        }
    }

    @Test
    public void testSelectedTeam() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setTeamKey("frc195");
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mAwards);
        CardedAwardListElement element = (CardedAwardListElement) data.get(0);

        assertEquals(element.selectedTeamNum, "frc195");
    }

    @Test
    public void testParseMultiTeamWinner() throws BasicModel.FieldNotDefinedException {
        assertItemsEqual(0);
    }

    @Test
    public void testParseSinglePersonWinner() throws BasicModel.FieldNotDefinedException {
        assertItemsEqual(1);
    }

    @Test
    public void testParseMultiPersonWinner() throws BasicModel.FieldNotDefinedException {
        assertItemsEqual(2);
    }

    @Test
    public void testParseSingleTeamWinner() throws BasicModel.FieldNotDefinedException {
        assertItemsEqual(3);
    }

    private void assertItemsEqual(int index) throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mAwards);
        CardedAwardListElement element = (CardedAwardListElement) data.get(0);
        Award award = mAwards.get(0);

        assertEquals(element.awardName, award.getName());
        assertEquals(element.eventKey, award.getEventKey());
        assertEquals(element.selectedTeamNum, "");
        assertTrue(element.awardWinners.equals(award.getWinners()));
    }
}