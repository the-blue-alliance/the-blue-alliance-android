package com.thebluealliance.androidclient.test.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Ignore
public class MediaTest {
    Media cdMedia;
    Media ytMedia;

    @Before
    public void readJsonData(){
        BufferedReader cdMediaReader;
        BufferedReader ytMediaReader;
        Gson gson = JSONHelper.getGson();
        String basePath = new File("").getAbsolutePath();
        try {
            cdMediaReader = new BufferedReader(
                new FileReader(basePath + "/android/src/test/java/com/thebluealliance/" +
                    "androidclient/test/models/media_cdphotothread.json"));
            ytMediaReader = new BufferedReader(
                new FileReader(basePath + "/android/src/test/java/com/thebluealliance/" +
                    "androidclient/test/models/data/media_youtube.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        cdMedia = gson.fromJson(cdMediaReader, Media.class);
        ytMedia = gson.fromJson(ytMediaReader, Media.class);
    }

    @Test
    public void testCdMedia() throws BasicModel.FieldNotDefinedException {
        assertNotNull(cdMedia);
        assertEquals(cdMedia.getMediaType(),
                     Media.TYPE.CD_PHOTO_THREAD);
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
                     Media.TYPE.YOUTUBE);
        assertEquals(ytMedia.getForeignKey(), "RpSgUrsghv4");
        assertFalse(ytMedia.getDetails().isJsonNull());
    }
}
