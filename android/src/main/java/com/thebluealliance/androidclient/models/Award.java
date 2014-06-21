package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.listitems.AwardListElement;

import java.util.ArrayList;
import java.util.Iterator;

public class Award extends BasicModel<Award> {

    String eventKey, name;
    int year;
    JsonArray winners;

    public Award() {
        super(Database.TABLE_AWARDS);
        this.eventKey = "";
        this.name = "";
        this.year = -1;
        this.winners = new JsonArray();
    }

    public Award(String eventKey, String name, int year, JsonArray winners) {
        super(Database.TABLE_AWARDS);
        this.eventKey = eventKey;
        this.name = name;
        this.year = year;
        this.winners = winners;
    }

    public JsonArray getWinners() {
        return winners;
    }

    public void setWinners(JsonArray winners) {
        this.winners = winners;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public ArrayList<Award> splitByWinner() {
        ArrayList<Award> out = new ArrayList<>();
        for (JsonElement winner : winners) {
            JsonArray winnerArray = new JsonArray();
            winnerArray.add(winner);
            out.add(new Award(eventKey, name, year, winnerArray));
        }
        return out;
    }

    public ArrayList<AwardListElement> renderAll() {
        ArrayList<AwardListElement> output = new ArrayList<>();
        Iterator<JsonElement> iterator = winners.iterator();
        String teamNumber;
        String awardee;
        while (iterator.hasNext()) {
            JsonObject winner = iterator.next().getAsJsonObject();
            if (winner.get("team_number").isJsonNull()) {
                teamNumber = "";
            } else {
                teamNumber = winner.get("team_number").getAsString();
            }
            if (winner.get("awardee").isJsonNull()) {
                awardee = "";
            } else {
                awardee = winner.get("awardee").getAsString();
            }

            output.add(new AwardListElement("frc" + teamNumber, name, buildWinnerString(awardee, teamNumber), teamNumber));
        }
        return output;
    }

    public static String buildWinnerString(String awardee, String team) {
        if (awardee.isEmpty()) {
            return "" + team;
        } else if (team.isEmpty()) {
            return awardee;
        } else {
            return awardee + " (" + team + ")";
        }
    }

    @Override
    public void addFields(String... fields) {

    }

    @Override
    public AwardListElement render() {
        Iterator<JsonElement> iterator = winners.iterator();
        String teamNumber = "";
        String awardee = "";
        while (iterator.hasNext()) {
            JsonObject winner = iterator.next().getAsJsonObject();
            if (winner.get("team_number").isJsonNull()) {
                teamNumber = "";
            } else {
                teamNumber += winner.get("team_number").getAsInt() + ", ";
            }
            if (winner.get("awardee").isJsonNull()) {
                awardee = "";
            } else {
                awardee += winner.get("awardee").getAsString() + ", ";
            }
        }
        if (!teamNumber.isEmpty()) {
            teamNumber = teamNumber.substring(0, teamNumber.length() - 2);
        }
        if (!awardee.isEmpty()) {
            awardee = awardee.substring(0, awardee.length() - 2);
        }
        return new AwardListElement("frc" + teamNumber, name, buildWinnerString(awardee, teamNumber), teamNumber);
    }

    @Override
    public ContentValues getParams() {
        return null;
    }
}
