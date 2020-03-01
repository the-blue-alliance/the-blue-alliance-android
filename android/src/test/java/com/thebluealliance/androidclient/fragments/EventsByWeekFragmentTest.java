package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
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