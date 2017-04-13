package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.content.Context;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(DefaultTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamInfoSubscriberTest {

    @Mock AppConfig mAppConfig;

    private TeamInfoSubscriber mSubscriber;
    private TeamInfoSubscriber.Model mModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Context context = RuntimeEnvironment.application;
        mSubscriber = new TeamInfoSubscriber(context, mAppConfig);
        Team team = ModelMaker.getModel(Team.class, "frc1124");
        List<Media> socialMedia = ModelMaker.getModelList(Media.class, "frc1124_social_media");
        mModel = new TeamInfoSubscriber.Model(team, socialMedia);
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mModel);
    }

    @Test
    public void testParsedData()  {
        TeamInfoBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mModel);

        assertNotNull(data);
        assertEquals(mModel.team.getKey(), data.teamKey);
        assertEquals(mModel.team.getName(), data.fullName);
        assertEquals(mModel.team.getNickname(), data.nickname);
        assertEquals(mModel.team.getLocation(), data.location);
        assertEquals(mModel.team.getWebsite(), data.website);
        assertEquals(mModel.socialMedia.size(), 4);
    }
}