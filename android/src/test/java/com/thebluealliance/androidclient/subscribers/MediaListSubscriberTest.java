package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class MediaListSubscriberTest {

    @Mock public Resources mResources;

    MediaListSubscriber mSubscriber;
    List<Media> mMedias;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mResources.getString(R.string.media_images_header)).thenReturn("Chief Delphi Photos");
        when(mResources.getString(R.string.media_videos_header)).thenReturn("YouTube Videos");

        mSubscriber = new MediaListSubscriber(mResources);
        mMedias = ModelMaker.getModelList(Media.class, "media_frc254_2014");
    }

    @Test
    public void testNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMedias);
    }

    @Test
    public void testParsedData()  {
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMedias);

        assertEquals(2, data.size());
        assertMediaGroup(data.get(0), 2, R.string.media_images_header, 0, 2);
        assertMediaGroup(data.get(1), 1, R.string.media_videos_header, 1);
    }

    private void assertMediaGroup(ListGroup group, int size, @StringRes int titleRes, int... indexes) {
        assertEquals(group.getTitle(), mResources.getString(titleRes));
        assertEquals(group.children.size(), size);
        assertEquals(size, indexes.length);
        for (int i = 0; i < group.children.size(); i++) {
            assertEquals(group.children.get(i), mMedias.get(indexes[i]));
        }
    }
}