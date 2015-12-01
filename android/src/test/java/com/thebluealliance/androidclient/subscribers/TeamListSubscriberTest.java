package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.TeamRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
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
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mTeams);
    }

    @Test
    public void testParsedDataNoButton() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setShowTeamInfoButton(false);
        DatafeedTestDriver.getParsedData(mSubscriber, mTeams);

        for (int i = 0; i < mTeams.size(); i++) {
            verify(mRenderer).renderFromModel(mTeams.get(i), false);
        }
    }

    @Test
    public void testParsedDataButton() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setShowTeamInfoButton(true);
        DatafeedTestDriver.getParsedData(mSubscriber, mTeams);

        for (int i = 0; i < mTeams.size(); i++) {
            verify(mRenderer).renderFromModel(mTeams.get(i), true);
        }
    }
}