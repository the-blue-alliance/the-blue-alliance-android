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
public class AwardHelperTest {

    @Test
    public void testCreateEventKey() {
        String eventKey = "2015cthar";
        int awardEnum = 12;

        String awardKey = AwardHelper.createAwardKey(eventKey, awardEnum);
        assertEquals("2015cthar:12", awardKey);
    }

    @Test
    public void testValidateKey() {
        String[] validKeys = new String[]{"2015cthar:12", "2015necmp:1"};
        String[] invalidKeys = new String[]{"asldf:5", "2015cthar:foo", "2015cthar", null};

        for (String key : validKeys) {
            assertTrue(AwardHelper.validateAwardKey(key));
        }

        for (String key : invalidKeys) {
            assertFalse(AwardHelper.validateAwardKey(key));
        }
    }
}