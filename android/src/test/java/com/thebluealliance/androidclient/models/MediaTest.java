package com.thebluealliance.androidclient.models;

import com.google.gson.JsonObject;
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

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MediaTest {
    Media cdMedia;
    Media ytMedia;

    @Before
    public void readJsonData(){
        cdMedia = ModelMaker.getModel(Media.class, "media_cdphotothread");
        ytMedia = ModelMaker.getModel(Media.class, "media_youtube");
    }

    @Test
    public void testCdMedia() throws BasicModel.FieldNotDefinedException {
        assertNotNull(cdMedia);
        assertEquals(cdMedia.getMediaType(),
                     MediaType.CD_PHOTO_THREAD);
        assertEquals(cdMedia.getForeignKey(), "39894");
        assertFalse(cdMedia.getDetails().isJsonNull());
        JsonObject details = cdMedia.getDetails();
        assertTrue(details.has("image_partial"));
        assertEquals(details.get("image_partial").getAsString(), "fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg");
    }

    @Test
    public void testYtMedia() throws BasicModel.FieldNotDefinedException {
        assertNotNull(ytMedia);
        assertEquals(ytMedia.getMediaType(),
                     MediaType.YOUTUBE);
        assertEquals(ytMedia.getForeignKey(), "RpSgUrsghv4");
        assertFalse(ytMedia.getDetails().isJsonNull());
    }
}
