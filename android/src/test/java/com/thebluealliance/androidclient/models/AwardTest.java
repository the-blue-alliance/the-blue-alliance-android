package com.thebluealliance.androidclient.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.helpers.JSONHelper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Ignore
public class AwardTest {
    Award mTeamAward;
    Award mIndividualAward;

    @Before
    public void readJsonData(){
        BufferedReader individualReader;
        BufferedReader teamReader;
        Gson gson = JSONHelper.getGson();
        String basePath = new File("").getAbsolutePath();
        try {
            individualReader = new BufferedReader(
                new FileReader(basePath +  "/android/src/test/java/com/thebluealliance/" +
                    "androidclient/test/models/award_individual.json"));
            teamReader = new BufferedReader(
                new FileReader(basePath +  "/android/src/test/java/com/thebluealliance/" +
                    "androidclient/test/models/award_team.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        mIndividualAward = gson.fromJson(individualReader, Award.class);
        mTeamAward = gson.fromJson(teamReader, Award.class);
    }

    @Test
    public void testTeamAward() throws BasicModel.FieldNotDefinedException {
        assertNotNull(mTeamAward);
        assertEquals(mTeamAward.getEventKey(), "2015cthar");
        assertEquals(mTeamAward.getEnum(), 17);
        assertEquals(mTeamAward.getName(), "Quality Award sponsored by Motorola");
        assertEquals(mTeamAward.getYear(), 2015);

        JsonArray recipientList = mTeamAward.getWinners();
        assertEquals(recipientList.size(), 1);
        assertTrue(recipientList.get(0).isJsonObject());

        JsonObject recipient = recipientList.get(0).getAsJsonObject();
        assertEquals(recipient.get("team_number").getAsInt(), 195);
        assertTrue(recipient.get("awardee").isJsonNull());
    }

    @Test
    public void testIndividualAward() throws BasicModel.FieldNotDefinedException {
        assertNotNull(mIndividualAward);
        assertEquals(mIndividualAward.getEventKey(), "2015necmp");
        assertEquals(mIndividualAward.getEnum(), 5);
        assertEquals(mIndividualAward.getName(), "Volunteer of the Year");
        assertEquals(mIndividualAward.getYear(), 2015);

        JsonArray recipientList = mIndividualAward.getWinners();
        assertEquals(recipientList.size(), 1);
        assertTrue(recipientList.get(0).isJsonObject());

        JsonObject recipient = recipientList.get(0).getAsJsonObject();
        assertEquals(recipient.get("team_number").getAsInt(), 319);
        assertEquals(recipient.get("awardee").getAsString(), "Ty Tremblay");
    }
}
