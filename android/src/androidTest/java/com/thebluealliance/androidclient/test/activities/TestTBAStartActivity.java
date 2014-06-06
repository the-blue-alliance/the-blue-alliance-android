package com.thebluealliance.androidclient.test.activities;

/**
 * Runs tests on the Events by Week fragment /Start activity.
 *
 *
 * Created by Bryce Matsuda on 6/3/14.
 */
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.StartActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.EventListElement;
import com.thebluealliance.androidclient.datatypes.ListElement;

public class TestTBAStartActivity extends ActivityInstrumentationTestCase2<StartActivity>{

    private StartActivity startActivity;
    private TextView year, eventName, eventDate, eventLocation;
    private ListView list;
    private Object item;
    private String eventKey;
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

    public TestTBAStartActivity(){
        super(StartActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        startActivity = getActivity();
        year = (TextView) startActivity.findViewById(R.id.year);

        View v = startActivity.getCurrentFocus();
        viewWidth = v.getWidth();
        viewHeight = v.getHeight();
        x = 500 + (viewWidth / 2.0f);
        fromY = 200 + (viewHeight / 2.0f);
        screenWidth = startActivity.getWindowManager().getDefaultDisplay().getWidth();
    }

    protected void tearDown() throws Exception{
        startActivity.finish();
        super.tearDown();
    }

    /**
     * Test if correct text is being displayed
     */
    public void testEventInfoDisplay1() {
        pauseActivity.run();

        list = (ListView) startActivity.findViewById(R.id.list);
        item = ((ListViewAdapter) list.getAdapter()).getItem(1);
        eventKey = ((ListElement) item).getKey();
        eventName = (TextView) list.findViewById(R.id.event_name);
        eventDate = (TextView) list.findViewById(R.id.event_dates);
        eventLocation = (TextView) list.findViewById(R.id.event_location);

        assertEquals("2014", year.getText().toString());
        assertEquals("2014ilil", eventKey);
        assertEquals("Central Illinois Regional", eventName.getText().toString());
        assertEquals("Feb 27 to Mar 1, 2014", eventDate.getText().toString());
        assertEquals("Pekin, IL, USA", eventLocation.getText().toString());
    }

    public void testDrag(){
        pauseActivity.run();
        //Drag from center of screen to the leftmost edge of display
        TouchUtils.drag(this, (screenWidth - 1), x, fromY, fromY, 5);
    }

    /**
     * Make sure objects aren't null somehow.
     */
    public void testNotNull(){
        assertNotNull(startActivity);
        assertNotNull(list);
        assertNotNull(item);
        assertNotNull(eventKey);
    }
}
