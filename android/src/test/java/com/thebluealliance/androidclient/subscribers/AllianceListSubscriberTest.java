package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AllianceListSubscriberTest extends TestCase {

    @Mock EventRenderer mRenderer;

    private AllianceListSubscriber mSubscriber;
    private Event mEvent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mSubscriber = new AllianceListSubscriber(mRenderer);
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        mSubscriber.onAllianceAdvancementLoaded(null);
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        mSubscriber.onAllianceAdvancementLoaded(null);
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEvent);
    }

    @Test
    public void testParse() throws BasicModel.FieldNotDefinedException {
        mSubscriber.onAllianceAdvancementLoaded(null);
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvent);

        verify(mRenderer).renderAlliances(mEvent, data, null);
    }
}