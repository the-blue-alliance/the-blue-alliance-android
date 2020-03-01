package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listitems.LabelValueDetailListItem;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TeamAtDistrictSummarySubscriberTest {

    @Mock Database mDb;
    @Mock Resources mResources;
    @Mock EventBus mEventBus;

    private TeamAtDistrictSummarySubscriber mSubscriber;
    private DistrictRanking mDistrictTeam;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockEventsTable(mDb);
        when(mResources.getString(R.string.district_point_rank)).thenReturn("District Rank");
        when(mResources.getString(R.string.district_points_format)).thenReturn("%d Points");
        when(mResources.getString(R.string.total_district_points)).thenReturn("Total Points");
        when(mResources.getString(R.string.team_actionbar_title)).thenReturn("Team %s");

        mSubscriber = new TeamAtDistrictSummarySubscriber(mDb, mResources, mEventBus);
        mSubscriber.setTeamKey("frc1124");
        mSubscriber.setDistrictKey("2015ne");
        mDistrictTeam = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings").get(0);
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistrictTeam);
        verify(mEventBus).post(any(ActionBarTitleEvent.class));
    }

    @Test
    public void testParsedData()  {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mDistrictTeam);

        assertEquals(5, data.size());
        LabelValueListItem rank = getItemAtPosition(0, data);
        LabelValueDetailListItem event1 = getDetailItemAtPoistion(1, data);
        LabelValueDetailListItem event2 = getDetailItemAtPoistion(2, data);
        LabelValueDetailListItem cmp = getDetailItemAtPoistion(3, data);
        LabelValueListItem total = getItemAtPosition(4, data);
        String event1Key = EventTeamHelper.generateKey("2015nhnas", "frc1519");
        String event2Key = EventTeamHelper.generateKey("2015manda", "frc1519");
        String cmpKey = EventTeamHelper.generateKey("2015necmp", "frc1519");

        assertEquals(new LabelValueListItem("District Rank", "1st"), rank);
        assertEquals(new LabelValueDetailListItem("2015nhnas", "73 Points", event1Key), event1);
        assertEquals(new LabelValueDetailListItem("2015manda", "73 Points", event2Key), event2);
        assertEquals(new LabelValueDetailListItem("2015necmp", "219 Points", cmpKey), cmp);
        assertEquals(new LabelValueListItem("Total Points", "365 Points"), total);
    }

    private static LabelValueListItem getItemAtPosition(int position, List<ListItem> data) {
        return (LabelValueListItem) data.get(position);
    }

    private static LabelValueDetailListItem getDetailItemAtPoistion(int position, List<ListItem> data) {
        return (LabelValueDetailListItem) data.get(position);
    }
}