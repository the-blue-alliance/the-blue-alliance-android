package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.IntegrationRobolectricRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import android.support.v4.app.FragmentManager;

import static org.junit.Assert.assertNotNull;

@RunWith(IntegrationRobolectricRunner.class)
public class TestEventListFragment {

    private static final String FRAGMENT_TAG = "fragment";

    private ActivityController mActivityController;
    private BaseTestActivity mActivity;
    private EventListFragment mFragment;

    @Before
    public void setUp() {
        mActivityController = Robolectric.buildActivity(BaseTestActivity.class);
        mActivity = (BaseTestActivity) mActivityController.create().start().resume().visible().get();
        mFragment = EventListFragment.newInstance(2015, 1, 3, "Week 1", true);

        FragmentManager manager = mActivity.getSupportFragmentManager();
        manager.beginTransaction()
                .add(mFragment, FRAGMENT_TAG).commit();
    }

    @Test
    public void testNotNull() {
        assertNotNull(mActivityController);
        assertNotNull(mActivity);
        assertNotNull(mFragment);
    }

}
