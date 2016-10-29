package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DefaultTestRunner.class)
public class AllTeamsListFragmentTest extends BaseFragmentTest {

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