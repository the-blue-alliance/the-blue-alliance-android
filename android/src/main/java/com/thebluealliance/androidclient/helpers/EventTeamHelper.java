package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;

/**
 * Created by phil on 7/1/14.
 */
public class EventTeamHelper {
    public static EventTeam fromEvent(String teamKey, Event in) throws BasicModel.FieldNotDefinedException {
        EventTeam eventTeam = new EventTeam();
        eventTeam.setEventKey(in.getEventKey());
        eventTeam.setYear(in.getEventYear());
        eventTeam.setCompWeek(in.getCompetitionWeek());
        eventTeam.setTeamKey(teamKey);
        return eventTeam;
    }

    public static String generateKey(String eventKey, String teamKey){
        return eventKey + "_" + teamKey;
    }

    public static boolean validateEventTeamKey(String key){
        String[] split = key.split("_");
        return  split.length == 2 &&
                EventHelper.validateEventKey(split[0]) &&
                TeamHelper.validateTeamKey(split[1]);
    }
}
