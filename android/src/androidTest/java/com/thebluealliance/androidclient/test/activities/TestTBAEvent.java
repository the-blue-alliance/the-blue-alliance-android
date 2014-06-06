package com.thebluealliance.androidclient.test.activities;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;

/**
 * Runs tests on a FRC event to ensure data is being handled properly.
 * <p/>
 * Created by Bryce Matsuda on 6/5/14.
 */
public class TestTBAEvent extends ActivityInstrumentationTestCase2<ViewEventActivity> {

    private ViewEventActivity eventActivity;
    private static final String eventKey = "2014pncmp";
    private TextView eventName, eventDate, eventLocation;
    private int viewHeight, viewWidth, screenWidth;
    private float x, fromY;
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

    public TestTBAEvent() {
        super(ViewEventActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getContext(), ViewEventActivity.class);
        intent.putExtra(ViewEventActivity.EVENTKEY, eventKey);
        setActivityIntent(intent);
        eventActivity = getActivity();

        eventName = (TextView) eventActivity.findViewById(R.id.event_name);
        eventDate = (TextView) eventActivity.findViewById(R.id.event_date);
        eventLocation = (TextView) eventActivity.findViewById(R.id.event_location);

        View v = eventActivity.getCurrentFocus();
        viewWidth = v.getWidth();
        viewHeight = v.getHeight();
        x = 500 + (viewWidth / 2.0f);
        fromY = 200 + (viewHeight / 2.0f);
        screenWidth = eventActivity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * Test to make sure event info is being properly displayed.
     */
    public void testEventInfoDisplay() {
        pauseActivity.run();

        assertEquals("Autodesk PNW FRC Championship", eventName.getText().toString());
        assertEquals("Apr 10 to Apr 12, 2014", eventDate.getText().toString());
        assertEquals("Portland, OR, USA", eventLocation.getText().toString());
    }

    /**
     * Makes sure items aren't null.
     */
    public void testNotNull() {
        assertNotNull(eventActivity);
    }

    public void testDrag() {
        TouchUtils.drag(this, (screenWidth - 1), x, fromY, fromY, 5);

        pauseActivity.run();

    }

}
