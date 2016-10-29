package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventTeamAndTeamListWriter extends BaseDbWriter<EventTeamAndTeamListWriter.EventTeamAndTeam> {

    private final EventTeamListWriter mEventTeamListWriter;
    private final TeamListWriter mTeamListWriter;

    @Inject
    public EventTeamAndTeamListWriter(Database db, EventTeamListWriter etWriter, TeamListWriter teamWriter) {
        super(db);
        mEventTeamListWriter = etWriter;
        mTeamListWriter = teamWriter;
    }

    @Override
    public void write(EventTeamAndTeam newModels, Long lastModified) {
        if (newModels == null) {
            return;
        }
        mDb.getWritableDatabase().beginTransaction();
        try {
            mEventTeamListWriter.write(newModels.eventTeams, lastModified);
            mTeamListWriter.write(newModels.teams, lastModified);
            mDb.getWritableDatabase().setTransactionSuccessful();
        } finally {
            mDb.getWritableDatabase().endTransaction();
        }
    }

    public static class EventTeamAndTeam {
        public final List<EventTeam> eventTeams;
        public final List<Team> teams;

        public EventTeamAndTeam(List<EventTeam> eventTeams, List<Team> teams) {
            this.eventTeams = eventTeams;
            this.teams = teams;
        }
    }
}
