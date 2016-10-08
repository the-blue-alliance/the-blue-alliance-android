package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.types.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(manifest = Config.NONE)
@RunWith(ParameterizedRobolectricTestRunner.class)
public class MediaRendererTest {

    private String mMediaName;
    private MediaType mMediaType;
    private Media mMedia;
    private MediaRenderer mRenderer;

    @ParameterizedRobolectricTestRunner.Parameters(name = "MediaType = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"media_cdphotothread", "cdphotothread"},
                {"media_youtube", "youtube"},
                {"media_imgur", "imgur"}
        });
    }

    public MediaRendererTest(String fileName, String typeString) {
        mMediaName = fileName;
        mMediaType = MediaType.fromString(typeString);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mMedia = ModelMaker.getModel(Media.class, mMediaName);
        mRenderer = new MediaRenderer();
    }

    @Test
    public void testRenderFromModel()  {
        ImageListElement listItem = mRenderer.renderFromModel(mMedia, null);
        assertNotNull(listItem);
        assertEquals(listItem.isVideo, mMediaType.isVideo());
        assertEquals(
                listItem.linkUrl,
                String.format(mMediaType.getLinkUrlPattern(), mMedia.getForeignKey()));
        if (mMediaType != MediaType.CD_PHOTO_THREAD) {
            assertEquals(
                    listItem.imageUrl,
                    String.format(mMediaType.getImageUrlPattern(), mMedia.getForeignKey()));
        } else {
            assertEquals(listItem.imageUrl, "http://www.chiefdelphi"
                     + ".com/media/img/fe3/fe38d320428adf4f51ac969efb3db32c_m.jpg");
        }
    }

}