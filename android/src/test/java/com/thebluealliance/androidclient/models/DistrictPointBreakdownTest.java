package com.thebluealliance.androidclient.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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