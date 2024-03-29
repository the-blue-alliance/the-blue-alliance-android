package com.thebluealliance.androidclient.datafeed.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AddDistrictTeamKeyTest {

    private AddDistrictTeamKey mKeyAdder;
    private DistrictRanking mDistrictTeam;
    private String mDistrictKey;

    @Before
    public void setUp() {
        mDistrictTeam = ModelMaker.getModel(DistrictRanking.class, "2015ne_district_team");
        mDistrictKey = "2015ne";
        mKeyAdder = new AddDistrictTeamKey(mDistrictKey);
    }

    @Test
    public void testAddDistrictTeamKey()  {
        List<DistrictRanking> teamList = new ArrayList<>();
        teamList.add(mDistrictTeam);

        teamList = mKeyAdder.call(teamList);
        assertNotNull(teamList);
        assertEquals(teamList.size(), 1);
        assertEquals(teamList.get(0), mDistrictTeam);

        String expectedKey = DistrictTeamHelper.generateKey(mDistrictTeam.getTeamKey(), mDistrictKey);
        assertEquals(mDistrictTeam.getKey(), expectedKey);
        assertEquals(mDistrictTeam.getDistrictKey(), mDistrictKey);
    }
}