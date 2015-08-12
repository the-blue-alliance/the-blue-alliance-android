package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MediaListSubscriberTest {

    @Mock public Resources mResources;

    MediaListSubscriber mSubscriber;
    List<Media> mMedias;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mResources.getString(R.string.cd_header)).thenReturn("Chief Delphi Photos");
        when(mResources.getString(R.string.yt_header)).thenReturn("YouTube Videos");

        mSubscriber = new MediaListSubscriber(mResources);
        mMedias = ModelMaker.getModelList(Media.class, "media_frc254_2014");
    }

    @Test
    public void testNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMedias);
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListGroup> data = DatafeedTestDriver.getParsedData(mSubscriber, mMedias);

        assertEquals(2, data.size());
        assertMediaGroup(0, data.get(0), R.string.cd_header);
        assertMediaGroup(1, data.get(1), R.string.yt_header);
    }

    private void assertMediaGroup(int index, ListGroup group, @StringRes int titleRes) {
        assertEquals(group.getTitle(), mResources.getString(titleRes));
        assertEquals(group.children.size(), 1);
        assertEquals(group.children.get(0), mMedias.get(index));
    }
}