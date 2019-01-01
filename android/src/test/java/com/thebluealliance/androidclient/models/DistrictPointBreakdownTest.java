package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(DefaultTestRunner.class)
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