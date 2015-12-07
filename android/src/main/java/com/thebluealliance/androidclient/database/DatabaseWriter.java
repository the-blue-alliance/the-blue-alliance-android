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

    public final Lazy<AwardWriter> awardWriter;
    public final Lazy<AwardListWriter> awardListWriter;
    public final Lazy<DistrictWriter> districtWriter;
    public final Lazy<DistrictListWriter> districtListWriter;
    public final Lazy<DistrictTeamWriter> districtTeamWriter;
    public final Lazy<DistrictTeamListWriter> districtTeamListWriter;
    public final Lazy<EventWriter> eventWriter;
    public final Lazy<EventListWriter> eventListWriter;
    public final Lazy<EventTeamWriter> eventTeamWriter;
    public final Lazy<EventTeamListWriter> eventTeamListWriter;
    public final Lazy<MatchWriter> matchWriter;
    public final Lazy<MatchListWriter> matchListWriter;
    public final Lazy<MediaWriter> mediaWriter;
    public final Lazy<MediaListWriter> mediaListWriter;
    public final Lazy<TeamWriter> teamWriter;
    public final Lazy<TeamListWriter> teamListWriter;
    public final Lazy<YearsParticipatedWriter> yearsParticipatedWriter;
    public final Lazy<EventTeamAndTeamListWriter> eventTeamAndTeamListWriter;
    public final Lazy<EventRankingsWriter> eventRankingsWriter;
    public final Lazy<EventStatsWriter> eventStatsWriter;
    public final Lazy<EventDistrictPointsWriter> eventDistrictPointsWriter;

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
}