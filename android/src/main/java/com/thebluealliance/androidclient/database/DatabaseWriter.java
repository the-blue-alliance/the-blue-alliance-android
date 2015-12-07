package com.thebluealliance.androidclient.database;

import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
import com.thebluealliance.androidclient.database.writers.EventRankingsWriter;
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

    public Lazy<AwardWriter> awardWriter;
    public Lazy<AwardListWriter> awardListWriter;
    public Lazy<DistrictWriter> districtWriter;
    public Lazy<DistrictListWriter> districtListWriter;
    public Lazy<DistrictTeamWriter> districtTeamWriter;
    public Lazy<DistrictTeamListWriter> districtTeamListWriter;
    public Lazy<EventWriter> eventWriter;
    public Lazy<EventListWriter> eventListWriter;
    public Lazy<EventTeamWriter> eventTeamWriter;
    public Lazy<EventTeamListWriter> eventTeamListWriter;
    public Lazy<MatchWriter> matchWriter;
    public Lazy<MatchListWriter> matchListWriter;
    public Lazy<MediaWriter> mediaWriter;
    public Lazy<MediaListWriter> mediaListWriter;
    public Lazy<TeamWriter> teamWriter;
    public Lazy<TeamListWriter> teamListWriter;
    public Lazy<YearsParticipatedWriter> yearsParticipatedWriter;
    public Lazy<EventTeamAndTeamListWriter> eventTeamAndTeamListWriter;
    public Lazy<EventRankingsWriter> eventRankingsWriter;

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
      Lazy<EventRankingsWriter> eventRankings) {
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
    }
}