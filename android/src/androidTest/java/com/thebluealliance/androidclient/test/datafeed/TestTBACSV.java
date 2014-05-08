package com.thebluealliance.androidclient.test.datafeed;

import android.test.suitebuilder.annotation.MediumTest;

import com.thebluealliance.androidclient.datafeed.CSVManager;
import com.thebluealliance.androidclient.models.SimpleTeam;

import junit.framework.TestCase;

/**
 * File created by phil on 5/8/14.
 */
public class TestTBACSV extends TestCase{

    @MediumTest
    public void testCSVParse(){
        String csvRow = "1124,\"UTC Fire and Security & Avon High School\",\"ÜberBots\",\"Avon, CT, USA\",\"http://www.uberbots.org\"";
        SimpleTeam team = CSVManager.parseTeamsFromCSV(csvRow).get(0);

        assertEquals((int)team.getTeamNumber(), 1124);
        assertEquals(team.getNickname(), "ÜberBots");
        assertEquals(team.getLocation(), "Avon, CT, USA");
    }

}
