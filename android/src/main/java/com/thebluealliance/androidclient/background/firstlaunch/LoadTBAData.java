package com.thebluealliance.androidclient.background.firstlaunch;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;
import rx.schedulers.Schedulers;

public class LoadTBAData extends AsyncTask<Short, LoadTBAData.LoadProgressInfo, Void> {

    public static final String DATA_TO_LOAD = "data_to_load";
    public static final short LOAD_TEAMS = 0,
            LOAD_EVENTS = 1,
            LOAD_DISTRICTS = 2;

    private TbaApiV3 datafeed;
    private AppConfig config;
    private LoadTBADataCallbacks callbacks;
    private Context context;
    private long startTime;
    private Database mDb;
    private TeamListWriter mTeamWriter;
    private EventListWriter mEventWriter;
    private DistrictListWriter mDistrictWriter;

    public LoadTBAData(TbaApiV3 datafeed, AppConfig config, LoadTBADataCallbacks callbacks, Context c,
                       Database db, TeamListWriter teamWriter, EventListWriter eventWriter, DistrictListWriter districtWriter) {
        this.datafeed = datafeed;
        this.config = config;
        this.callbacks = callbacks;
        this.context = c.getApplicationContext();
        this.startTime = System.currentTimeMillis();
        this.mDb = db;
        this.mTeamWriter = teamWriter;
        this.mEventWriter = eventWriter;
        this.mDistrictWriter = districtWriter;
    }

    @Override
    protected Void doInBackground(Short... params) {
        if (callbacks == null) {
            throw new IllegalArgumentException("callbacks must not be null!");
        }

        TbaLogger.d("Input: " + Arrays.deepToString(params));

        Short[] dataToLoad;
        if (params == null) {
            dataToLoad = new Short[]{LOAD_TEAMS,
                    LOAD_EVENTS,
                    LOAD_DISTRICTS};
        } else {
            dataToLoad = params;
        }

        TbaLogger.d("Loading: " + Arrays.deepToString(dataToLoad));

        /* We need to download and cache every team and event into the database. To avoid
         * unexpected behavior caused by changes in network connectivity, we will load all
         * teams into memory first. Once we have loaded everything, only then will we wipe the
         * database and insert all the new teams and events
         */

        try {
            /* First, do a blocking update of Remote Config */
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, context.getString(R.string.loading_config)));
            config.updateRemoteDataBlocking();

            Call<ApiStatus> statusCall = datafeed.fetchApiStatus();
            Response<ApiStatus> statusResponse = statusCall.execute();
            if (!statusResponse.isSuccessful() || statusResponse.body() == null) {
                onConnectionError();
                return null;
            }
            int maxCompYear = statusResponse.body().getMaxSeason();


            List<Team> allTeams = new ArrayList<>();
            int maxPageNum = 0;
            if (Arrays.binarySearch(dataToLoad, LOAD_TEAMS) != -1) {
                mDb.getTeamsTable().deleteAllRows();
                // First we will load all the teams
                for (int pageNum = 0; pageNum < 20; pageNum++) {  // limit to 20 pages to prevent potential infinite loop
                    if (isCancelled()) {
                        return null;
                    }
                    int start = pageNum * Constants.API_TEAM_LIST_PAGE_SIZE;
                    int end = start + Constants.API_TEAM_LIST_PAGE_SIZE - 1;
                    start = start == 0 ? 1 : start;
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_teams), start, end)));
                    Call<List<Team>> teamListCall =
                            datafeed.fetchTeamPage(pageNum, ApiConstants.TBA_CACHE_WEB);
                    Response<List<Team>> teamListResponse = teamListCall.execute();
                    if (teamListResponse == null || !teamListResponse.isSuccessful()) {
                        onConnectionError();
                        return null;
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
                    if (isCancelled()) {
                        return null;
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_events), Integer.toString(year))));
                    Call<List<Event>> eventListCall =
                            datafeed.fetchEventsInYear(year, ApiConstants.TBA_CACHE_WEB);
                    Response<List<Event>> eventListResponse = eventListCall.execute();
                    if (eventListResponse == null
                            || !eventListResponse.isSuccessful()) {
                        onConnectionError();
                        return null;
                    }
                    if (eventListResponse.body() == null) continue;
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
                    if (isCancelled()) {
                        return null;
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_districts), year)));
                    AddDistrictKeys keyAdder = new AddDistrictKeys(year);
                    Call<List<District>> districtListCall =
                            datafeed.fetchDistrictList(year, ApiConstants.TBA_CACHE_WEB);
                    Response<List<District>> districtListResponse = districtListCall.execute();
                    if (districtListResponse == null
                            || !districtListResponse.isSuccessful()
                            || districtListResponse.body() == null) {
                        onConnectionError();
                        return null;
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

            if (isCancelled()) {
                return null;
            }
            // If no exception has been thrown at this point, we have all the data. We can now
            // insert it into the database. Pass a 0 as the last-modified time here, because we set
            // it individually above
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, context.getString(R.string.loading_almost_finished)));

            TbaLogger.i("Writing " + allTeams.size() + " teams");
            Schedulers.io().createWorker().schedule(() -> mTeamWriter.write(allTeams, 0L));
            TbaLogger.i("Writing " + allEvents.size() + " events");
            Schedulers.io().createWorker().schedule(() -> mEventWriter.write(allEvents, 0L));
            TbaLogger.i("Writing " + allDistricts.size() + " districts");
            Schedulers.io().createWorker().schedule(() -> mDistrictWriter.write(allDistricts, 0L));

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

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
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_FINISHED, context.getString(R.string.loading_finished)));
        } catch (RuntimeException ex) {
            // This is bad, probably an error in the response from the server
            ex.printStackTrace();
            // Alert the user that there was a problem
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_ERROR, Utilities.exceptionStacktraceToString(ex)));
        } catch (IOException | InterruptedException e) {
            /* Some sort of network error */
            e.printStackTrace();
            onConnectionError();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (context != null) {
            AnalyticsHelper.sendTimingUpdate(context, System.currentTimeMillis() - startTime, "load all data", "");
        }
    }

    private void onConnectionError() {
        publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_NO_CONNECTION, context.getString(R.string.connection_lost)));
    }

    @Override
    protected void onProgressUpdate(LoadProgressInfo... values) {
        callbacks.onProgressUpdate(values[0]);
    }

    public class LoadProgressInfo {

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

    }

    public interface LoadTBADataCallbacks {
        void onProgressUpdate(LoadProgressInfo info);
    }
}