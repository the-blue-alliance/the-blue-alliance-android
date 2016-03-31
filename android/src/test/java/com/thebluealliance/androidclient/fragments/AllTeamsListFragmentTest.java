package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class AllTeamsListFragmentTest {

    AllTeamsListFragment mFragment;

    @Before
    public void setUp() {
        mFragment = AllTeamsListFragment.newInstance(0);
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}