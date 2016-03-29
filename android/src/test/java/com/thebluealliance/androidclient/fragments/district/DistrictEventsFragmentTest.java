package com.thebluealliance.androidclient.fragments.district;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class DistrictEventsFragmentTest {

    DistrictEventsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = DistrictEventsFragment.newInstance("2015ne");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}