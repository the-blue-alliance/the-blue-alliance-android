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
import rx.schedulers.Schedulers;

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
        @Override public void call(Award award) {
            Schedulers.io().createWorker().schedule(() -> mDb.getAwardsTable().add(award));
        }
    }
    class AwardListWriter implements Action1<List<Award>> {
        @Override public void call(List<Award> awards) {
            Schedulers.io().createWorker().schedule(() -> mDb.getAwardsTable().add(awards));
        }
    }
    class DistrictWriter implements Action1<District> {
        @Override public void call(District district) {
           Schedulers.io().createWorker().schedule(() ->  mDb.getDistrictsTable().add(district));
        }
    }
    class DistrictListWriter implements Action1<List<District>> {
        @Override public void call(List<District> districts) {
            Schedulers.io().createWorker().schedule(() -> mDb.getDistrictsTable().add(districts));
        }
    }
    class DistrictTeamWriter implements Action1<DistrictTeam> {
        @Override public void call(DistrictTeam districtTeam) {
            Schedulers.io().createWorker()
              .schedule(() -> mDb.getDistrictTeamsTable().add(districtTeam));
        }
    }
    class DistrictTeamListWriter implements Action1<List<DistrictTeam>> {
        @Override public void call(List<DistrictTeam> districtTeams) {
            Schedulers.io().createWorker()
              .schedule(() -> mDb.getDistrictTeamsTable().add(districtTeams));
        }
    }
    class EventWriter implements Action1<Event> {
        @Override public void call(Event event) {
            Schedulers.io().createWorker().schedule(() -> mDb.getEventsTable().add(event));
        }
    }
    class EventListWriter implements Action1<List<Event>> {
        @Override public void call(List<Event> events) {
            Schedulers.io().createWorker().schedule(() -> mDb.getEventsTable().add(events));
        }
    }
    class EventTeamWriter implements Action1<EventTeam> {
        @Override public void call(EventTeam eventTeam) {
            Schedulers.io().createWorker().schedule(() -> mDb.getEventTeamsTable().add(eventTeam));
        }
    }
    class EventTeamListWriter implements Action1<List<EventTeam>> {
        @Override public void call(List<EventTeam> eventTeams) {
            Schedulers.io().createWorker()
              .schedule(() -> mDb.getEventTeamsTable().add(eventTeams));
        }
    }
    class MatchWriter implements Action1<Match> {
        @Override public void call(Match match) {
            Schedulers.io().createWorker().schedule(() -> mDb.getMatchesTable().add(match));
        }
    }
    class MatchListWriter implements Action1<List<Match>> {
        @Override public void call(List<Match> matches) {
            Schedulers.io().createWorker().schedule(() -> mDb.getMatchesTable().add(matches));
        }
    }
    class MediaWriter implements Action1<Media> {
        @Override public void call(Media media) {
            Schedulers.io().createWorker().schedule(() -> mDb.getMediasTable().add(media));
        }
    }
    class MediaListWriter implements Action1<List<Media>> {
        @Override public void call(List<Media> medias) {
            Schedulers.io().createWorker().schedule(() -> mDb.getMediasTable().add(medias));
        }
    }
    class TeamWriter implements Action1<Team> {
        @Override public void call(Team team) {
            Schedulers.io().createWorker().schedule(() -> mDb.getTeamsTable().add(team));
        }
    }
    class TeamListWriter implements Action1<List<Team>> {
        @Override public void call(List<Team> teams) {
            Schedulers.io().createWorker().schedule(() -> mDb.getTeamsTable().add(teams));
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