package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class AddDistrictTeamKeyTest {

    private AddDistrictTeamKey mKeyAdder;
    private DistrictTeam mDistrictTeam;
    private String mDistrictShort;
    private int mYear;

    @Before
    public void setUp() {
        mDistrictTeam = ModelMaker.getModel(DistrictTeam.class, "2015ne_district_team");
        mDistrictShort = "ne";
        mYear = 2015;
        mKeyAdder = new AddDistrictTeamKey(mDistrictShort, mYear);
    }

    @Test
    public void testAddDistrictTeamKey() throws BasicModel.FieldNotDefinedException {
        List<DistrictTeam> teamList = new ArrayList<>();
        teamList.add(mDistrictTeam);

        teamList = mKeyAdder.call(teamList);
        assertNotNull(teamList);
        assertEquals(teamList.size(), 1);
        assertEquals(teamList.get(0), mDistrictTeam);

        String districtKey = DistrictHelper.generateKey(mDistrictShort, mYear);
        String expectedKey = DistrictTeamHelper.generateKey(mDistrictTeam.getTeamKey(), districtKey);
        int districtEnum = DistrictHelper.districtTypeFromKey(districtKey).ordinal();
        assertEquals(mDistrictTeam.getKey(), expectedKey);
        assertEquals(mDistrictTeam.getDistrictEnum(), districtEnum);
        assertEquals(mDistrictTeam.getYear(), mYear);
        assertEquals(mDistrictTeam.getDistrictKey(), districtKey);
    }
}