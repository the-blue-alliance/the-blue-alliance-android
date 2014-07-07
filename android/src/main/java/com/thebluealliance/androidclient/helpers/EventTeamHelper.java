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
}
