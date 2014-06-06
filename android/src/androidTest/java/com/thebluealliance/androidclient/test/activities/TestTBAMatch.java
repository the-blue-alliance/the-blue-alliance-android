package com.thebluealliance.androidclient.test.activities;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;

/**
 * Runs tests on a FRC match page to ensure data is being handled properly.
 *
 * Created by Bryce Matsuda on 6/3/14.
 */
public class TestTBAMatch extends ActivityInstrumentationTestCase2<ViewMatchActivity>{

    private ViewMatchActivity matchActivity;
    private static final String matchKey = "2014onto_qm4";
    private TextView eventName, matchName;
    private TextView red1, red2, red3, redScore;
    private TextView blue1, blue2, blue3, blueScore;

    public TestTBAMatch() {
       super(ViewMatchActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getContext(), ViewMatchActivity.class);
        intent.putExtra(ViewMatchActivity.MATCH_KEY, matchKey);
        setActivityIntent(intent);
        matchActivity = getActivity();

        eventName = (TextView) matchActivity.findViewById(R.id.event_name);
        matchName = (TextView) matchActivity.findViewById(R.id.match_name);

        red1 = (TextView) matchActivity.findViewById(R.id.red1);
        red2 = (TextView) matchActivity.findViewById(R.id.red2);
        red3 = (TextView) matchActivity.findViewById(R.id.red3);
        redScore = (TextView) matchActivity.findViewById(R.id.red_score);

        blue1 = (TextView) matchActivity.findViewById(R.id.blue1);
        blue2 = (TextView) matchActivity.findViewById(R.id.blue2);
        blue3 = (TextView) matchActivity.findViewById(R.id.blue3);
        blueScore = (TextView) matchActivity.findViewById(R.id.blue_score);
    }

    /**
     * Make sure correct match info is being displayed
     */
    public void testMatchInfoDisplay(){

        // Pause the activity for a bit while the information loads (in case of slow device/emulator)
        Thread pauseActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    // Catch if something goes terribly wrong
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        pauseActivity.run();

        assertEquals("Quals 4",matchName.getText().toString());
        assertEquals("Greater Toronto East Regional", eventName.getText().toString());
        assertEquals("1114", red1.getText().toString());
        assertEquals("3683", red2.getText().toString());
        assertEquals("2044omgrobots".equals(red3.getText().toString()), false);
        assertEquals("420", redScore.getText().toString());

        assertEquals("288", blue1.getText().toString());
        assertEquals(!("1337".equals(blue2.getText().toString())), true);
        assertEquals("2198", blue3.getText().toString());
        assertEquals("57", blueScore.getText().toString());
    }

    /**
     * Check if items aren't null
     */
    public void testNotNull(){

        assertNotNull(matchActivity);
        assertNotNull(eventName);
        assertNotNull(matchName);

    }
}
