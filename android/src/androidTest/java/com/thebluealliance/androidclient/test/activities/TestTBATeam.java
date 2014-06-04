package com.thebluealliance.androidclient.test.activities;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;

/**
 * Runs tests on a FRC team page to ensure it is handling data properly.
 *
 * Created by Bryce Matsuda on 6/3/14.
 */
public class TestTBATeam extends ActivityInstrumentationTestCase2<ViewTeamActivity> {

    private ViewTeamActivity teamActivity;
    private static final String teamKey = "frc1540";
    private TextView teamName, teamFullName, location;

    public TestTBATeam() {
        super(ViewTeamActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getContext(), ViewTeamActivity.class);
        intent.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);
        setActivityIntent(intent);
        teamActivity = getActivity();
        teamName = (TextView) teamActivity.findViewById(R.id.team_name);
        teamFullName = (TextView) teamActivity.findViewById(R.id.team_full_name);
        location = (TextView) teamActivity.findViewById(R.id.team_location);

    }

    /**
     * Makes sure correct team info is displayed.
     */
    public void testTeamInfoDisplay(){
        assertNotNull(teamActivity);
        assertNotNull(teamName);
        assertNotNull(teamFullName);
        assertNotNull(location);

        assertEquals("Flaming Chickens", teamName.getText().toString());
        assertEquals("aka Catlin Gabel High School", teamFullName.getText().toString());
        assertEquals("Portland, OR, USA", location.getText().toString());

        assertEquals("Fire Hazards".equals(teamName.getText().toString()), false);
        assertEquals("aka Spirent Communications & Hawaii Baptist Academy".equals
                                        (teamFullName.getText().toString()), false);
        assertEquals("Honolulu, HI, USA".equals(location.getText().toString()), false);
    }

}
