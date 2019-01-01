package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictKeys;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(DefaultTestRunner.class)
public class DistrictListSubscriberTest {

    @Mock public Database mDb;
    @Mock public DistrictRenderer mRenderer;

    private DistrictListSubscriber mSubscriber;
    private List<District> mDistricts;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockEventsTable(mDb);
        AddDistrictKeys keyAdder = new AddDistrictKeys(2015);
        mSubscriber = new DistrictListSubscriber(mDb, mRenderer);
        mDistricts = ModelMaker.getModelList(District.class, "2015_districts");
        keyAdder.call(mDistricts);
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistricts);
    }

    @Test
    public void testParse()  {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mDistricts);

        assertEquals(data.size(), 5);
        for (int i = 0; i < data.size(); i++) {
            verify(mRenderer).renderFromModel(eq(mDistricts.get(i)), anyObject());
        }
    }
}