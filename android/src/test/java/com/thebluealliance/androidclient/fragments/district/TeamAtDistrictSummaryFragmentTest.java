package com.thebluealliance.androidclient.fragments.district;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class TeamAtDistrictSummaryFragmentTest extends BaseFragmentTest {

    TeamAtDistrictSummaryFragment mFragment;

    @Before
    public void setUp() {
        mFragment = TeamAtDistrictSummaryFragment.newInstance("frc1124", "2015ne");
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