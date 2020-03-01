package com.thebluealliance.androidclient.fragments.gameday;

import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class GamedayTickerFragmentTest extends BaseFragmentTest {

    GamedayTickerFragment mFragment;

    @Before
    public void setUp() {
        mFragment = GamedayTickerFragment.newInstance();
    }

    @Test @Ignore
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}