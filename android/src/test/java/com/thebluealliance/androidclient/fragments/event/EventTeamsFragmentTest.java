package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.TeamRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class EventTeamsFragmentTest extends BaseFragmentTest {

    EventTeamsFragment mFragment;
    List<ListItem> mTeams;

    @Before
    public void setUp() {
        mFragment = EventTeamsFragment.newInstance("2015necmp");
        Team team = ModelMaker.getModel(Team.class, "frc1124");
        TeamRenderer renderer = new TeamRenderer(null);
        mTeams = new ArrayList<>();
        mTeams.add(renderer.renderFromModel(team, TeamRenderer.RENDER_DETAILS_BUTTON));
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
    public void testTeamItemClick() {
        //FragmentTestDriver.testListViewClick(mFragment, mTeams, TeamAtEventActivity.class);
    }

}