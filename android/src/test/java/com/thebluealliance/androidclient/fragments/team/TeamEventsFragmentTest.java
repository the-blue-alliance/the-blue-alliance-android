package com.thebluealliance.androidclient.fragments.team;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@Ignore
@RunWith(DefaultTestRunner.class)
public class TeamEventsFragmentTest extends BaseFragmentTest {

    TeamEventsFragment mFragment;
    List<ListItem> mEvents;

    @Before
    public void setUp() {
        mFragment = TeamEventsFragment.newInstance("frc1124", 2015);
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
        // FragmentTestDriver.testListViewClick(mFragment, mEvents, TeamAtEventActivity.class);
    }
}