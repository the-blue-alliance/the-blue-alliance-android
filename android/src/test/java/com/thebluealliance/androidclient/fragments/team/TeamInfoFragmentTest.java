package com.thebluealliance.androidclient.fragments.team;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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
public class TeamInfoFragmentTest extends BaseFragmentTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);


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