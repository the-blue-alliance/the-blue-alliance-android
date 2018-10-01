package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.types.DistrictType;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DistrictHelperTest {

    @Test
    public void testValidateDistrictKey() {
        String[] validKeys = new String[]{"2015ne", "2016in", "2014mar"};
        String[] invliadKeys = new String[]{"f", "meowne"};

        for (String key : validKeys) {
            assertTrue(DistrictHelper.validateDistrictKey(key));
        }

        for (String key : invliadKeys) {
            assertFalse(DistrictHelper.validateDistrictKey(key));
        }
    }

    @Test
    public void testExtractYearFromKey() {
        String key = "2015ne";
        int year = DistrictHelper.extractYearFromKey(key);
        assertEquals(year, 2015);
    }

    @Test
    public void testExtractAbbrevFromKey() {
        String key = "2015ne";
        String abbrev = DistrictHelper.extractAbbrevFromKey(key);
        assertEquals(abbrev, "ne");
    }

    @Test
    public void testGenerateKey() {
        String abbrev = "ne";
        int year = 2015;
        String key = DistrictHelper.generateKey(abbrev, year);
        assertEquals(key, "2015ne");
    }

    @Test
    public void testDistrictTypeFromKey() {
        String key = "2015ne";
        DistrictType type = DistrictHelper.districtTypeFromKey(key);
        assertEquals(type, DistrictType.NEW_ENGLAND);
    }
}