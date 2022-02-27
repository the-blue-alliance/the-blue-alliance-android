package com.thebluealliance.androidclient.di;

import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.config.LocalProperties;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventDetailWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
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
import com.thebluealliance.androidclient.datafeed.deserializers.APIStatusDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AllianceDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchAllianceDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchVideoDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingItemDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingsResponseDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamAtEventStatusDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.models.ApiStatus;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.MatchAlliancesContainer;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.RankingSortOrder;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.api.model.IAllianceBackup;
import com.thebluealliance.api.model.IApiStatus;
import com.thebluealliance.api.model.IAward;
import com.thebluealliance.api.model.IAwardRecipient;
import com.thebluealliance.api.model.IDistrict;
import com.thebluealliance.api.model.IDistrictEventPoints;
import com.thebluealliance.api.model.IDistrictRanking;
import com.thebluealliance.api.model.IEvent;
import com.thebluealliance.api.model.IEventAlliance;
import com.thebluealliance.api.model.IMatch;
import com.thebluealliance.api.model.IMatchAlliancesContainer;
import com.thebluealliance.api.model.IMatchVideo;
import com.thebluealliance.api.model.IMedia;
import com.thebluealliance.api.model.IRankingResponseObject;
import com.thebluealliance.api.model.IRankingSortOrder;
import com.thebluealliance.api.model.ITeam;
import com.thebluealliance.api.model.ITeamAtEventPlayoff;
import com.thebluealliance.api.model.ITeamAtEventStatus;
import com.thebluealliance.api.model.ITeamRecord;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;

/**
 * App-wide dependency injection items
 */
@InstallIn(SingletonComponent.class)
@Module
public class TBAAndroidModule {

    private static Gson sGson;

    public TBAAndroidModule() {
    }

    @Provides
    public Resources provideApplicationResources(@ApplicationContext Context context) {
        return context.getResources();
    }

    @Provides @Singleton
    public Gson provideGson() {
        return getGson();
    }

