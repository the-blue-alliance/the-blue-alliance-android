package com.thebluealliance.androidclient.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DistrictTest {
    District district;

    @Before
    public void readJsonData() {
        district = ModelMaker.getModelList(District.class, "2015_districts").get(3);
    }

    @Test
    public void testDistrictModel()  {
        assertNotNull(district);
        assertEquals(district.getDisplayName(), "New England");
        assertEquals(district.getAbbreviation(), "ne");
    }
}
