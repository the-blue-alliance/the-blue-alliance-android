package com.thebluealliance.androidclient.test.activities;

/**
 * Runs tests on the Events by Week fragment /Start activity.
 *
 *
 * Created by Bryce Matsuda on 6/3/14.
 */
import android.test.ActivityInstrumentationTestCase2;
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
    private TextView yearTextView;
    private ListView list;
    private Object item;
    private String eventKey, eventName, eventLocation, eventDate;

    public TestTBAStartActivity(){
        super(StartActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        startActivity = getActivity();
        yearTextView = (TextView) startActivity.findViewById(R.id.year);
        list = (ListView) startActivity.findViewById(R.id.list);
        item = ((ListViewAdapter) list.getAdapter()).getItem(1);
        eventKey = ((ListElement) item).getKey();

        eventName = ((EventListElement) item).getEventName();
        eventDate = ((EventListElement) item).getEventDates();
        eventLocation = ((EventListElement) item).getEventLocation();
    }

    /**
     * Test if correct text is being displayed
     */
    public void testEventInfoDisplay() {

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

        assertEquals("2014", yearTextView.getText().toString());
        assertEquals("2014ilil", eventKey);
        assertEquals("Central Illinois Regional", eventName);
        assertEquals("Feb 27 to Mar 1, 2014", eventDate);
        assertEquals("Pekin, IL, USA", eventLocation);
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
