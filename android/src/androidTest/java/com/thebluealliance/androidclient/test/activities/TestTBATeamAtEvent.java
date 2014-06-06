package com.thebluealliance.androidclient.test.activities;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;

/**
 *
 * Runs test on a Team @ Event page to ensure data is handled properly
 *
 * Created by Bryce Matsuda on 6/5/14.
 */
public class TestTBATeamAtEvent extends ActivityInstrumentationTestCase2<TeamAtEventActivity>{

    private TeamAtEventActivity atEventActivity;
    private static final String teamKey = "frc359";
    private static final String eventKey = "2014mndu2";
    private TextView results;
    // Pause the activity for a bit while the information loads (in case of slow device/emulator)
    private Thread pauseActivity = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(8000);
                // Catch if something goes terribly wrong
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    public TestTBATeamAtEvent() {
        super(TeamAtEventActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent (getInstrumentation().getContext(), TeamAtEventActivity.class);
        intent.putExtra(TeamAtEventActivity.TEAM, teamKey);
        intent.putExtra(TeamAtEventActivity.EVENT, eventKey);
        setActivityIntent(intent);
        atEventActivity = getActivity();
        results = (TextView) atEventActivity.findViewById(R.id.team_record);
    }

    protected void tearDown() throws Exception{
        atEventActivity.finish();
        super.tearDown();
    }

    /**
     * Make sure correct info is displayed.
     */
    public void testAtEventInfoDisplay()
    {
        pauseActivity.run();
        assertEquals("Overall, Team 359 was Rank 4 \nand had a record of 14-2-0", results.getText().toString());
    }


    /**
     * Make sure items aren't null.
     */
    public void testNotNull(){
        assertNotNull(atEventActivity);
        assertNotNull(results);
    }
}
