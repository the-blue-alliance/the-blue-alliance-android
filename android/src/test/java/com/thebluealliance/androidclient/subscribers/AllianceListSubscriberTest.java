package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(DefaultTestRunner.class)
public class AllianceListSubscriberTest extends TestCase {

    @Mock EventRenderer mRenderer;

    private AllianceListSubscriber mSubscriber;
    private List<EventAlliance> m2016nytrAlliances;
    private List<EventAlliance> m2014ctharAlliances;
    private List<EventAlliance> m2015arcAlliances;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mSubscriber = new AllianceListSubscriber(mRenderer);
        m2016nytrAlliances = ModelMaker.getModelList(EventAlliance.class, "2016nytr_alliances_apiv3");
        m2014ctharAlliances = ModelMaker.getModelList(EventAlliance.class, "2014cthar_alliances_apiv3");
        m2015arcAlliances = ModelMaker.getModelList(EventAlliance.class, "2015arc_alliances_apiv3");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testParse2016()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, m2016nytrAlliances);
    }

    @Test
    public void testParse2014() {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, m2014ctharAlliances);
    }

    @Test
    public void testParse4Team2015() {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, m2015arcAlliances);
    }

    @Test
    public void testParse()  {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, m2016nytrAlliances);

        verify(mRenderer).renderAlliances(m2016nytrAlliances, data);
    }
}