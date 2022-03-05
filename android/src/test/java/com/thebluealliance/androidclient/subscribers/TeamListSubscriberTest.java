package com.thebluealliance.androidclient.subscribers;

import static org.mockito.Mockito.verify;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.TeamRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;

import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class TeamListSubscriberTest {

    @Mock TeamRenderer mRenderer;

    TeamListSubscriber mSubscriber;
    List<Team> mTeams;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSubscriber = new TeamListSubscriber(mRenderer);
        mTeams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mTeams);
    }

    @Test
    public void testParsedDataNoButton()  {
        mSubscriber.setRenderMode(TeamRenderer.RENDER_BASIC);
        DatafeedTestDriver.getParsedData(mSubscriber, mTeams);

        for (int i = 0; i < mTeams.size(); i++) {
            verify(mRenderer).renderFromModel(mTeams.get(i), TeamRenderer.RENDER_BASIC);
        }
    }

    @Test
    public void testParsedDataButton()  {
        mSubscriber.setRenderMode(TeamRenderer.RENDER_DETAILS_BUTTON);
        DatafeedTestDriver.getParsedData(mSubscriber, mTeams);

        for (int i = 0; i < mTeams.size(); i++) {
            verify(mRenderer).renderFromModel(mTeams.get(i), TeamRenderer.RENDER_DETAILS_BUTTON);
        }
    }
}