package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.EventAwardsEvent;
import com.thebluealliance.androidclient.eventbus.EventMatchesEvent;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class TeamAtEventSummarySubscriberTest {
    @Mock Context mContext;
    @Mock Event mEvent;
    @Mock EventMatchesEvent mMatchesEvent;
    @Mock EventAwardsEvent mAwardsEvent;
    @Mock MatchRenderer mMatchRenderer;
    @Mock AppConfig mAppConfig;
    @Mock EventBus mEventBus;
    @Mock Team mTeam;

    TeamAtEventSummarySubscriber mSubscriber;
    TeamAtEventStatus mStatus;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
        when(mContext.getResources().getString(anyInt())).thenReturn("");
        mSubscriber = new TeamAtEventSummarySubscriber(mContext, mAppConfig, mEventBus,
                                                       mMatchRenderer);
        mSubscriber.setTeamAndEventKeys("frc1519", "2015necmp");
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        mStatus = ModelMaker.getModel(TeamAtEventStatus.class, "frc1519_2015necmp_status");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        mSubscriber.onEventMatchesLoaded(mMatchesEvent);
        mSubscriber.onEventAwardsLoaded(mAwardsEvent);
        DatafeedTestDriver.testSimpleParsing(mSubscriber,  new TeamAtEventSummarySubscriber.Model(mStatus, mEvent, mTeam));
    }

    @Test
    public void testUnplayedEvent() {
        DatafeedTestDriver.testSimpleParsing(mSubscriber,  new TeamAtEventSummarySubscriber.Model(null, mEvent, mTeam));
    }
}