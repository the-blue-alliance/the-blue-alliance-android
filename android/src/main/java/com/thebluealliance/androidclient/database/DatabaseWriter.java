package com.thebluealliance.androidclient.database;

import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import rx.functions.Action1;

public class DatabaseWriter {

    private Database mDb;

    private AwardWriter awardWriter;
    private AwardListWriter awardListWriter;
    private DistrictWriter districtWriter;
    private DistrictListWriter districtListWriter;
    private DistrictTeamWriter districtTeamWriter;
    private DistrictTeamListWriter districtTeamListWriter;
    private EventWriter eventWriter;
    private EventListWriter eventListWriter;
    private EventTeamWriter eventTeamWriter;
    private EventTeamListWriter eventTeamListWriter;
    private MatchWriter matchWriter;
    private MatchListWriter matchListWriter;
    private MediaWriter mediaWriter;
    private MediaListWriter mediaListWriter;
    private TeamWriter teamWriter;
    private TeamListWriter teamListWriter;

    public DatabaseWriter(Database db) {
        mDb = db;
    }

    class AwardWriter implements Action1<Award> {
        private AwardWriter() {}
        @Override public void call(Award award) {
            mDb.getAwardsTable().add(award);
        }
    }
    class AwardListWriter implements Action1<List<Award>> {
        private AwardListWriter() {}
        @Override public void call(List<Award> awards) {
            mDb.getAwardsTable().add(awards);
        }
    }
    class DistrictWriter implements Action1<District> {
        private DistrictWriter() {}
        @Override public void call(District district) {
            mDb.getDistrictsTable().add(district);
        }
    }
    class DistrictListWriter implements Action1<List<District>> {
        private DistrictListWriter() {}
        @Override public void call(List<District> districts) {
            mDb.getDistrictsTable().add(districts);
        }
    }
    class DistrictTeamWriter implements Action1<DistrictTeam> {
        private DistrictTeamWriter() {}
        @Override public void call(DistrictTeam districtTeam) {
            mDb.getDistrictTeamsTable().add(districtTeam);
        }
    }
    class DistrictTeamListWriter implements Action1<List<DistrictTeam>> {
        private DistrictTeamListWriter() {}
        @Override public void call(List<DistrictTeam> districtTeams) {
            mDb.getDistrictTeamsTable().add(districtTeams);
        }
    }
    class EventWriter implements Action1<Event> {
        private EventWriter() {}
        @Override public void call(Event event) {
            mDb.getEventsTable().add(event);
        }
    }
    class EventListWriter implements Action1<List<Event>> {
        private EventListWriter() {}
        @Override public void call(List<Event> events) {
            mDb.getEventsTable().add(events);
        }
    }
    class EventTeamWriter implements Action1<EventTeam> {
        private EventTeamWriter() {}
        @Override public void call(EventTeam eventTeam) {
            mDb.getEventTeamsTable().add(eventTeam);
        }
    }
    class EventTeamListWriter implements Action1<List<EventTeam>> {
        private EventTeamListWriter() {}
        @Override public void call(List<EventTeam> eventTeams) {
            mDb.getEventTeamsTable().add(eventTeams);
        }
    }
    class MatchWriter implements Action1<Match> {
        private MatchWriter() {}
        @Override public void call(Match match) {
            mDb.getMatchesTable().add(match);
        }
    }
    class MatchListWriter implements Action1<List<Match>> {
        private MatchListWriter() {}
        @Override public void call(List<Match> matches) {
            mDb.getMatchesTable().add(matches);
        }
    }
    class MediaWriter implements Action1<Media> {
        private MediaWriter() {}
        @Override public void call(Media media) {
            mDb.getMediasTable().add(media);
        }
    }
    class MediaListWriter implements Action1<List<Media>> {
        private MediaListWriter() {}
        @Override public void call(List<Media> medias) {
            mDb.getMediasTable().add(medias);
        }
    }
    class TeamWriter implements Action1<Team> {
        private TeamWriter() {}
        @Override public void call(Team team) {
            mDb.getTeamsTable().add(team);
        }
    }
    class TeamListWriter implements Action1<List<Team>> {
        private TeamListWriter() {}
        @Override public void call(List<Team> teams) {
            mDb.getTeamsTable().add(teams);
        }
    }

    public AwardWriter getAwardWriter() {
        if (awardWriter == null) {
            awardWriter = new AwardWriter();
        }
        return awardWriter;
    }
    public AwardListWriter getAwardListWriter() {
        if (awardListWriter == null) {
            awardListWriter = new AwardListWriter();
        }
        return awardListWriter;
    }
    public DistrictWriter getDistrictWriter() {
        if (districtWriter == null) {
            districtWriter = new DistrictWriter();
        }
        return districtWriter;
    }
    public DistrictListWriter getDistrictListWriter() {
        if (districtListWriter == null) {
            districtListWriter = new DistrictListWriter();
        }
        return districtListWriter;
    }
    public DistrictTeamWriter getDistrictTeamWriter() {
        if (districtTeamWriter == null) {
            districtTeamWriter = new DistrictTeamWriter();
        }
        return districtTeamWriter;
    }
    public DistrictTeamListWriter getDistrictTeamListWriter() {
        if (districtTeamListWriter == null) {
            districtTeamListWriter = new DistrictTeamListWriter();
        }
        return districtTeamListWriter;
    }
    public EventWriter getEventWriter() {
        if (eventWriter == null) {
            eventWriter = new EventWriter();
        }
        return eventWriter;
    }
    public EventListWriter getEventListWriter() {
        if (eventListWriter == null) {
            eventListWriter = new EventListWriter();
        }
        return eventListWriter;
    }
    public EventTeamWriter getEventTeamWriter() {
        if (eventTeamWriter == null) {
            eventTeamWriter = new EventTeamWriter();
        }
        return eventTeamWriter;
    }
    public EventTeamListWriter getEventTeamListWriter() {
        if (eventTeamListWriter == null) {
            eventTeamListWriter = new EventTeamListWriter();
        }
        return eventTeamListWriter;
    }
    public MatchWriter getMatchWriter() {
        if (matchWriter == null) {
            matchWriter = new MatchWriter();
        }
        return matchWriter;
    }
    public MatchListWriter getMatchListWriter() {
        if (matchListWriter == null) {
            matchListWriter = new MatchListWriter();
        }
        return matchListWriter;
    }
    public MediaWriter getMediaWriter() {
        if (mediaWriter == null) {
            mediaWriter = new MediaWriter();
        }
        return mediaWriter;
    }
    public MediaListWriter getMediaListWriter() {
        if (mediaListWriter == null) {
            mediaListWriter = new MediaListWriter();
        }
        return mediaListWriter;
    }
    public TeamWriter getTeamWriter() {
        if (teamWriter == null) {
            teamWriter = new TeamWriter();
        }
        return teamWriter;
    }
    public TeamListWriter getTeamListWriter() {
        if (teamListWriter == null) {
            teamListWriter = new TeamListWriter();
        }
        return teamListWriter;
    }
}