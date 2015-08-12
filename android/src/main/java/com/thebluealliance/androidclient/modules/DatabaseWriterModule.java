package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = TBAAndroidModule.class)
public class DatabaseWriterModule {

    @Provides @Singleton
    public AwardListWriter awardListWriter(Database db) {
        return new AwardListWriter(db);
    }

    @Provides @Singleton
    public AwardWriter awardWriter(Database db) {
        return new AwardWriter(db);
    }

    @Provides @Singleton
    public DistrictListWriter districtListWriter(Database db) {
        return new DistrictListWriter(db);
    }

    @Provides @Singleton
    public DistrictTeamListWriter districtTeamListWriter(Database db) {
        return new DistrictTeamListWriter(db);
    }

    @Provides @Singleton
    public DistrictTeamWriter districtTeamWriter(Database db) {
        return new DistrictTeamWriter(db);
    }

    @Provides @Singleton
    public DistrictWriter districtWriter(Database db) {
        return new DistrictWriter(db);
    }

    @Provides @Singleton
    public EventListWriter eventListWriter(Database db) {
        return new EventListWriter(db);
    }

    @Provides @Singleton
    public EventTeamListWriter eventTeamListWriter(Database db) {
        return new EventTeamListWriter(db);
    }

    @Provides @Singleton
    public EventTeamWriter eventTeamWriter(Database db) {
        return new EventTeamWriter(db);
    }

    @Provides @Singleton
    public EventWriter eventWriter(Database db) {
        return new EventWriter(db);
    }

    @Provides @Singleton
    public MatchListWriter matchListWriter(Database db) {
        return new MatchListWriter(db);
    }

    @Provides @Singleton
    public MatchWriter matchWriter(Database db) {
        return new MatchWriter(db);
    }

    @Provides @Singleton
    public MediaListWriter mediaListWriter(Database db) {
        return new MediaListWriter(db);
    }

    @Provides @Singleton
    public MediaWriter mediaWriter(Database db) {
        return new MediaWriter(db);
    }

    @Provides @Singleton
    public TeamListWriter teamListWriter(Database db) {
        return new TeamListWriter(db);
    }

    @Provides @Singleton
    public TeamWriter teamWriter(Database db) {
        return new TeamWriter(db);
    }

    @Provides @Singleton
    public YearsParticipatedWriter yearsParticipatedWriter(Database db) {
        return new YearsParticipatedWriter(db);
    }
}
