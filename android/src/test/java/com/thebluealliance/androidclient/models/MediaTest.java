package com.thebluealliance.androidclient.models;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.types.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(DefaultTestRunner.class)
public class MediaTest {
    private Media cdMedia;
    private Media ytMedia;
    private Media imgurMedia;

    @Before
    public void readJsonData(){
        cdMedia = ModelMaker.getModel(Media.class, "media_cdphotothread");
        ytMedia = ModelMaker.getModel(Media.class, "media_youtube");
        imgurMedia = ModelMaker.getModel(Media.class, "media_imgur");
    }

    @Test
    public void testCdMedia()  {
        assertNotNull(cdMedia);
        assertEquals(MediaType.fromString(cdMedia.getType()),
                     MediaType.CD_PHOTO_THREAD);
        assertEquals(cdMedia.getForeignKey(), "39894");
        assertFalse(cdMedia.getDetailsJson().isJsonNull());
        JsonObject details = cdMedia.getDetailsJson();
        assertTrue(details.has("image_partial"));
        assertEquals(details.get("image_partial").getAsString(), "fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg");
    }

    @Test
    public void testYtMedia()  {
        assertNotNull(ytMedia);
        assertEquals(MediaType.fromString(ytMedia.getType()),
                     MediaType.YOUTUBE);
        assertEquals(ytMedia.getForeignKey(), "RpSgUrsghv4");
        assertFalse(ytMedia.getDetailsJson().isJsonNull());
    }

    @Test
    public void testImgurMedia() {
        assertNotNull(imgurMedia);
        assertEquals(MediaType.fromString(imgurMedia.getType()),
                     MediaType.IMGUR);
        assertEquals(imgurMedia.getForeignKey(), "aF8T5ZE");
        assertFalse(imgurMedia.getDetailsJson().isJsonNull());
    }
}
