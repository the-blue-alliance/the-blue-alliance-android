package com.thebluealliance.androidclient.background.firstlaunch;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.hilt.work.HiltWorker;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.api.ApiConstants;
import com.thebluealliance.androidclient.api.call.TbaApiV3;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
import com.thebluealliance.androidclient.database.writers.TeamListWriter;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictKeys;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.ApiStatus;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.inject.Named;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

@HiltWorker
public class LoadTBADataWorker extends Worker {

    private static final String WORK_TAG = "load_initial_data";
    public static final String DATA_TO_LOAD = "data_to_load";
    public static final short LOAD_TEAMS = 0,
            LOAD_EVENTS = 1,
            LOAD_DISTRICTS = 2;

    private final Context mApplicationContext;
    private final TbaApiV3 mDatafeed;
    private final AppConfig mAppConfig;
    private final Database mDb;
    private final TeamListWriter mTeamWriter;
    private final EventListWriter mEventWriter;
    private final DistrictListWriter mDistrictWriter;
    private final SharedPreferences mSharedPreferences;

    private long mStartTime;

    @AssistedInject
    public LoadTBADataWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters params,
            @Named("tba_apiv3_call") TbaApiV3 datafeed,
            AppConfig appConfig,
            Database db,
            TeamListWriter teamListWriter,
            EventListWriter eventListWriter,
            DistrictListWriter districtListWriter,
            SharedPreferences sharedPreferences) {
        super(context, params);
        mApplicationContext = context.getApplicationContext();
        mDatafeed = datafeed;
        mAppConfig = appConfig;
        mDb = db;
        mTeamWriter = teamListWriter;
        mEventWriter = eventListWriter;
        mDistrictWriter = districtListWriter;
        mSharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public Result doWork() {
        mStartTime = System.currentTimeMillis();
        int[] params = getInputData().getIntArray(DATA_TO_LOAD);
        int[] dataToLoad;
        if (params == null || params.length == 0) {
            dataToLoad = new int[]{LOAD_TEAMS,
                    LOAD_EVENTS,
                    LOAD_DISTRICTS};
        } else {
            dataToLoad = params;
        }

        /* We need to download and cache every team and event into the database. To avoid
         * unexpected behavior caused by changes in network connectivity, we will load all
         * teams into memory first. Once we have loaded everything, only then will we wipe the
         * database and insert all the new teams and events
         */

        try {
            /* First, do a blocking update of Remote Config */
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, mApplicationContext.getString(R.string.loading_config)));
            mAppConfig.updateRemoteDataBlocking();

            Call<ApiStatus> statusCall = mDatafeed.fetchApiStatus();
            Response<ApiStatus> statusResponse = statusCall.execute();
            if (!statusResponse.isSuccessful() || statusResponse.body() == null || statusResponse.body().getMaxSeason() == null) {
                TbaLogger.e("Bad API status response: " + statusResponse);
                return onConnectionError();
            }

            int maxCompYear = statusResponse.body().getMaxSeason();
            List<Team> allTeams = new ArrayList<>();
            int maxPageNum = 0;
            if (Arrays.binarySearch(dataToLoad, LOAD_TEAMS) != -1) {
                mDb.getTeamsTable().deleteAllRows();
                // First we will load all the teams
                for (int pageNum = 0; pageNum < 20; pageNum++) {  // limit to 20 pages to prevent potential infinite loop
                    if (isStopped()) {
                        return Result.failure();
                    }
                    int start = pageNum * Constants.API_TEAM_LIST_PAGE_SIZE;
                    int end = start + Constants.API_TEAM_LIST_PAGE_SIZE - 1;
                    start = start == 0 ? 1 : start;
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, mApplicationContext.getString(R.string.loading_teams, start, end)));
                    Call<List<Team>> teamListCall =
                            mDatafeed.fetchTeamPage(pageNum, ApiConstants.TBA_CACHE_WEB);
                    Response<List<Team>> teamListResponse = teamListCall.execute();
                    if (!teamListResponse.isSuccessful()) {
                        return onConnectionError();
                    }
                    if (teamListResponse.body() == null || teamListResponse.body().isEmpty()) {
                        // No teams found for a page; we are done
                        break;
                    }
                    Date lastModified = teamListResponse.headers().getDate("Last-Modified");
                    List<Team> responseBody = teamListResponse.body();
                    if (lastModified != null) {
                        long lastModifiedTimestamp = lastModified.getTime();
                        for (int i = 0; i < responseBody.size(); i++) {
                            responseBody.get(i).setLastModified(lastModifiedTimestamp);
                        }
                    }
                    allTeams.addAll(responseBody);
                    maxPageNum = Math.max(maxPageNum, pageNum);
                }
            }

            List<Event> allEvents = new ArrayList<>();
            if (Arrays.binarySearch(dataToLoad, LOAD_EVENTS) != -1) {
                mDb.getEventsTable().deleteAllRows();
                // Now we load all events
                for (int year = Constants.FIRST_COMP_YEAR; year <= maxCompYear; year++) {
                    if (isStopped()) {
                        return Result.failure();
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, mApplicationContext.getString(R.string.loading_events, Integer.toString(year))));
                    Call<List<Event>> eventListCall =
                            mDatafeed.fetchEventsInYear(year, ApiConstants.TBA_CACHE_WEB);
                    Response<List<Event>> eventListResponse = eventListCall.execute();
                    if (!eventListResponse.isSuccessful()) {
                        return onConnectionError();
                    }
                    if (eventListResponse.body() == null) {
                        continue;
                    }
                    Date lastModified = eventListResponse.headers().getDate("Last-Modified");
                    List<Event> responseBody = eventListResponse.body();
                    if (lastModified != null) {
                        long lastModifiedTimestamp = lastModified.getTime();
                        for (int i = 0; i < responseBody.size(); i++) {
                            responseBody.get(i).setLastModified(lastModifiedTimestamp);
                        }
                    }
                    allEvents.addAll(responseBody);
                    TbaLogger.i(String.format("Loaded %1$d events in %2$d",
                            eventListResponse.body().size(), year));
                }
            }

            List<District> allDistricts = new ArrayList<>();
            if (Arrays.binarySearch(dataToLoad, LOAD_DISTRICTS) != -1) {
                mDb.getDistrictsTable().deleteAllRows();
                //load all districts
                for (int year = Constants.FIRST_DISTRICT_YEAR; year <= maxCompYear; year++) {
                    if (isStopped()) {
                        return Result.failure();
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, mApplicationContext.getString(R.string.loading_districts, year)));
                    AddDistrictKeys keyAdder = new AddDistrictKeys(year);
                    Call<List<District>> districtListCall =
                            mDatafeed.fetchDistrictList(year, ApiConstants.TBA_CACHE_WEB);
                    Response<List<District>> districtListResponse = districtListCall.execute();
                    if (!districtListResponse.isSuccessful()
                            || districtListResponse.body() == null) {
                        return onConnectionError();
                    }

                    List<District> newDistrictList = districtListResponse.body();
                    keyAdder.call(newDistrictList);
                    Date lastModified = districtListResponse.headers().getDate("Last-Modified");
                    if (lastModified != null) {
                        long lastModifiedTimestamp = lastModified.getTime();
                        for (int i = 0; i < newDistrictList.size(); i++) {
                            newDistrictList.get(i).setLastModified(lastModifiedTimestamp);
                        }
                    }
                    allDistricts.addAll(newDistrictList);
                    TbaLogger.i(String.format("Loaded %1$d districts in %2$d",
                            newDistrictList.size(), year));
                }
            }

            if (isStopped()) {
                return Result.failure();
            }
            // If no exception has been thrown at this point, we have all the data. We can now
            // insert it into the database. Pass a 0 as the last-modified time here, because we set
            // it individually above
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, mApplicationContext.getString(R.string.loading_almost_finished)));

            TbaLogger.i("Writing " + allTeams.size() + " teams");
            mTeamWriter.write(allTeams, 0L);
            TbaLogger.i("Writing " + allEvents.size() + " events");
            mEventWriter.write(allEvents, 0L);
            TbaLogger.i("Writing " + allDistricts.size() + " districts");
            mDistrictWriter.write(allDistricts, 0L);

            SharedPreferences.Editor editor = mSharedPreferences.edit();

            // Write TBA Status
            editor.putString(TBAStatusController.STATUS_PREF_KEY, statusResponse.body().getJsonBlob());
            editor.putInt(Constants.LAST_YEAR_KEY, statusResponse.body().getMaxSeason());

            // Loop through all pages
            for (int pageNum = 0; pageNum <= maxPageNum; pageNum++) {
                editor.putBoolean(Database.ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, true);
            }
            // Loop through all years
            for (int year = Constants.FIRST_COMP_YEAR; year <= maxCompYear; year++) {
                editor.putBoolean(Database.ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true);
            }
            // Loop through years for districts
            for (int year = Constants.FIRST_DISTRICT_YEAR; year <= maxCompYear; year++) {
                editor.putBoolean(Database.ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR + year, true);
            }
            editor.putInt(Constants.APP_VERSION_KEY, BuildConfig.VERSION_CODE);
            editor.apply();

            AnalyticsHelper.sendTimingUpdate(mApplicationContext, System.currentTimeMillis() - mStartTime, "load all data", "");
            return Result.success(new LoadProgressInfo(LoadProgressInfo.STATE_FINISHED, mApplicationContext.getString(R.string.loading_finished)).toData());
        } catch (RuntimeException ex) {
            // This is bad, probably an error in the response from the server
            TbaLogger.e("Error loading initial data", ex);
            return onFailure(new LoadProgressInfo(LoadProgressInfo.STATE_ERROR, Utilities.exceptionStacktraceToString(ex)));
        } catch (IOException | InterruptedException | ExecutionException e) {
            /* Some sort of network error */
            TbaLogger.e("Error loading initial data", e);
            return onConnectionError();
        }
    }

    private Result onConnectionError() {
        LoadProgressInfo progress = new LoadProgressInfo(LoadProgressInfo.STATE_NO_CONNECTION, mApplicationContext.getString(R.string.connection_lost));
        return Result.failure(progress.toData());
    }

    private Result onFailure(LoadProgressInfo progress) {
        return Result.failure(progress.toData());
    }

    private void publishProgress(LoadProgressInfo progress) {
        setProgressAsync(progress.toData());
    }

    public interface LoadTBADataCallbacks {
        void onProgressUpdate(LoadProgressInfo info);
    }

    public static UUID runWithCallbacks(AppCompatActivity activity, int[] dataToLoad, LoadTBADataCallbacks callback) {
        OneTimeWorkRequest downloadRequest =
                new OneTimeWorkRequest.Builder(LoadTBADataWorker.class)
                    .setInputData(new Data.Builder().putIntArray(DATA_TO_LOAD, dataToLoad).build())
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build();

        WorkManager workManager = WorkManager.getInstance(activity);
        workManager.enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, downloadRequest);
        subscribeToJob(activity, downloadRequest.getId(), callback);
        return downloadRequest.getId();
    }

    public static void subscribeToJob(AppCompatActivity activity, UUID jobId, LoadTBADataCallbacks callback) {
        WorkManager workManager = WorkManager.getInstance(activity);
        workManager.getWorkInfoByIdLiveData(jobId).observe(activity, info -> {
            TbaLogger.d("Data load info: " + info);
            if (info == null) {
                return;
            }

            @Nullable LoadProgressInfo progress = null;
            if (info.getState().isFinished()) {
                Data progressData = info.getOutputData();
                progress = LoadProgressInfo.fromData(progressData);
            } else if (info.getState() == WorkInfo.State.RUNNING){
                Data progressData = info.getProgress();
                progress = LoadProgressInfo.fromData(progressData);
            }

            if (info.getState() == WorkInfo.State.FAILED && progress != null && progress.state == -1) {
                // If we had some other framework-level exception, add an error message so we
                // don't just spin forever
                TbaLogger.e("Failure loading TBA data!");
                progress = new LoadProgressInfo(LoadProgressInfo.STATE_ERROR, "Error loading TBA data!");
            }

            if (progress == null) {
                TbaLogger.d("Unable to get load data progress! " + info);
                return;
            }

            TbaLogger.d("Data load progress: " + progress);
            callback.onProgressUpdate(progress);
        });
    }

    public static void cancel(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(WORK_TAG);
    }

    public static class LoadProgressInfo {

        public static final int STATE_LOADING = 0;
        public static final int STATE_FINISHED = 1;
        public static final int STATE_NO_CONNECTION = 2;
        public static final int STATE_ERROR = 3;

        public int state = -1;
        public String message = "";

        public LoadProgressInfo(int state, String message) {
            this.state = state;
            this.message = message;
        }

        @Override
        public String toString() {
            return "LoadProgressInfo{"
                    + "state=" + state
                    + ", message='" + message + '\''
                    + '}';
        }

        public static LoadProgressInfo fromData(Data data) {
            return new LoadProgressInfo(
                    data.getInt("state", -1),
                    data.getString("message")
            );
        }

        public Data toData() {
            return new Data.Builder()
                    .putInt("state", this.state)
                    .putString("message", this.message)
                    .build();
        }
    }
}