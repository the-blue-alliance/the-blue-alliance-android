package com.thebluealliance.androidclient.test.models;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Ignore
public class DistrictTest {
    District district;

    @Before
    public void readJsonData() {
        BufferedReader districtReader;
        Gson gson = JSONHelper.getGson();
        String basePath = new File("").getAbsolutePath();
        try {
            districtReader = new BufferedReader(
                new FileReader(basePath +
                    "/android/src/test/java/com/thebluealliance/" +
                    "androidclient/test/models/data/district_ne.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        district = gson.fromJson(districtReader,
                                 District.class);
    }

    @Test
    public void testDistrictModel() throws BasicModel.FieldNotDefinedException {
        assertNotNull(district);
        assertEquals(district.getName(), "New England");
        assertEquals(district.getKey(), "ne");
    }
}
