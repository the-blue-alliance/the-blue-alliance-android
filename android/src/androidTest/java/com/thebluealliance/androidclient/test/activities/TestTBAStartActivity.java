package com.thebluealliance.androidclient.test.activities;

/**
 * Runs tests on the Events by Week activity.
 *
 *
 * Created by Bryce Matsuda on 6/3/14.
 */
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.StartActivity;

public class TestTBAStartActivity extends ActivityInstrumentationTestCase2<StartActivity>{

    private StartActivity startActivity;
    private TextView yearTextView;

    public TestTBAStartActivity(){
        super(StartActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        startActivity = getActivity();
        yearTextView = (TextView) startActivity.findViewById(R.id.year);
    }

    public void testTextView() {
        assertEquals("2014", yearTextView.getText().toString());
    }

}
