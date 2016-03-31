package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class EventDistrictPointsFragmentTest {

    EventDistrictPointsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = EventDistrictPointsFragment.newInstance("2015cthar");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}