package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;

public class EventTeamHelper {
    public static EventTeam fromEvent(String teamKey, Event in) throws BasicModel.FieldNotDefinedException {
        EventTeam eventTeam = new EventTeam();
        eventTeam.setEventKey(in.getKey());
        eventTeam.setYear(in.getEventYear());
        eventTeam.setCompWeek(in.getCompetitionWeek());
        eventTeam.setTeamKey(teamKey);
        eventTeam.setKey(EventTeamHelper.generateKey(in.getKey(), teamKey));
        return eventTeam;
    }

    public static String generateKey(String eventKey, String teamKey) {
        return eventKey + "_" + teamKey;
    }

    public static String getEventKey(String eventTeamKey) {
        return eventTeamKey.split("_")[0];
    }

    public static String getTeamKey(String eventTeamKey) {
        return eventTeamKey.split("_")[1];
    }

    public static boolean validateEventTeamKey(String key) {
        String[] split = key.split("_");
        return split.length == 2 &&
                EventHelper.validateEventKey(split[0]) &&
                TeamHelper.validateTeamKey(split[1]);
    }
}
