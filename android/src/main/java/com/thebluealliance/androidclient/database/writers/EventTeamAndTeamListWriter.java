package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
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
    public EventTeamAndTeamListWriter(Database db, BriteDatabase briteDb, EventTeamListWriter etWriter, TeamListWriter teamWriter) {
        super(db, briteDb);
        mEventTeamListWriter = etWriter;
        mTeamListWriter = teamWriter;
    }

    @Override
    public void write(EventTeamAndTeam newModels) {
        if (newModels == null) {
            return;
        }
        BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
        try {
            mEventTeamListWriter.write(newModels.eventTeams);
            mTeamListWriter.write(newModels.teams);
            transaction.markSuccessful();
        } finally {
            transaction.end();
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
