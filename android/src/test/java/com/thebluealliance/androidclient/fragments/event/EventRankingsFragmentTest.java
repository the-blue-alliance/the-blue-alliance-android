package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.fragments.framework.BaseFragmentTest;
import com.thebluealliance.androidclient.fragments.framework.FragmentTestDriver;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.RankingListElement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(IntegrationRobolectricRunner.class)
public class EventRankingsFragmentTest extends BaseFragmentTest {

    EventRankingsFragment mFragment;
    List<ListItem> mRankings;

    @Before
    public void setUp() {
        mFragment = EventRankingsFragment.newInstance("2015cthar");
        mRankings = new ArrayList<>();
        mRankings.add(new RankingListElement(
                "frc1124",
                "1124",
                "UberBots",
                1,
                "2-0-0",
                "moo"));
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
        // FragmentTestDriver.testListViewClick(mFragment, mRankings, TeamAtEventActivity.class);
    }
}