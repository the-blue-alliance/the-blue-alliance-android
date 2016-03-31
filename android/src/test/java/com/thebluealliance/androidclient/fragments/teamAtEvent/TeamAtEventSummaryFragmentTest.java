package com.thebluealliance.androidclient.fragments.teamAtEvent;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class TeamAtEventSummaryFragmentTest {

    TeamAtEventSummaryFragment mFragment;

    @Before
    public void setUp() {
        mFragment = TeamAtEventSummaryFragment.newInstance("frc1124", "2015cthar");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}