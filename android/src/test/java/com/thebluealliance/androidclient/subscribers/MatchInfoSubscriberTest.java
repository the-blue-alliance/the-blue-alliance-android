package com.thebluealliance.androidclient.subscribers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.content.res.Resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.MediaRenderer;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber.Model;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;

import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class MatchInfoSubscriberTest {

    @Mock EventBus mEventBus;
    @Mock APICache mCache;
    @Mock Resources mResources;

    private MatchInfoSubscriber mSubscriber;
    private Gson mGson;
    private Model mData;
    private MatchRenderer mRenderer;
    private MediaRenderer mMediaRenderer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mGson = TBAAndroidModule.getGson();
        mRenderer = spy(new MatchRenderer(mCache, mResources));
        mMediaRenderer = spy(new MediaRenderer());
        mSubscriber = new MatchInfoSubscriber(mGson, mEventBus, mRenderer, mMediaRenderer, mResources);
        mData = new Model(
          ModelMaker.getModel(Match.class, "2015necmp_f1m1"),
          ModelMaker.getModel(Event.class, "2015necmp"));
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mData);
        verify(mEventBus).post(any(ActionBarTitleEvent.class));
    }

    @Test
    public void testParsedData()  {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mData);

        assertEquals(3, data.size());
        assertTrue(data.get(0) instanceof MatchListElement);
        assertTrue(data.get(1) instanceof ImageListElement);

        Media videoItem = ((Match.MatchVideo)mData.match.getVideos().get(0)).asMedia();
        ImageListElement video = (ImageListElement) data.get(1);

        verify(mRenderer).renderFromModel(mData.match, MatchRenderer.RENDER_MATCH_INFO);
        assertTrue(video.equals(mMediaRenderer.renderFromModel(videoItem, null)));
    }
}