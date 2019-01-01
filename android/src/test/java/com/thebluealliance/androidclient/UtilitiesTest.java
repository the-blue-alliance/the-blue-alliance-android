package com.thebluealliance.androidclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(DefaultTestRunner.class)
public class UtilitiesTest {

    @Mock private Context context;


    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void teamIntent_noTeamNumber() {
        Uri uri = Uri.parse("http://www.thebluealliance.com/team");
        Intent intent = Utilities.getIntentForTBAUrl(context, uri);

        assertNull(intent);
    }

    @Test
    public void teamIntent_invalidTeamNumber() {
        Uri uri = Uri.parse("http://www.thebluealliance.com/team/a230");
        Intent intent = Utilities.getIntentForTBAUrl(context, uri);

        assertNull(intent);
    }

    @Test
    public void teamIntent_teamNumber() {
        Uri uri = Uri.parse("http://www.thebluealliance.com/team/230");
        Intent intent = Utilities.getIntentForTBAUrl(context, uri);

        assertNotNull(intent);
        assertEquals("frc230", intent.getStringExtra(ViewTeamActivity.EXTRA_TEAM_KEY));
    }

    @Test
    public void teamIntent_leadingZerosInTeamNumber() {
        Uri uri = Uri.parse("http://www.thebluealliance.com/team/0030");
        Intent intent = Utilities.getIntentForTBAUrl(context, uri);

        assertNotNull(intent);
        assertEquals("frc30", intent.getStringExtra(ViewTeamActivity.EXTRA_TEAM_KEY));
    }

    @Test
    public void teamIntent_teamNumberAndYear() {
        Uri uri = Uri.parse("http://www.thebluealliance.com/team/230/2009");
        Intent intent = Utilities.getIntentForTBAUrl(context, uri);

        assertNotNull(intent);
        assertEquals("frc230", intent.getStringExtra(ViewTeamActivity.EXTRA_TEAM_KEY));
        assertEquals(2009, intent.getIntExtra(ViewTeamActivity.TEAM_YEAR, 0));
    }

}
