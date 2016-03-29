package com.thebluealliance.androidclient.fragments.district;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class DistrictListFragmentTest {

    DistrictListFragment mFragment;

    @Before
    public void setUp() {
        mFragment = DistrictListFragment.newInstance(2016);
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}