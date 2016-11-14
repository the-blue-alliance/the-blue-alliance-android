package com.thebluealliance.androidclient.fragments.mytba;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DefaultTestRunner.class)
public class MyTBAFragmentTest extends BaseFragmentTest {

    MyTBAFragment mFragment;

    @Before
    public void setUp() {
        mFragment = new MyTBAFragment();
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }
}