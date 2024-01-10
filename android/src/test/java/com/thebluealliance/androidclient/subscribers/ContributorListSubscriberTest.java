package com.thebluealliance.androidclient.subscribers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ContributorListElement;
import com.thebluealliance.androidclient.listitems.ListItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.LooperMode;

import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class ContributorListSubscriberTest {

    ContributorListSubscriber mSubscriber;
    JsonArray mData;

    @Before
    public void setUp() {
        mData = ModelMaker.getModel(JsonArray.class, "contributors_list");
        mSubscriber = new ContributorListSubscriber(Mockito.mock(Picasso.class));
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mData);
    }

    @Test
    public void testParsedData()  {
        List<ListItem> parsed = DatafeedTestDriver.getParsedData(mSubscriber, mData);
        assertEquals(1, parsed.size());
        assertTrue(parsed.get(0) instanceof ContributorListElement);
        ContributorListElement element = (ContributorListElement) parsed.get(0);
        assertEquals("phil-lopreiato", element.username);
        assertEquals(1266, element.contributionCount);
        assertEquals("https://avatars.githubusercontent.com/u/2754863?v=3", element.avatarUrl);
    }
}