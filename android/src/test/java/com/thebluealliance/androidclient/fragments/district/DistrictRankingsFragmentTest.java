package com.thebluealliance.androidclient.fragments.district;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;


@HiltAndroidTest
@Config(application = HiltTestApplication.class)
@RunWith(AndroidJUnit4.class)
public class DistrictRankingsFragmentTest extends BaseFragmentTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    DistrictRankingsFragment mFragment;

    @Before
    public void setUp() {
        mFragment = DistrictRankingsFragment.newInstance("2015ne");
    }

    @Test
    public void setLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }

    @Test
    public void testNoDataBinding() {
        FragmentTestDriver.testNoDataBindings(mFragment, R.id.no_data);
    }
}