package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datatypes.AwardListElement;

import java.util.ArrayList;
import java.util.Iterator;

public class Award implements BasicModel {

    String eventKey, name;
    int year;
    JsonArray winners;

    public Award(){
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

    public JsonArray getWinners(){
        return winners;
    }

    public void setWinners(JsonArray winners){
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

    public ArrayList<AwardListElement> renderAll(){
        ArrayList<AwardListElement> output = new ArrayList<>();
        Iterator<JsonElement> iterator = winners.iterator();
        int teamNumber;
        String awardee;
        while(iterator.hasNext()){
            JsonObject winner = iterator.next().getAsJsonObject();
            if(winner.get("team_number").isJsonNull()){
                teamNumber = -1;
            }else{
                teamNumber = winner.get("team_number").getAsInt();
            }
            if(winner.get("awardee").isJsonNull()){
                awardee = "";
            }else{
                awardee = winner.get("awardee").getAsString();
            }

            output.add(new AwardListElement(eventKey+"_"+name,name,buildWinnerString(awardee,teamNumber),teamNumber));
        }
        return output;
    }

    public static String buildWinnerString(String awardee, int team){
        if(awardee.isEmpty()){
            return ""+team;
        }else if(team == -1){
            return awardee;
        }else{
            return awardee + " ("+team+")";
        }
    }

    @Override
    public AwardListElement render() {
        return null;
    }

    @Override
    public ContentValues getParams() {
        return null;
    }
}
