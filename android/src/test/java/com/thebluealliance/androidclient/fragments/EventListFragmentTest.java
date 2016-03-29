package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class EventListFragmentTest {

    private EventListFragment mFragment;

    @Before
    public void setUp() {
        mFragment = EventListFragment.newInstance(2015, 1, -1, "Week 1");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }

}
