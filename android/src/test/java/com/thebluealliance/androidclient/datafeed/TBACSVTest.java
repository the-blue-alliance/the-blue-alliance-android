package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * File created by phil on 5/8/14.
 */
@RunWith(RobolectricTestRunner.class)
public class TBACSVTest {

    @org.junit.Test
    public void testCSVParse(){
        String csvRow = "1124,\"UTC Fire and Security & Avon High School\",\"ÜberBots\",\"Avon, CT, USA\",\"http://www.uberbots.org\"";
        Team team = CSVManager.parseTeamsFromCSV(csvRow).get(0);

        try {
            assertEquals((int)team.getTeamNumber(), 1124);
            assertEquals(team.getNickname(), "ÜberBots");
            assertEquals(team.getLocation(), "Avon, CT, USA");
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get team fields");
            e.printStackTrace();
        }
    }

}
