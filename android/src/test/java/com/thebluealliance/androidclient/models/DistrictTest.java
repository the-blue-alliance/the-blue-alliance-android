package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.DistrictHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DistrictTest {
    District district;

    @Before
    public void readJsonData() {
        district = ModelMaker.getModel(District.class, "district_ne");
    }

    @Test
    public void testDistrictModel() throws BasicModel.FieldNotDefinedException {
        assertNotNull(district);
        assertEquals(district.getName(), "New England");
        assertEquals(district.getAbbreviation(), "ne");
        assertEquals(district.getEnum(), DistrictHelper.DISTRICTS.NEW_ENGLAND.ordinal());
    }
}
