package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.RankingListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class RankingsListSubscriberTest {

    @Mock Database mDb;
    @Mock EventBus mEventBus;

    RankingsListRecyclerSubscriber mSubscriber;
    JsonArray mRankings;
    // Includes a team with a number like "####B"
    JsonArray mRankingsMultiTeam;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockTeamsTable(mDb);

        mSubscriber = new RankingsListRecyclerSubscriber(mDb, mEventBus);
        mRankings = ModelMaker.getModel(JsonArray.class, "2015necmp_rankings");
        mRankingsMultiTeam = ModelMaker.getModel(JsonArray.class, "2015ohri_rankings");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testJsonNull() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseJsonNull(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mRankings);
        verify(mEventBus).post(any(EventRankingsEvent.class));
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mRankings);
        EventHelper.CaseInsensitiveMap<String> rankingElements = new EventHelper.CaseInsensitiveMap<>();
        for (int j = 2; j < mRankings.get(0).getAsJsonArray().size(); j++) {
            rankingElements.put(
              mRankings.get(0).getAsJsonArray().get(j).getAsString(),
              mRankings.get(1).getAsJsonArray().get(j).getAsString());
        }
        String breakdown = EventHelper.createRankingBreakdown(rankingElements);
        TeamRankingViewModel expected = new TeamRankingViewModel("frc1519", "Team 1519", "1519", 1, "", breakdown);

        assertEquals(1, data.size());
        assertEquals(expected, data.get(0));
    }

    @Test
    public void testParsedDataWithMultiTeam() throws BasicModel.FieldNotDefinedException {
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mRankingsMultiTeam);
        EventHelper.CaseInsensitiveMap<String> rankingElements = new EventHelper.CaseInsensitiveMap<>();
        for (int j = 2; j < mRankingsMultiTeam.get(0).getAsJsonArray().size(); j++) {
            rankingElements.put(
                    mRankingsMultiTeam.get(0).getAsJsonArray().get(j).getAsString(),
                    mRankingsMultiTeam.get(1).getAsJsonArray().get(j).getAsString());
        }
        String breakdown = EventHelper.createRankingBreakdown(rankingElements);
        TeamRankingViewModel expected = new TeamRankingViewModel("frc1038B", "Team 1038B", "1038B", 30, "", breakdown);

        assertEquals(1, data.size());
        assertEquals(expected, data.get(0));
    }
}