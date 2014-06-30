package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.TeamListElement;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

public class Team extends BasicModel<Team> {

    public Team() {
        super(Database.TABLE_TEAMS);
    }

    public Team(String teamKey, int teamNumber, String nickname, String location) {
        this();
        setTeamKey(teamKey);
        setTeamNumber(teamNumber);
        setNickname(nickname);
        setLocation(location);
    }

    public String getFullName() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.NAME) && fields.get(Database.Teams.NAME) instanceof String) {
            return (String) fields.get(Database.Teams.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Teams.NAME is not defined");
    }

    public void setFullName(String fullName) {
        fields.put(Database.Teams.NAME, fullName);
    }

    public JsonArray getEvents() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.EVENTS) && fields.get(Database.Teams.EVENTS) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Teams.EVENTS));
        }
        throw new FieldNotDefinedException("Field Database.Teams.EVENTS is not defined");
    }

    public void setEvents(JsonArray events) {
        fields.put(Database.Teams.EVENTS, events.toString());
    }

    public String getWebsite() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.WEBSITE) && fields.get(Database.Teams.WEBSITE) instanceof String) {
            return (String) fields.get(Database.Teams.WEBSITE);
        }
        throw new FieldNotDefinedException("Field Database.Teams.WEBSITE is not defined");
    }

    public void setWebsite(String website) {
        fields.put(Database.Teams.WEBSITE, website);
    }

    public String getTeamKey() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.KEY) && fields.get(Database.Teams.KEY) instanceof String) {
            return (String) fields.get(Database.Teams.KEY);
        }
        throw new FieldNotDefinedException("Field Database.Teams.KEY is not defined");
    }

    public void setTeamKey(String teamKey) {
        fields.put(Database.Teams.KEY, teamKey);
    }

    public String getNickname() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.SHORTNAME) && fields.get(Database.Teams.SHORTNAME) instanceof String) {
            return (String) fields.get(Database.Teams.SHORTNAME);
        }
        throw new FieldNotDefinedException("Field Database.Teams.SHORTNAME is not defined");
    }

    public void setNickname(String nickname) {
        fields.put(Database.Teams.SHORTNAME, nickname);
    }

    public String getLocation() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.LOCATION) && fields.get(Database.Teams.LOCATION) instanceof String) {
            return (String) fields.get(Database.Teams.LOCATION);
        }
        throw new FieldNotDefinedException("Field Database.Teams.LOCATION is not defined");
    }

    public void setLocation(String location) {
        fields.put(Database.Teams.LOCATION, location);
    }

    public Integer getTeamNumber() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.NUMBER) && fields.get(Database.Teams.NUMBER) instanceof Integer) {
            return (Integer) fields.get(Database.Teams.NUMBER);
        }
        throw new FieldNotDefinedException("Field Database.Teams.NUMBER is not defined");
    }

    public void setTeamNumber(int teamNumber) {
        fields.put(Database.Teams.NUMBER, teamNumber);
    }

    public void setYearsParticipated(JsonArray years){
        fields.put(Database.Teams.YEARS_PARTICIPATED, years.toString());
    }

    public JsonArray getYearsParticipated() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Teams.YEARS_PARTICIPATED) && fields.get(Database.Teams.YEARS_PARTICIPATED) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Teams.YEARS_PARTICIPATED));
        }
        throw new FieldNotDefinedException("Field Database.Teams.YEARS_PARTICIPATED is not defined");
    }

    public Event getCurrentEvent() {
        try {
            Date now = new Date(), eventStart, eventEnd;
            Iterator<JsonElement> iterator = getEvents().iterator();
            JsonObject e;
            while (iterator.hasNext()) {
                try {
                    e = iterator.next().getAsJsonObject();
                    eventStart = EventHelper.eventDateFormat.parse(e.get("start_date").getAsString());
                    eventEnd = EventHelper.eventDateFormat.parse(e.get("end_date").getAsString());
                    if (now.after(eventStart) && now.before(eventEnd)) {
                        return JSONManager.getGson().fromJson(e, Event.class);
                    }
                } catch (ParseException ex) {
                    //can't parse the date. Give up.
                }
            }
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for determining current event\n" +
                    "Required: Database.Teams.EVENTS");
        }
        return null;
    }

    public String getSearchTitles() {
        try {
            return getTeamKey() + "," + getNickname() + "," + getTeamNumber();
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for creating search titles\n" +
                    "Required: Database.Teams.KEY, Database.Teams.SHORTNAME, Database.Teams.NUMBER");
            return null;
        }
    }

    @Override
    public TeamListElement render() {
        try {
            return new TeamListElement(getTeamKey(), getTeamNumber(), getNickname(), getLocation());
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
                    "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }

    public TeamListElement render(boolean showTeamInfoButton) {
        try {
            return new TeamListElement(getTeamKey(), getTeamNumber(), getNickname(), getLocation(), showTeamInfoButton);
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
                    "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }

    @Override
    public void merge(Team in) {
        //we need to merge the events the team competed in.
        //since the incoming data takes precedence over current data,
        //we're adding events that don't already exist into the new data in there
        JsonArray newEvents;
        try {
            newEvents = in.getEvents();
        } catch (FieldNotDefinedException e) {
            newEvents = new JsonArray();
        }

        JsonArray currentEvents;
        try {
            currentEvents = getEvents();
        } catch (FieldNotDefinedException e) {
            currentEvents = new JsonArray();
        }

        String newString = newEvents.toString();
        for(JsonElement event: currentEvents){
            if(!newString.contains(event.getAsString())){
                newEvents.add(event.getAsJsonPrimitive());
            }
        }

        super.merge(in);
    }

    public static APIResponse<Team> query(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_TEAMS, fields, whereClause, whereArgs, null, null, null, null);
        Team team;
        if(cursor != null && cursor.moveToFirst()){
            team = ModelInflater.inflateTeam(cursor);
        }else{
            team = new Team();
        }

        APIResponse.CODE code = APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Team updatedTeam = JSONManager.getGson().fromJson(response.getData(), Team.class);
                team.merge(updatedTeam);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            team.write(c);
        }
        return new APIResponse<>(team, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getTeamsTable().add(this);
    }
}
