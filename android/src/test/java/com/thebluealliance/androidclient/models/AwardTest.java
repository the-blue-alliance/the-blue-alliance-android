package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AwardTest {
    private Award mTeamAward;
    private Award mIndividualAward;

    @Before
    public void readJsonData(){
        mIndividualAward = ModelMaker.getModel(Award.class, "award_individual");
        mTeamAward = ModelMaker.getModel(Award.class, "award_team");
    }

    @Test
    public void testTeamAward()  {
        assertNotNull(mTeamAward);
        assertEquals(mTeamAward.getEventKey(), "2015cthar");
        assertNotNull(mTeamAward.getEnum());
        assertEquals(mTeamAward.getEnum().intValue(), 17);
        assertEquals(mTeamAward.getName(), "Quality Award sponsored by Motorola");
        assertEquals(mTeamAward.getYear().intValue(), 2015);

        JsonArray recipientList = mTeamAward.getWinners();
        assertNotNull(recipientList);
        assertEquals(recipientList.size(), 1);
        assertTrue(recipientList.get(0).isJsonObject());

        JsonObject recipient = recipientList.get(0).getAsJsonObject();
        assertEquals(recipient.get("team_number").getAsInt(), 195);
        assertTrue(recipient.get("awardee").isJsonNull());
    }

    @Test
    public void testIndividualAward()  {
        assertNotNull(mIndividualAward);
        assertEquals(mIndividualAward.getEventKey(), "2015necmp");
        assertNotNull(mIndividualAward.getEnum());
        assertEquals(mIndividualAward.getEnum().intValue(), 5);
        assertEquals(mIndividualAward.getName(), "Volunteer of the Year");
        assertEquals(mIndividualAward.getYear().intValue(), 2015);

        JsonArray recipientList = mIndividualAward.getWinners();
        assertNotNull(recipientList);
        assertEquals(recipientList.size(), 1);
        assertTrue(recipientList.get(0).isJsonObject());

        JsonObject recipient = recipientList.get(0).getAsJsonObject();
        assertEquals(recipient.get("team_number").getAsInt(), 319);
        assertEquals(recipient.get("awardee").getAsString(), "Ty Tremblay");
    }
}
