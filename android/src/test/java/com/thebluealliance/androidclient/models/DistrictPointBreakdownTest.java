package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DistrictPointBreakdownTest {

    private DistrictPointBreakdown mBreakdown;

    @Before
    public void readJson() {
        mBreakdown = ModelMaker.getModel(DistrictPointBreakdown.class, "team_at_district_points");
    }

    @Test
    public void testDistrictPointBreakdown() {
        assertNotNull(mBreakdown);
        assertEquals(mBreakdown.getTotal().intValue(), 87);
        assertEquals(mBreakdown.getQualPoints().intValue(), 36);
        assertEquals(mBreakdown.getElimPoints().intValue(), 30);
        assertEquals(mBreakdown.getAlliancePoints().intValue(), 6);
        assertEquals(mBreakdown.getAwardPoints().intValue(), 15);
    }

}