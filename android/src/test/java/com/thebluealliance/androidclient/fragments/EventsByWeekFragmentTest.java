package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class EventsByWeekFragmentTest {

    EventsByWeekFragment mFragment;

    @Before
    public void setUp() {
        mFragment = EventsByWeekFragment.newInstance(2015);
    }

    @Test
    public void testLifeycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}