package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.DistrictPointBreakdownRenderer;

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

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DistrictPointsListSubscriberTest {

    @Mock public Database mDb;
    @Mock public Event mEvent;

    private DistrictPointsListSubscriber mSubscriber;
    private JsonObject mPoints;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockEventsTable(mDb);
        DatabaseMocker.mockTeamsTable(mDb);
        DistrictPointBreakdownRenderer renderer = new DistrictPointBreakdownRenderer();
        mSubscriber = new DistrictPointsListSubscriber(mDb, HttpModule.getGson(), renderer);
        mPoints = ModelMaker.getModel(JsonObject.class, "2015necmp_points");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testParseJsonNull() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseJsonNull(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mPoints);
    }

    @Test
    public void testCorrectParsedType() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mPoints);
        assertTrue(data instanceof DistrictPointsListSubscriber.Type);
    }

    @Test
    public void testParseInvalidJson() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, new JsonObject());

        assertNotNull(data);
        assertEquals(data.size(), 0);
    }

    @Test
    public void testParseNonDistrict() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mPoints);
        DistrictTeamListElement element =
          new DistrictTeamListElement("frc1124", "", "Team 1124", 1, 87);

        assertNotNull(data);
        assertEquals(data.size(), 1);
        assertTrue(element.equals(data.get(0)));
    }

}