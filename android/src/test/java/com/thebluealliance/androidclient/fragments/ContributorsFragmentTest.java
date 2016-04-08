package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(IntegrationRobolectricRunner.class)
public class ContributorsFragmentTest extends BaseFragmentTest{

    ContributorsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = ContributorsFragment.newInstance();
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}