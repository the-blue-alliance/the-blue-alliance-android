package com.thebluealliance.androidclient.background;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.datafeed.APIRequest;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * Created by phil on 7/7/14.
 */
public class UpdateAPIEndpoints extends AsyncTask<Void, Void, APIResponse.CODE> {

    private RefreshableHostActivity activity;
    private ArrayList<APIRequest> requests;

    public UpdateAPIEndpoints(RefreshableHostActivity activity, ArrayList<APIRequest> requests){
        super();
        this.activity = activity;
        this.requests = requests;
    }

    @Override
    protected APIResponse.CODE doInBackground(Void... params) {
        Log.d(Constants.DATAMANAGER_LOG, "Updating "+requests.size()+" API URLS");
        if(ConnectionDetector.isConnectedToInternet(activity)) {
            APIResponse.CODE result = APIResponse.CODE.CACHED304;
            for(APIRequest request : requests) {
                try {
                    request.setResponse(TBAv2.getResponseFromURLOrThrow(activity, request.getUrl(), true));
                    if(request.getResponse().getCode() == APIResponse.CODE.WEBLOAD || request.getResponse().getCode() == APIResponse.CODE.UPDATED) {
                        storeQuery(request);
                    }
                    result = APIResponse.mergeCodes(result, request.getResponse().getCode());
                } catch (DataManager.NoDataException e) {
                    Log.e(Constants.LOG_TAG, "Unable to fetch: "+request);
                    e.printStackTrace();
                }
            }
            return result;
        }else{
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        activity.notifyRefreshListeners();
    }

    private void storeQuery(APIRequest request){
        if(request == null || request.getResponse() == null || request.getResponse().getData() == null) return;

        //first, drop any database records that will be replaced by this call
        switch (request.getType()){
            case TEAM_LIST:
            case TEAM:
                Database.getInstance(activity).getTeamsTable().delete(request.getSqlWhere(), request.getWhereArgs());
                break;
            case TEAM_EVENTS:
                Database.getInstance(activity).safeDelete(Database.TABLE_EVENTTEAMS, request.getSqlWhere(), request.getWhereArgs());
                break;
            case TEAM_EVENT_AWARDS:
            case EVENT_AWARDS:
                Database.getInstance(activity).safeDelete(Database.TABLE_AWARDS, request.getSqlWhere(), request.getWhereArgs());
                break;
            case TEAM_EVENT_MATCHES:
            case EVENT_MATCHES:
                Database.getInstance(activity).safeDelete(Database.TABLE_MATCHES, request.getSqlWhere(), request.getWhereArgs());
                break;
            case TEAM_YEARS_PARTICIPATED:
                ContentValues yearsValues = new ContentValues();
                yearsValues.put(Database.Teams.YEARS_PARTICIPATED, "");
                Database.getInstance(activity).safeUpdate(Database.TABLE_TEAMS, yearsValues, request.getSqlWhere(), request.getWhereArgs());
                break;
            case TEAM_MEDIA:
                Database.getInstance(activity).safeDelete(Database.TABLE_MEDIAS, request.getSqlWhere(), request.getWhereArgs());
                break;
            case EVENT_LIST:
            case EVENT_INFO:
                Database.getInstance(activity).getEventsTable().delete(request.getSqlWhere(), request.getWhereArgs());
                break;
            case EVENT_TEAMS:
                ContentValues teamValues = new ContentValues();
                teamValues.put(Database.Events.TEAMS, "");
                Database.getInstance(activity).safeUpdate(Database.TABLE_EVENTS, teamValues, request.getSqlWhere(), request.getWhereArgs());
                break;
            case EVENT_STATS:
                ContentValues statsValues = new ContentValues();
                statsValues.put(Database.Events.STATS, "");
                Database.getInstance(activity).safeUpdate(Database.TABLE_EVENTS, statsValues, request.getSqlWhere(), request.getWhereArgs());
                break;
            case EVENT_RANKS:
                ContentValues ranksValues = new ContentValues();
                ranksValues.put(Database.Events.RANKINGS, "");
                Database.getInstance(activity).safeUpdate(Database.TABLE_EVENTS, ranksValues, request.getSqlWhere(), request.getWhereArgs());
                break;
        }

        //now, we can inflate the data to the proper type and store it in the database
        String data = request.getResponse().getData();
        switch (request.getType()){
            case TEAM_LIST:
                ArrayList<Team> teams = TBAv2.getTeamList(data);
                Database.getInstance(activity).getTeamsTable().storeTeams(teams);
                break;
            case TEAM:
                Team team = JSONManager.getGson().fromJson(data, Team.class);
                Database.getInstance(activity).getTeamsTable().add(team);
                break;
            case TEAM_EVENTS:
                String teamKey = request.getWhereArgs()[0]; // Team key is first argument in WHERE clause defined in TBAv2
                EventTeam.store(activity, data, teamKey);
                break;
            case TEAM_EVENT_AWARDS:
            case EVENT_AWARDS:
                ArrayList<Award> awards = TBAv2.getAwardList(data);
                Database.getInstance(activity).getAwardsTable().add(awards);
                break;
            case TEAM_EVENT_MATCHES:
            case EVENT_MATCHES:
                ArrayList<Match> matches = TBAv2.getMatchList(data);
                Database.getInstance(activity).getMatchesTable().add(matches);
                break;
            case TEAM_YEARS_PARTICIPATED:
                ContentValues updatedYears = new ContentValues();
                updatedYears.put(Database.Teams.YEARS_PARTICIPATED, data);
                Database.getInstance(activity).safeUpdate(Database.TABLE_TEAMS, updatedYears, request.getSqlWhere(), request.getWhereArgs());
                break;
            case TEAM_MEDIA:
                ArrayList<Media> medias = TBAv2.getMediaList(data);
                Database.getInstance(activity).getMediasTable().add(medias);
                break;
            case EVENT_LIST:
                ArrayList<Event> events = TBAv2.getEventList(data);
                Database.getInstance(activity).getEventsTable().storeEvents(events);
                break;
            case EVENT_INFO:
                Event event = JSONManager.getGson().fromJson(data, Event.class);
                Database.getInstance(activity).getEventsTable().add(event);
                break;
            case EVENT_TEAMS:
                ContentValues updatedTeams = new ContentValues();
                updatedTeams.put(Database.Events.TEAMS, data);
                Database.getInstance(activity).safeUpdate(Database.TABLE_EVENTS, updatedTeams, request.getSqlWhere(), request.getWhereArgs());
                break;
            case EVENT_STATS:
                ContentValues updatedStats = new ContentValues();
                updatedStats.put(Database.Events.STATS, data);
                Database.getInstance(activity).safeUpdate(Database.TABLE_EVENTS, updatedStats, request.getSqlWhere(), request.getWhereArgs());
                break;
            case EVENT_RANKS:
                ContentValues updatedRanks = new ContentValues();
                updatedRanks.put(Database.Events.RANKINGS, data);
                Database.getInstance(activity).safeUpdate(Database.TABLE_EVENTS, updatedRanks, request.getSqlWhere(), request.getWhereArgs());
                break;
        }
    }
}