    @Provides
    @Singleton
    public Database provideDatabase(@ApplicationContext Context context, Gson gson) {
        return Database.getInstance(context, gson);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(@ApplicationContext Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @Singleton
    public Tracker provideAndroidTracker(@ApplicationContext Context context) {
        return Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, context);
    }

    @Provides
    public NotificationManager provideNotificationManager(@ApplicationContext Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    public AccountManager provideAccountManager(@ApplicationContext Context context) {
        return AccountManager.get(context);
    }

    @Provides
    public LocalProperties provideLocalProperties(@ApplicationContext Context context) {
        return new LocalProperties(context);
    }

    @Provides
    @Singleton
    public DatabaseWriter provideDatabaseWriter(
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
            Lazy<EventDetailWriter> eventDetail) {
        return new DatabaseWriter(award, awardList, district, districtList, districtTeam,
          districtTeamList, event, eventList, eventTeam, eventTeamList, match, matchList, media,
          mediaList, team, teamList, yearsParticipated, eventTeamAndTeamList, eventDetail);
    }


    @VisibleForTesting
    public static Gson getGson() {
        if (sGson != null) return sGson;
        GsonBuilder builder = new GsonBuilder();
        AwardDeserializer awardDeserializer = new AwardDeserializer();
        EventDeserializer eventDeserializer = new EventDeserializer();
        MatchDeserializer matchDeserializer = new MatchDeserializer();
        TeamDeserializer teamDeserializer = new TeamDeserializer();
        MediaDeserializer mediaDeserializer = new MediaDeserializer();
        DistrictDeserializer districtDeserializer = new DistrictDeserializer();
        APIStatusDeserializer apiStatusDeserializer = new APIStatusDeserializer();
        RankingsResponseDeserializer rankingsResponseDeserializer = new RankingsResponseDeserializer();
        RankingsResponseDeserializer.RankingSortOrderDeserializer sortOrderDeserializer = new RankingsResponseDeserializer.RankingSortOrderDeserializer();
        AllianceDeserializer allianceDeserializer = new AllianceDeserializer();
        AllianceDeserializer.AllianceBackupDeserializer backupDeserializer = new AllianceDeserializer.AllianceBackupDeserializer();
        MatchAllianceDeserializer matchAllianceDeserializer = new MatchAllianceDeserializer();
        MatchVideoDeserializer matchVideoDeserializer = new MatchVideoDeserializer();
        AwardDeserializer.AwardRecipientDeserializer recipientDeserializer = new AwardDeserializer.AwardRecipientDeserializer();
        DistrictTeamDeserializer districtTeamDeserializer = new DistrictTeamDeserializer();
        DistrictTeamDeserializer.DistrictEventPointsDeserializer eventPointsDeserializer = new DistrictTeamDeserializer.DistrictEventPointsDeserializer();
        RankingItemDeserializer.RecordDeserializer recordDeserializer = new RankingItemDeserializer.RecordDeserializer();
        TeamAtEventStatusDeserializer teamAtEventStatusDeserializer = new TeamAtEventStatusDeserializer();
        TeamAtEventStatusDeserializer.Playoff playoffStatusDeserializer = new TeamAtEventStatusDeserializer.Playoff();

        builder.registerTypeAdapter(IAward.class, awardDeserializer);
        builder.registerTypeAdapter(Award.class, awardDeserializer);
        builder.registerTypeAdapter(IAwardRecipient.class, recipientDeserializer);
        builder.registerTypeAdapter(Award.AwardRecipient.class, recipientDeserializer);

        builder.registerTypeAdapter(IEvent.class, eventDeserializer);
        builder.registerTypeAdapter(Event.class, eventDeserializer);

        builder.registerTypeAdapter(IMatch.class, matchDeserializer);
        builder.registerTypeAdapter(Match.class, matchDeserializer);

        builder.registerTypeAdapter(ITeam.class, teamDeserializer);
        builder.registerTypeAdapter(Team.class, teamDeserializer);

        builder.registerTypeAdapter(IMedia.class, mediaDeserializer);
        builder.registerTypeAdapter(Media.class, mediaDeserializer);

        builder.registerTypeAdapter(IApiStatus.class, apiStatusDeserializer);
        builder.registerTypeAdapter(ApiStatus.class, apiStatusDeserializer);

        builder.registerTypeAdapter(IRankingResponseObject.class, rankingsResponseDeserializer);
        builder.registerTypeAdapter(RankingResponseObject.class, rankingsResponseDeserializer);
        builder.registerTypeAdapter(RankingItem.class, new RankingItemDeserializer());
        builder.registerTypeAdapter(RankingItem.TeamRecord.class, recordDeserializer);
        builder.registerTypeAdapter(ITeamRecord.class, recordDeserializer);
        builder.registerTypeAdapter(IRankingSortOrder.class, sortOrderDeserializer);
        builder.registerTypeAdapter(RankingSortOrder.class, sortOrderDeserializer);

        builder.registerTypeAdapter(IEventAlliance.class, allianceDeserializer);
        builder.registerTypeAdapter(EventAlliance.class, allianceDeserializer);

        builder.registerTypeAdapter(IMatchAlliancesContainer.class, matchAllianceDeserializer);
        builder.registerTypeAdapter(MatchAlliancesContainer.class, matchAllianceDeserializer);
        builder.registerTypeAdapter(IAllianceBackup.class, backupDeserializer);
        builder.registerTypeAdapter(EventAlliance.AllianceBackup.class, backupDeserializer);

        builder.registerTypeAdapter(IMatchVideo.class, matchVideoDeserializer);
        builder.registerTypeAdapter(Match.MatchVideo.class, matchVideoDeserializer);

        builder.registerTypeAdapter(District.class, districtDeserializer);
        builder.registerTypeAdapter(IDistrict.class, districtDeserializer);
        builder.registerTypeAdapter(IDistrictRanking.class, districtTeamDeserializer);
        builder.registerTypeAdapter(DistrictRanking.class, districtTeamDeserializer);
        builder.registerTypeAdapter(IDistrictEventPoints.class, eventPointsDeserializer);
        builder.registerTypeAdapter(DistrictPointBreakdown.class, eventPointsDeserializer);

        builder.registerTypeAdapter(ITeamAtEventStatus.class, teamAtEventStatusDeserializer);
        builder.registerTypeAdapter(TeamAtEventStatus.class, teamAtEventStatusDeserializer);
        builder.registerTypeAdapter(ITeamAtEventPlayoff.class, playoffStatusDeserializer);
        builder.registerTypeAdapter(TeamAtEventStatus.TeamAtEventPlayoff.class, playoffStatusDeserializer);

        sGson = builder.create();
        return sGson;
    }
}
