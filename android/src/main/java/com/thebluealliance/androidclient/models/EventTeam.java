package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.ModelType;

public class EventTeam extends BasicModel<EventTeam> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.ALLIANCE_SELECTION,
            //NotificationTypes.AWARDS,
            //NotificationTypes.FINAL_RESULTS
    };

    public EventTeam() {
        super(Database.TABLE_EVENTTEAMS, ModelType.EVENTTEAM);
    }

    public void setKey(String newKey) {
        if (EventTeamHelper.validateEventTeamKey(newKey)) {
            fields.put(EventTeamsTable.KEY, newKey);
        } else {
            throw new IllegalArgumentException("Invalid EventTeam key: " + newKey);
        }
    }

    public String getKey() {
        if (fields.containsKey(EventTeamsTable.KEY) && fields.get(EventTeamsTable.KEY) instanceof String) {
            return (String) fields.get(EventTeamsTable.KEY);
        } else {
            try {
                String newKey = EventTeamHelper.generateKey(getEventKey(), getTeamKey());
                setKey(newKey);
                return newKey;
            } catch (FieldNotDefinedException e) {
                return "";
            }
        }
    }

    public void setTeamKey(String teamKey) {
        fields.put(EventTeamsTable.TEAMKEY, teamKey);
    }

    public String getTeamKey() throws FieldNotDefinedException {
        if (fields.containsKey(EventTeamsTable.TEAMKEY) && fields.get(EventTeamsTable.TEAMKEY) instanceof String) {
            return (String) fields.get(EventTeamsTable.TEAMKEY);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.TEAMKEY is not defined");
    }

    public void setEventKey(String eventKey) {
        fields.put(EventTeamsTable.EVENTKEY, eventKey);
    }

    public String getEventKey() throws FieldNotDefinedException {
        if (fields.containsKey(EventTeamsTable.EVENTKEY) && fields.get(EventTeamsTable.EVENTKEY) instanceof String) {
            return (String) fields.get(EventTeamsTable.EVENTKEY);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.EVENTKEY is not defined");
    }

    public void setYear(int year) {
        fields.put(EventTeamsTable.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(EventTeamsTable.YEAR) && fields.get(EventTeamsTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(EventTeamsTable.YEAR);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.YEAR is not defined");
    }

    public void setCompWeek(int week) {
        fields.put(EventTeamsTable.COMPWEEK, week);
    }

    public int getCompWeek() throws FieldNotDefinedException {
        if (fields.containsKey(EventTeamsTable.COMPWEEK) && fields.get(EventTeamsTable.COMPWEEK) instanceof Integer) {
            return (Integer) fields.get(EventTeamsTable.COMPWEEK);
        }
        throw new FieldNotDefinedException("Field Database.EventTeams.COMPWEEK is not defined");
    }

}
