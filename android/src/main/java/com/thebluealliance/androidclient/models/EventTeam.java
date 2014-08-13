package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by phil on 7/1/14.
 */
public class EventTeam extends BasicModel<EventTeam> {

    public EventTeam() {
        super(Database.TABLE_EVENTTEAMS);
    }

    public void setKey(String newKey) {
        if (EventTeamHelper.validateEventTeamKey(newKey)) {
            fields.put(Database.EventTeams.KEY, newKey);
        } else {
            throw new IllegalArgumentException("Invalid EventTeam key: " + newKey);
        }
    }

    public String getKey() throws FieldNotDefinedException {
        if (fields.containsKey(Database.EventTeams.KEY) && fields.get(Database.EventTeams.KEY) instanceof String) {
            return (String) fields.get(Database.EventTeams.KEY);
        } else {
            try {
                String newKey = EventTeamHelper.generateKey(getEventKey(), getTeamKey());
                setKey(newKey);
                return newKey;
            } catch (FieldNotDefinedException e) {
                throw new FieldNotDefinedException("Field Database.Awards.KEY is not defined");
            }
        }
    }

    public void setTeamKey(String teamKey) {
        fields.put(Database.EventTeams.TEAMKEY, teamKey);
    }

    public String getTeamKey() throws FieldNotDefinedException {
        if (fields.containsKey(Database.EventTeams.TEAMKEY) && fields.get(Database.EventTeams.TEAMKEY) instanceof String) {
            return (String) fields.get(Database.EventTeams.TEAMKEY);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.TEAMKEY is not defined");
    }

    public void setEventKey(String eventKey) {
        fields.put(Database.EventTeams.EVENTKEY, eventKey);
    }

    public String getEventKey() throws FieldNotDefinedException {
        if (fields.containsKey(Database.EventTeams.EVENTKEY) && fields.get(Database.EventTeams.EVENTKEY) instanceof String) {
            return (String) fields.get(Database.EventTeams.EVENTKEY);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.EVENTKEY is not defined");
    }

    public void setYear(int year) {
        fields.put(Database.EventTeams.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(Database.EventTeams.YEAR) && fields.get(Database.EventTeams.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.EventTeams.YEAR);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.YEAR is not defined");
    }

    public void setCompWeek(int week) {
        fields.put(Database.EventTeams.COMPWEEK, week);
    }

    public int getCompWeek() throws FieldNotDefinedException {
        if (fields.containsKey(Database.EventTeams.COMPWEEK) && fields.get(Database.EventTeams.COMPWEEK) instanceof Integer) {
            return (Integer) fields.get(Database.EventTeams.COMPWEEK);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.COMPWEEK is not defined");
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getEventTeamsTable().add(this);
    }

    @Override
    public ListElement render() {
        return null;
    }

    public static synchronized APIResponse<ArrayList<EventTeam>> queryList(Context c, boolean forceFromCache, String teamKey, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying eventTeams table: " + whereClause + Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_EVENTTEAMS, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<EventTeam> eventTeams = new ArrayList<>();
        ArrayList<Event> events = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                eventTeams.add(ModelInflater.inflateEventTeam(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }


        APIResponse.CODE code = forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray matchList = JSONManager.getasJsonArray(response.getData());
                eventTeams = new ArrayList<>();
                for (JsonElement m : matchList) {
                    Event e = JSONManager.getGson().fromJson(m, Event.class);
                    events.add(e);
                    try {
                        EventTeam et = EventTeamHelper.fromEvent(teamKey, e);
                        eventTeams.add(et);
                    } catch (FieldNotDefinedException e1) {
                        Log.e(Constants.LOG_TAG, "Couldn't make eventTeam from event");
                    }
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            Database.getInstance(c).getEventTeamsTable().add(eventTeams);
            Database.getInstance(c).getEventsTable().storeEvents(events);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found " + events.size() + " events and, updated in db? " + changed);
        return new APIResponse<>(eventTeams, code);
    }
}
