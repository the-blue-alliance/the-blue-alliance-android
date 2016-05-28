package com.thebluealliance.androidclient.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventTeamHelperTest {

    @Test
    public void testValidateEventTeamKey() {
        String[] validKeys = new String[]{"2015arc_frc1124", "2015gal_frc254", "2015gal_frc254B"};
        String[] invalidKeys = new String[]{null, "", "1124", "frc", "frc11245", "frc254B", "2015arc", "2015arc_", "2015arc_frc"};

        for (String key : validKeys) {
            assertTrue(EventTeamHelper.validateEventTeamKey(key));
        }

        for (String key : invalidKeys) {
            assertFalse(EventTeamHelper.validateEventTeamKey(key));
        }
    }

    @Test
    public void testGetEventKey() {
        assertEquals("2015arc", EventTeamHelper.getEventKey("2015arc_frc111"));
    }

    @Test
    public void testGetTeamKey() {
        assertEquals("frc111", EventTeamHelper.getTeamKey("2015arc_frc111"));
        assertEquals("frc111B", EventTeamHelper.getTeamKey("2015arc_frc111B"));
    }
}
