package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class DistrictTeamExtractorTest {

    private List<DistrictRanking> mDistrictTeams;
    private String mSearchTeamKey;
    private String mNotFoundTeamKey;
    private DistrictTeamExtractor mExtractor;

    @Before
    public void setUp() {
        mDistrictTeams = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings");
        mSearchTeamKey = "frc1124";
        mNotFoundTeamKey = "frc254";
        mExtractor = new DistrictTeamExtractor(mSearchTeamKey);
    }

    @Test
    public void testDistrictTeamExtractor()  {
        DistrictRanking extracted = mExtractor.call(mDistrictTeams);
        assertNotNull(extracted);
        assertEquals(extracted.getTeamKey(), mSearchTeamKey);
    }

    @Test
    public void testDistrictTeamExtractorNotFound() {
        mExtractor = new DistrictTeamExtractor(mNotFoundTeamKey);
        DistrictRanking extracted = mExtractor.call(mDistrictTeams);
        assertNull(extracted);
    }
}