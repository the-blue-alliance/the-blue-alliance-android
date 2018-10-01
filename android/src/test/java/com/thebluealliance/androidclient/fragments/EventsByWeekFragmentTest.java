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
public class EventsByWeekFragmentTest extends BaseFragmentTest {

    EventsByWeekFragment mFragment;

    @Before
    public void setUp() {
        mFragment = EventsByWeekFragment.newInstance(2015);
    }

    @Test
    public void testLifeycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}