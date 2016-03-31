package com.thebluealliance.androidclient.fragments.team;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class TeamEventsFragmentTest {

    TeamEventsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = TeamEventsFragment.newInstance("frc1124", 2015);
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}