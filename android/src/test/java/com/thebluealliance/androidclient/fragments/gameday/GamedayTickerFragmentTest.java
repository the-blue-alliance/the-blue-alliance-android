package com.thebluealliance.androidclient.fragments.gameday;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DefaultTestRunner.class)
public class GamedayTickerFragmentTest extends BaseFragmentTest {

    GamedayTickerFragment mFragment;

    @Before
    public void setUp() {
        mFragment = GamedayTickerFragment.newInstance();
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}