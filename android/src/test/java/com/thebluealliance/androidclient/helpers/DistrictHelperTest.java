package com.thebluealliance.androidclient.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
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
}