package com.thebluealliance.androidclient.database;

import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventDistrictPointsWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
import com.thebluealliance.androidclient.database.writers.EventRankingsWriter;
import com.thebluealliance.androidclient.database.writers.EventStatsWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamAndTeamListWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamListWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamWriter;
import com.thebluealliance.androidclient.database.writers.EventWriter;
import com.thebluealliance.androidclient.database.writers.MatchListWriter;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.database.writers.MediaListWriter;
import com.thebluealliance.androidclient.database.writers.MediaWriter;
import com.thebluealliance.androidclient.database.writers.TeamListWriter;
import com.thebluealliance.androidclient.database.writers.TeamWriter;
import com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter;

import javax.inject.Inject;

import dagger.Lazy;

public class DatabaseWriter {

    private final Lazy<AwardWriter> awardWriter;
    private final Lazy<AwardListWriter> awardListWriter;
    private final Lazy<DistrictWriter> districtWriter;
    private final Lazy<DistrictListWriter> districtListWriter;
    private final Lazy<DistrictTeamWriter> districtTeamWriter;
    private final Lazy<DistrictTeamListWriter> districtTeamListWriter;
    private final Lazy<EventWriter> eventWriter;
    private final Lazy<EventListWriter> eventListWriter;
    private final Lazy<EventTeamWriter> eventTeamWriter;
    private final Lazy<EventTeamListWriter> eventTeamListWriter;
    private final Lazy<MatchWriter> matchWriter;
    private final Lazy<MatchListWriter> matchListWriter;
    private final Lazy<MediaWriter> mediaWriter;
    private final Lazy<MediaListWriter> mediaListWriter;
    private final Lazy<TeamWriter> teamWriter;
    private final Lazy<TeamListWriter> teamListWriter;
    private final Lazy<YearsParticipatedWriter> yearsParticipatedWriter;
    private final Lazy<EventTeamAndTeamListWriter> eventTeamAndTeamListWriter;
    private final Lazy<EventRankingsWriter> eventRankingsWriter;
    private final Lazy<EventStatsWriter> eventStatsWriter;
    private final Lazy<EventDistrictPointsWriter> eventDistrictPointsWriter;

    @Inject
    public DatabaseWriter(
            Lazy<AwardWriter> award,
            Lazy<AwardListWriter> awardList,
            Lazy<DistrictWriter> district,
            Lazy<DistrictListWriter> districtList,
            Lazy<DistrictTeamWriter> districtTeam,
            Lazy<DistrictTeamListWriter> districtTeamList,
            Lazy<EventWriter> event,
            Lazy<EventListWriter> eventList,
            Lazy<EventTeamWriter> eventTeam,
            Lazy<EventTeamListWriter> eventTeamList,
            Lazy<MatchWriter> match,
            Lazy<MatchListWriter> matchList,
            Lazy<MediaWriter> media,
            Lazy<MediaListWriter> mediaList,
            Lazy<TeamWriter> team,
            Lazy<TeamListWriter> teamList,
            Lazy<YearsParticipatedWriter> yearsParticipated,
            Lazy<EventTeamAndTeamListWriter> eventTeamAndTeamList,
            Lazy<EventRankingsWriter> eventRankings,
            Lazy<EventStatsWriter> eventStats,
            Lazy<EventDistrictPointsWriter> eventDistrictPoints) {
        awardWriter = award;
        awardListWriter = awardList;
        districtWriter = district;
        districtListWriter = districtList;
        districtTeamWriter = districtTeam;
        districtTeamListWriter = districtTeamList;
        eventWriter = event;
        eventListWriter = eventList;
        eventTeamWriter = eventTeam;
        eventTeamListWriter = eventTeamList;
        matchWriter = match;
        matchListWriter = matchList;
        mediaWriter = media;
        mediaListWriter = mediaList;
        teamWriter = team;
        teamListWriter = teamList;
        yearsParticipatedWriter = yearsParticipated;
        eventTeamAndTeamListWriter = eventTeamAndTeamList;
        eventRankingsWriter = eventRankings;
        eventStatsWriter = eventStats;
        eventDistrictPointsWriter = eventDistrictPoints;
    }

    public Lazy<AwardWriter> getAwardWriter() {
        return awardWriter;
    }

    public Lazy<AwardListWriter> getAwardListWriter() {
        return awardListWriter;
    }

    public Lazy<DistrictWriter> getDistrictWriter() {
        return districtWriter;
    }

    public Lazy<DistrictListWriter> getDistrictListWriter() {
        return districtListWriter;
    }

    public Lazy<DistrictTeamWriter> getDistrictTeamWriter() {
        return districtTeamWriter;
    }

    public Lazy<DistrictTeamListWriter> getDistrictTeamListWriter() {
        return districtTeamListWriter;
    }

    public Lazy<EventWriter> getEventWriter() {
        return eventWriter;
    }

    public Lazy<EventListWriter> getEventListWriter() {
        return eventListWriter;
    }

    public Lazy<EventTeamWriter> getEventTeamWriter() {
        return eventTeamWriter;
    }

    public Lazy<EventTeamListWriter> getEventTeamListWriter() {
        return eventTeamListWriter;
    }

    public Lazy<MatchWriter> getMatchWriter() {
        return matchWriter;
    }

    public Lazy<MatchListWriter> getMatchListWriter() {
        return matchListWriter;
    }

    public Lazy<MediaWriter> getMediaWriter() {
        return mediaWriter;
    }

    public Lazy<MediaListWriter> getMediaListWriter() {
        return mediaListWriter;
    }

    public Lazy<TeamWriter> getTeamWriter() {
        return teamWriter;
    }

    public Lazy<TeamListWriter> getTeamListWriter() {
        return teamListWriter;
    }

    public Lazy<YearsParticipatedWriter> getYearsParticipatedWriter() {
        return yearsParticipatedWriter;
    }

    public Lazy<EventTeamAndTeamListWriter> getEventTeamAndTeamListWriter() {
        return eventTeamAndTeamListWriter;
    }

    public Lazy<EventRankingsWriter> getEventRankingsWriter() {
        return eventRankingsWriter;
    }

    public Lazy<EventStatsWriter> getEventStatsWriter() {
        return eventStatsWriter;
    }

    public Lazy<EventDistrictPointsWriter> getEventDistrictPointsWriter() {
        return eventDistrictPointsWriter;
    }
}