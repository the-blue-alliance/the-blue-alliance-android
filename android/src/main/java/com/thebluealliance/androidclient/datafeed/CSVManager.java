package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.models.SimpleTeam;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Nathan on 5/2/2014.
 */
public class CSVManager {

    public static ArrayList<SimpleTeam> parseTeamsFromCSV(String CSV) {
        ArrayList<SimpleTeam> teams = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new StringReader(CSV));
        String line = "";
        String splitBy = ",";
        try {
            while ((line = reader.readLine()) != null) {
                String[] teamParts = line.split(splitBy);
                if (teamParts[1].toLowerCase().equals("none")) {
                    continue;
                }
                String teamKey = "frc" + teamParts[0];
                try {
                    SimpleTeam team = new SimpleTeam(teamKey, Integer.parseInt(teamParts[0]), teamParts[2], teamParts[3], -1);
                    teams.add(team);
                } catch (NumberFormatException e) {
                    // Invalid team number. Probably the column header.
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return teams;
    }
}
