package com.thebluealliance.androidclient.fragments.teamAtEvent;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(DefaultTestRunner.class)
public class TeamAtEventStatsFragmentTest extends BaseFragmentTest {

    TeamAtEventStatsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = TeamAtEventStatsFragment.newInstance("frc1124", "2015cthar");
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }

    @Test
    public void testNoDataBinding() {
        FragmentTestDriver.testNoDataBindings(mFragment, R.id.no_data);
    }
}