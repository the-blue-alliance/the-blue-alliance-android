package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class EventTeamsFragmentTest {

    EventTeamsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = EventTeamsFragment.newInstance("2015cthar");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}