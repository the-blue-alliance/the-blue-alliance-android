package com.thebluealliance.androidclient.fragments;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;


@HiltAndroidTest
@Config(application = HiltTestApplication.class)
@RunWith(AndroidJUnit4.class)
public class EventListFragmentTest extends BaseFragmentTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    private EventListFragment mFragment;
    private List<ListItem> mEvents;

    @Before
    public void setUp() {
        ArrayList<String> eventKeys = new ArrayList<>();
        eventKeys.add("2015cthar");
        mFragment = EventListFragment.newInstance(2015, eventKeys, true);
        Event event = ModelMaker.getModel(Event.class, "2015cthar");
        EventRenderer renderer = new EventRenderer(null);
        mEvents = new ArrayList<>();
        mEvents.add(renderer.renderFromModel(event, false));
    }

    @Test
    public void testLifecycle() {
        FragmentTestDriver.testLifecycle(mFragment);
    }

    @Test
    public void testNoDataBinding() {
        FragmentTestDriver.testNoDataBindings(mFragment, R.id.no_data);
    }

    @Test
    public void testItemClick() {
        // FragmentTestDriver.testListViewClick(mFragment, mEvents, ViewEventActivity.class);
    }

}
