package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.DefaultTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(DefaultTestRunner.class)
public class DistrictTeamHelperTest {

    @Test
    public void testValidateKey() {
        String[] validKeys = new String[]{"2015ne_frc1124", "2014mar_frc11"};
        String[] invalidKeys = new String[]{"2015_frc1124", "2015ne_1124", "f", "", null};

        for (String key : validKeys) {
            assertTrue(DistrictTeamHelper.validateDistrictTeamKey(key));
        }

        for (String key : invalidKeys) {
            assertFalse(DistrictTeamHelper.validateDistrictTeamKey(key));
        }
    }

    @Test
    public void testGetDistrictKey() {
        String districtTeamKey = "2015ne_frc1124";
        String districtKey = DistrictTeamHelper.getDistrictKey(districtTeamKey);
        assertEquals(districtKey, "2015ne");
    }

    @Test
    public void testGetTeamKey() {
        String districtTeamKey = "2015ne_frc1124";
        String teamKey = DistrictTeamHelper.getTeamKey(districtTeamKey);
        assertEquals(teamKey, "frc1124");
    }

    @Test
    public void testGenerateKey() {
        String teamKey = "frc1124";
        String districtKey = "2015ne";
        String districtTeamKey = DistrictTeamHelper.generateKey(teamKey, districtKey);
        assertEquals(districtTeamKey, "2015ne_frc1124");
    }
}