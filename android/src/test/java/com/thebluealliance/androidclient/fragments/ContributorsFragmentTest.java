package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ContributorsFragmentTest extends BaseFragmentTest{

    ContributorsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = ContributorsFragment.newInstance();
    }

    @Ignore
    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}