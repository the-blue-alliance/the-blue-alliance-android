package com.thebluealliance.androidclient.fragments.match;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class MatchInfoFragmentTest {

    MatchInfoFragment mFragment;

    @Before
    public void setUp() {
        mFragment = MatchInfoFragment.newInstance("2015cthar_f1m1");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}