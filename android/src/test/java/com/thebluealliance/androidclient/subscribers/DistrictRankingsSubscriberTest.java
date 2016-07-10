package com.thebluealliance.androidclient.subscribers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictTeamKey;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
public class DistrictRankingsSubscriberTest {

    @Rule public PowerMockRule rule = new PowerMockRule();

    @Mock public Database mDb;

    private DistrictRankingsSubscriber mSubscriber;
    private List<DistrictTeam> mDistrictTeams;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        BriteDatabase briteDb = PowerMockito.mock(BriteDatabase.class);
        DatabaseMocker.mockTeamsTable(mDb, briteDb);

        AddDistrictTeamKey keyAdder = new AddDistrictTeamKey("ne", 2015);
        mSubscriber = new DistrictRankingsSubscriber(mDb);
        mDistrictTeams = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings");
        keyAdder.call(mDistrictTeams);
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistrictTeams);
    }

    @Test
    public void testParse() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mDistrictTeams);
        DistrictTeamListElement element =
          new DistrictTeamListElement("frc1124", "2015ne", "Team 1124", 26, 157);

        assertNotNull(data);
        assertEquals(data.size(), 1);
        assertTrue(data.get(0).equals(element));
    }
}