package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.helpers.AwardHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.types.ModelType;

public class Award extends BasicModel<Award> {

    private JsonArray winners;

    public Award() {
        super(Database.TABLE_AWARDS, ModelType.AWARD);
        winners = null;
    }

    public Award(String eventKey, String name, int year, JsonArray winners) {
        this();
        setEventKey(eventKey);
        setName(name);
        setYear(year);
        setWinners(winners);
    }

    public void setKey(String key) {
        if (AwardHelper.validateAwardKey(key)) {
            fields.put(AwardsTable.KEY, key);
        } else {
            throw new IllegalArgumentException("Invalid award key: " + key);
        }
    }

    public String getKey() {
        if (fields.containsKey(AwardsTable.KEY) && fields.get(AwardsTable.KEY) instanceof String) {
            return (String) fields.get(AwardsTable.KEY);
        } else {
            try {
                String newKey = AwardHelper.createAwardKey(getEventKey(), getEnum());
                setKey(newKey);
                return newKey;
            } catch (FieldNotDefinedException e) {
                return "";
            }
        }
    }

    public void generateKey() {

    }

    public int getEnum() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.ENUM) && fields.get(AwardsTable.ENUM) instanceof Integer) {
            return (Integer) fields.get(AwardsTable.ENUM);
        }
        throw new FieldNotDefinedException("Field Database.Awards.ENUM is not defined");
    }

    public void setEnum(int awardEnum) {
        fields.put(AwardsTable.ENUM, awardEnum);
    }

    public JsonArray getWinners() throws FieldNotDefinedException {
        if (winners != null) {
            return winners;
        }
        if (fields.containsKey(AwardsTable.WINNERS) && fields.get(AwardsTable.WINNERS) instanceof String) {
            winners = JSONHelper.getasJsonArray((String) fields.get(AwardsTable.WINNERS));
            return winners;
        }
        throw new FieldNotDefinedException("Field Database.Awards.WINNERS is not defined");
    }

    public void setWinners(JsonArray winners) {
        fields.put(AwardsTable.WINNERS, winners.toString());
        this.winners = winners;
    }

    public void setWinners(String winnersJson) {
        fields.put(AwardsTable.WINNERS, winnersJson);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.YEAR) && fields.get(AwardsTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(AwardsTable.YEAR);
        }
        throw new FieldNotDefinedException("Field Database.Awards.YEAR is not defined");
    }

    public void setYear(int year) {
        fields.put(AwardsTable.YEAR, year);
    }

    public String getName() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.NAME) && fields.get(AwardsTable.NAME) instanceof String) {
            return (String) fields.get(AwardsTable.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Awards.NAME is not defined");
    }

    public void setName(String name) {
        fields.put(AwardsTable.NAME, name);
    }

    public String getEventKey() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.EVENTKEY) && fields.get(AwardsTable.EVENTKEY) instanceof String) {
            return (String) fields.get(AwardsTable.EVENTKEY);
        }
        throw new FieldNotDefinedException("Field Database.Awards.EVENTKEY is not defined");
    }

    public void setEventKey(String eventKey) {
        fields.put(AwardsTable.EVENTKEY, eventKey);
    }

}
