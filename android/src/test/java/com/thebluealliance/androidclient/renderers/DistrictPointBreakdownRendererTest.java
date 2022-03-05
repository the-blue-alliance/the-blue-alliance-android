package com.thebluealliance.androidclient.renderers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class DistrictPointBreakdownRendererTest {

    private DistrictPointBreakdown mBreakdown;
    private DistrictPointBreakdownRenderer mRenderer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        JsonArray rankList = ModelMaker.getModel(JsonArray.class, "2015ne_rankings");
        mBreakdown = TBAAndroidModule.getGson().fromJson(rankList.get(0), DistrictPointBreakdown.class);
        mBreakdown.setDistrictKey("2015ne");
        mRenderer = new DistrictPointBreakdownRenderer();
    }

    @Test
    public void testRenderFromModel() {
        DistrictTeamListElement element = mRenderer.renderFromModel(mBreakdown, null);
        assertNotNull(element);
        assertEquals(element.districtKey, mBreakdown.getDistrictKey());
        assertEquals(element.teamKey, mBreakdown.getTeamKey());
        assertEquals(element.teamName, mBreakdown.getTeamName());
        assertEquals(element.teamRank, mBreakdown.getRank().intValue());
        assertEquals(element.totalPoints, mBreakdown.getTotal().intValue());
    }

}