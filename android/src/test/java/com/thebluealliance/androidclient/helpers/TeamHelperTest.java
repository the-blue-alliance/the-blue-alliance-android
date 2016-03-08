package com.thebluealliance.androidclient.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamHelperTest {

    @Test
    public void testValidateKey() {
        String[] validKeys = new String[]{"frc1124", "frc254"};
        String[] invalidKeys = new String[]{null, "", "1124", "frc", "frc11245", "frc254B"};

        for (String key : validKeys) {
            assertTrue(TeamHelper.validateTeamKey(key));
        }

        for (String key : invalidKeys) {
            assertFalse(TeamHelper.validateTeamKey(key));
        }
    }

    @Test
    public void testValidateMultiTeamKey() {
        String[] validKeys = new String[]{"frc1124b", "frc254A"};
        String[] invalidKeys = new String[]{null, "", "frc1124", "frc254ab", "frc1114A%"};

        for (String key : validKeys) {
            assertTrue(TeamHelper.validateMultiTeamKey(key));
        }

        for (String key : invalidKeys) {
            assertFalse(TeamHelper.validateMultiTeamKey(key));
        }
    }

    @Test
    public void testBaseTeamKey() {
        assertEquals("frc432", TeamHelper.baseTeamKey("frc432"));
        assertEquals("frc432", TeamHelper.baseTeamKey("frc432B"));
        assertEquals("", TeamHelper.baseTeamKey(""));
        assertNull(TeamHelper.baseTeamKey(null));
    }

    @Test
    public void testGetTeamNumber() {
        assertEquals(-1, TeamHelper.getTeamNumber(null));
        assertEquals(1124, TeamHelper.getTeamNumber("frc1124"));
        assertEquals(254, TeamHelper.getTeamNumber("frc254"));
    }
}