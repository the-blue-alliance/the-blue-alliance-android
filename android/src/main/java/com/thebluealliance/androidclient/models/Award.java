package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.listitems.AwardListElement;

import java.util.ArrayList;

public class Award implements BasicModel {

    String eventKey, name;
    int year;
    JsonArray winners;

    public Award() {
        this.eventKey = "";
        this.name = "";
        this.year = -1;
        this.winners = new JsonArray();
    }

    public Award(String eventKey, String name, int year, JsonArray winners) {
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

    @Override
    public AwardListElement render() {
        return new AwardListElement(name, winners);
    }

    @Override
    public ContentValues getParams() {
        return null;
    }
}
