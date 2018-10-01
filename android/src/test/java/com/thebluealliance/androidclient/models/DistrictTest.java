package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.types.DistrictType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
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
        assertEquals(district.getEnum(), DistrictType.NEW_ENGLAND.ordinal());
    }
}
