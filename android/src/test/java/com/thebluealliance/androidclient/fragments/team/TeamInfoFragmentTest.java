package com.thebluealliance.androidclient.fragments.team;

import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class TeamInfoFragmentTest extends BaseFragmentTest {

    TeamInfoFragment mFragment;

    @Before
    public void setUp() {
        mFragment = TeamInfoFragment.newInstance("frc1124");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}