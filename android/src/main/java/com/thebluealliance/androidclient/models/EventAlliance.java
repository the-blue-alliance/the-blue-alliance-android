package com.thebluealliance.androidclient.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.types.EventDetailType;
import com.thebluealliance.api.model.IAllianceBackup;
import com.thebluealliance.api.model.IEventAlliance;
import com.thebluealliance.api.model.ITeamAtEventPlayoff;

import java.util.List;

import javax.annotation.Nullable;

public class EventAlliance implements IEventAlliance {

    private List<String> picks;
    private @Nullable String eventKey;
    private @Nullable List<String> declines;
    private @Nullable String name;
    private @Nullable IAllianceBackup backup;
    private @Nullable Long lastModified;
    private @Nullable ITeamAtEventPlayoff status;

    @Override @Nullable public String getName() {
        return name;
    }

    @Override public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override public List<String> getPicks() {
        return picks;
    }

    @Override public void setPicks(List<String> picks) {
        this.picks = picks;
    }

    @Override @Nullable public List<String> getDeclines() {
        return declines;
    }

    @Override public void setDeclines(@Nullable List<String> declines) {
        this.declines = declines;
    }

    @Override @Nullable
    public IAllianceBackup getBackup() {
        return backup;
    }

    @Override
    public void setBackup(@Nullable IAllianceBackup backup) {
        this.backup = backup;
    }

    @Override @Nullable public ITeamAtEventPlayoff getStatus() {
        return status;
    }

    @Override public void setStatus(@Nullable ITeamAtEventPlayoff status) {
        this.status = status;
    }

    public @Nullable String getEventKey() {
        return eventKey;
    }

    public void setEventKey(@Nullable String eventKey) {
        this.eventKey = eventKey;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }

    public static EventDetail toEventDetail(List<EventAlliance> alliances,
                                            String eventKey,
                                            Gson gson) {
        if (alliances == null) return null;
        JsonArray allianceArray = new JsonArray();
        for (int i = 0; i < alliances.size(); i++) {
            EventAlliance alliance = alliances.get(i);
            allianceArray.add(gson.toJsonTree(alliance, EventAlliance.class));
        }

        EventDetail eventDetail = new EventDetail(eventKey, EventDetailType.ALLIANCES);
        eventDetail.setJsonData(allianceArray.toString());
        return eventDetail;
    }

    public static class AllianceBackup implements IAllianceBackup {
        private String in;
        private String out;

        @Override public String getIn() {
            return in;
        }

        @Override public void setIn(String in) {
            this.in = in;
        }

        @Override public String getOut() {
            return out;
        }

        @Override public void setOut(String out) {
            this.out = out;
        }
    }
}
