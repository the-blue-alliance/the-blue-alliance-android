package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.VisibleForTesting;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.deserializers.APIStatusDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AllianceDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AllianceDeserializer.AllianceBackupDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer.AwardRecipientDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer.DistrictEventPointsDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchAllianceDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchVideoDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingItemDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingItemDeserializer.RecordDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingsResponseDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamAtEventStatusDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamAtEventStatusDeserializer.Playoff;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
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
import com.thebluealliance.androidclient.models.TeamAtEventStatus.TeamAtEventPlayoff;
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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static com.thebluealliance.androidclient.datafeed.deserializers.RankingsResponseDeserializer.RankingSortOrderDeserializer;

/**
 * Dagger module that handles OkHttp and Gson
 */
@InstallIn(SingletonComponent.class)
@Module(includes = {TBAAndroidModule.class})
public class HttpModule {

    public static int CACHE_SIZE = 10 * 1024 * 1024;

    public HttpModule() {}

    @Provides @Singleton
    public APIv3RequestInterceptor provideApiRequestInterceptor(SharedPreferences prefs) {
        return new APIv3RequestInterceptor(prefs);
    }

    @Provides @Singleton
    public OkHttpClient getOkHttp(Cache responseCache, APIv3RequestInterceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        if (Utilities.isDebuggable()) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        builder.cache(responseCache);
        return builder.build();
    }

    @Provides @Singleton
    public Cache provideOkCache(@ApplicationContext Context context) {
        return new Cache(context.getCacheDir(), CACHE_SIZE);
    }
}
