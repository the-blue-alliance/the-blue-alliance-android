package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(DefaultTestRunner.class)
public class TeamListFragmentTest extends BaseFragmentTest {

    TeamListFragment mFragment;

    @Before
    public void setUp() {
        mFragment = TeamListFragment.newInstance(0);
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}