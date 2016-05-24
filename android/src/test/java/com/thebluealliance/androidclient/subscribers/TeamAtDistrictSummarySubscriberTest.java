package com.thebluealliance.androidclient.subscribers;

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
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamAtDistrictSummarySubscriberTest {

    @Mock Database mDb;
    @Mock Resources mResources;
    @Mock EventBus mEventBus;

    private TeamAtDistrictSummarySubscriber mSubscriber;
    private DistrictTeam mDistrictTeam;

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
        mDistrictTeam = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings").get(0);
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistrictTeam);
        verify(mEventBus).post(any(ActionBarTitleEvent.class));
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mDistrictTeam);

        assertEquals(5, data.size());
        LabelValueListItem rank = getItemAtPosition(0, data);
        LabelValueDetailListItem event1 = getDetailItemAtPoistion(1, data);
        LabelValueDetailListItem event2 = getDetailItemAtPoistion(2, data);
        LabelValueDetailListItem cmp = getDetailItemAtPoistion(3, data);
        LabelValueListItem total = getItemAtPosition(4, data);
        String event1Key = EventTeamHelper.generateKey("2015ctwat", "frc1124");
        String event2Key = EventTeamHelper.generateKey("2015manda", "frc1124");
        String cmpKey = EventTeamHelper.generateKey("2015necmp", "frc1124");

        assertEquals(new LabelValueListItem("District Rank", "26th"), rank);
        assertEquals(new LabelValueDetailListItem("2015ctwat", "26 Points", event1Key), event1);
        assertEquals(new LabelValueDetailListItem("2015manda", "44 Points", event2Key), event2);
        assertEquals(new LabelValueDetailListItem("2015necmp", "87 Points", cmpKey), cmp);
        assertEquals(new LabelValueListItem("Total Points", "157 Points"), total);
    }

    private static LabelValueListItem getItemAtPosition(int position, List<ListItem> data) {
        return (LabelValueListItem) data.get(position);
    }

    private static LabelValueDetailListItem getDetailItemAtPoistion(int position, List<ListItem> data) {
        return (LabelValueDetailListItem) data.get(position);
    }
}