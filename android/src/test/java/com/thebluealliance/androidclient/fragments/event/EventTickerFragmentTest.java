package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DefaultTestRunner.class)
public class EventTickerFragmentTest extends BaseFragmentTest {

    EventTickerFragment mFragment;

    @Before
    public void setUp() {
        mFragment = EventTickerFragment.newInstance("2015cthar");
    }

    @Test @Ignore
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}