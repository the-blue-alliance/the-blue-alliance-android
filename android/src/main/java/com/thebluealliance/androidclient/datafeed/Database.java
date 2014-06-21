package com.thebluealliance.androidclient.datafeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.interfaces.ModelTable;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Semaphore;


/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    private Context context;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",
            TABLE_API = "api",
            TABLE_TEAMS = "teams",
            TABLE_EVENTS = "events",
            TABLE_AWARDS = "awards",
            TABLE_MATCHES = "matches",
            TABLE_MEDIAS = "medias",
            TABLE_SEARCH = "search",
            TABLE_SEARCH_TEAMS = "search_teams",
            TABLE_SEARCH_EVENTS = "search_events";

    String CREATE_API = "CREATE TABLE IF NOT EXISTS " + TABLE_API + "("
            + Response.URL + " TEXT PRIMARY KEY, "
            + Response.LASTUPDATE + " TIMESTAMP, "
            + Response.LASTHIT + " TIMESTAMP "
            + ")";
    String CREATE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + "("
            + Teams.KEY + " TEXT PRIMARY KEY, "
            + Teams.NUMBER + " INTEGER NOT NULL, "
            + Teams.NAME + " TEXT, "
            + Teams.SHORTNAME + " TEXT, "
            + Teams.LOCATION + " TEXT,"
            + Teams.WEBSITE + " TEXT, "
            + Teams.EVENTS + " TEXT, "
            + ")";
    String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
            + Events.KEY + " TEXT PRIMARY KEY, "
            + Events.YEAR + " INTEGER NOT NULL, "
            + Events.NAME + " TEXT, "
            + Events.LOCATION + " TEXT, "
            + Events.VENUE + " TEXT, "
            + Events.TYPE + " INTEGER, "
            + Events.DISTRICT + " INTEGER, "
            + Events.DISTRICT_STRING + " TEXT, "
            + Events.START + " TIMESTAMP, "
            + Events.END + " TIMESTAMP, "
            + Events.OFFICIAL + " INTEGER, "
            + Events.WEEK + " INTEGER, "
            + Events.RANKINGS + " STRING, "
            + Events.ALLIANCES + " STRING, "
            + Events.STATS + " STRING, "
            + ")";
    String CREATE_AWARDS = "CREATE TABLE IF NOT EXISTS " + TABLE_AWARDS + "("
            + Awards.EVENTKEY + " TEXT, "
            + Awards.NAME + " TEXT, "
            + Awards.YEAR + " INTEGER, "
            + Awards.WINNERS + " TEXT"
            + ")";
    String CREATE_MATCHES = "CREATE TABLE IF NOT EXISTS " + TABLE_MATCHES + "("
            + Matches.KEY + " TEXT PRIMARY KEY, "
            + Matches.EVENT + " TEXT, "
            + Matches.TIMESTRING + " TEXT, "
            + Matches.TIME + " TIMESTAMP, "
            + Matches.ALLIANCES + " TEXT, "
            + Matches.VIDEOS + " TEXT"
            + ")";
    String CREATE_MEDIAS = "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIAS + "("
            + Medias.TYPE + " TEXT, "
            + Medias.FOREIGNKEY + " TEXT, "
            + Medias.TEAMKEY +" TEXT, "
            + Medias.DETAILS + " TEXT, "
            + Medias.YEAR + " INTEGER"
            + ")";
    String CREATE_SEARCH_TEAMS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_TEAMS +
            " USING fts3 (" +
            SearchTeam.KEY + "," +
            SearchTeam.TITLES + "," +
            SearchTeam.NUMBER + ")";

    String CREATE_SEARCH_EVENTS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_EVENTS +
            " USING fts3 (" +
            SearchEvent.KEY + "," +
            SearchEvent.TITLES + "," +
            SearchEvent.YEAR + ")";

    protected SQLiteDatabase db;
    private static Database sDatabaseInstance;
    private static Semaphore sSemaphore;

    private Teams teamsTable;
    private Events eventsTable;
    private Awards awardsTable;
    private Matches matchesTable;
    private Medias mediasTable;
    private Response responseTable;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
        teamsTable = new Teams();
        eventsTable = new Events();
        awardsTable = new Awards();
        matchesTable = new Matches();
        mediasTable = new Medias();
        responseTable = new Response();
        sSemaphore = new Semaphore(1);
    }

    public Semaphore getSemaphore(){
        return sSemaphore;
    }

    public static synchronized Database getInstance(Context context) {
        return sDatabaseInstance;
    }

    public Teams getTeamsTable() {
        return teamsTable;
    }
    public Events getEventsTable() {
        return eventsTable;
    }
    public Awards getAwardsTable() {
        return awardsTable;
    }
    public Matches getMatchesTable() {
        return matchesTable;
    }
    public Medias getMediasTable() {
        return mediasTable;
    }
    public Response getResponseTable(){
        return responseTable;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_API);
        db.execSQL(CREATE_TEAMS);
        db.execSQL(CREATE_EVENTS);
        db.execSQL(CREATE_AWARDS);
        db.execSQL(CREATE_MATCHES);
        db.execSQL(CREATE_MEDIAS);
        db.execSQL(CREATE_SEARCH_TEAMS);
        db.execSQL(CREATE_SEARCH_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Constants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (newVersion >= DATABASE_VERSION && oldVersion < DATABASE_VERSION){
            /* For now, we're just gonna drop all data tables and wipe the data-related
               shared preferences just to put everyone on track starting from v8.

               For future versions, a possible idea is adding any previous db changes
               through a while loop/switch statement, incrementing up until the latest version.
               like http://blog.adamsbros.org/2012/02/28/upgrade-android-sqlite-database/

               If version is less than 8, then wipe remaining tables/data prefs.
            */
            // d-d-d-d-drop the tables (ノಠ益ಠ)ノ彡┻━┻ #dubstepwubwubz
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AWARDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIAS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_API);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_TEAMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_EVENTS);

            // Clear the data-related shared prefs
            Map<String, ?> allEntries = PreferenceManager.getDefaultSharedPreferences(context).getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().contains(DataManager.Events.ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR) ||
                    entry.getKey().contains(DataManager.Teams.ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE))
                {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().
                            remove(entry.getKey()).commit();
                }
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit().
                    remove(LaunchActivity.ALL_DATA_LOADED).commit();
        }

        onCreate(db);
    }

    public class Response {
        public static final String URL = "url", //text
                LASTUPDATE = "lastUpdated",     //timestamp for Last-Modified from API
                LASTHIT = "lastHit";           //last time we hit the API

        public long storeResponse(String url, String updated) {
            if(responseExists(url)){
                return updateResponse(url, updated);
            }
            ContentValues cv = new ContentValues();
            cv.put(Response.URL, url);
            cv.put(Response.LASTUPDATE, updated);
            cv.put(Response.LASTHIT, new Date().getTime());
            return safeInsert(TABLE_API, null, cv);
        }

        public boolean responseExists(String url) {
            Cursor cursor = safeQuery(TABLE_API, new String[]{Response.URL}, Response.URL + "=?", new String[]{url}, null, null, null, null);
            boolean exists = (cursor.moveToFirst()) || (cursor.getCount() != 0);
            cursor.close();
            return exists;
        }

        public int deleteResponse(String url) {
            if (responseExists(url)) {
                return safeDelete(TABLE_API, Response.URL + "=?", new String[]{url});
            }
            return 0;
        }

        public void deleteAllResponses() {
            safeDelete(TABLE_API, "", new String[]{});
        }

        public int updateResponse(String url, String updated) {
            ContentValues cv = new ContentValues();
            cv.put(Response.LASTUPDATE, updated);
            cv.put(Response.LASTHIT, new Date().getTime());
            return safeUpdate(TABLE_API, cv, Response.URL + "=?", new String[]{url});
        }

        /**
         * Just updates the last hit time in the database.
         * Like UNIX `touch`
         * @param url URL for the record to touch
         * @return update code
         */
        public int touchResponse(String url){
            if(responseExists(url)) {
                ContentValues cv = new ContentValues();
                cv.put(Response.LASTHIT, new Date().getTime());
                return safeUpdate(TABLE_API, cv, Response.URL + "=?", new String[]{url});
            }else{
                return -1;
            }
        }
    }
    public class Teams implements ModelTable<Team>{
        public static final String KEY = "key",
                NUMBER = "number",
                NAME = "name",
                SHORTNAME = "shortname",
                LOCATION = "location",
                WEBSITE = "website",
                EVENTS = "events";

        public long add(Team team) {
            if(!exists(team.getTeamKey())) {
                insertSearchItemTeam(team);
                return safeInsert(TABLE_TEAMS, null, team.getParams());
            }else{
                return update(team);
            }
        }

        @Override
        public int update(Team in) {
            updateSearchItemTeam(in);
            return safeUpdate(TABLE_TEAMS, in.getParams(), Teams.KEY + " = ?", new String[]{in.getTeamKey()});
        }

        public void storeTeams(ArrayList<Team> teams) {
            Semaphore dbSemaphore = null;
            try{
                dbSemaphore = getSemaphore();
                dbSemaphore.acquire();
                db.beginTransaction();
                for (Team team : teams) {
                    db.insert(TABLE_TEAMS, null, team.getParams());
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to aquire database semaphore");
            }finally {
                if(dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        public Team get(String teamKey, String[] fields) {
            Cursor cursor = safeQuery(TABLE_TEAMS, fields, Teams.KEY + " = ?", new String[]{teamKey}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Team team = ModelInflater.infalteTeam(cursor);
                cursor.close();
                return team;
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_TEAMS, new String[]{}, Teams.KEY + " = ?", new String[]{key}, null, null, null, null);
            boolean result = cursor != null && cursor.moveToFirst();
            if(cursor != null){
                cursor.close();
            }
            return result;
        }

        @Override
        public void delete(Team in) {
            safeDelete(TABLE_TEAMS, Teams.KEY + " = ? ", new String[]{in.getTeamKey()});
        }

        public ArrayList<Team> getInRange(int lowerBound, int upperBound, String[] fields) {
            ArrayList<Team> teams = new ArrayList<>();
            // ?+0 ensures that string arguments that are really numbers are cast to numbers for the query
            Cursor cursor = safeQuery(TABLE_TEAMS, fields, Teams.NUMBER + " BETWEEN ?+0 AND ?+0", new String[]{String.valueOf(lowerBound), String.valueOf(upperBound)}, null, null, null, null);
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    teams.add(ModelInflater.infalteTeam(cursor));
                }while (cursor.moveToNext());
                cursor.close();
            }
            return teams;
        }

        public Cursor getCursorForTeamsInRange(int lowerBound, int upperBound) {
            Cursor cursor = safeRawQuery("SELECT " + TABLE_TEAMS + ".rowid as '_id',"
                    + Teams.KEY + ","
                    + Teams.NUMBER + ","
                    + Teams.NAME + ","
                    + Teams.SHORTNAME + ","
                    + Teams.LOCATION
                    + " FROM " + TABLE_TEAMS + " WHERE " + Teams.NUMBER + " BETWEEN ?+0 AND ?+0 ORDER BY ? ASC", new String[]{String.valueOf(lowerBound), String.valueOf(upperBound), Teams.NUMBER});

            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
            return cursor;
        }
    }
    public class Events implements ModelTable<Event>{
        public static final String KEY = "key",
                YEAR = "year",
                NAME = "name",
                LOCATION = "location",
                VENUE = "venue",
                TYPE = "eventType",
                DISTRICT = "eventDistrict",
                DISTRICT_STRING = "districtString",
                START = "startDate",
                END = "endDate",
                OFFICIAL = "official",
                WEEK = "competitionWeek",
                RANKINGS = "rankings",
                ALLIANCES = "alliances",
                STATS = "stats";

        public long add(Event event) {
            if (!exists(event.getEventKey())) {
                insertSearchItemEvent(event);
                return safeInsert(TABLE_EVENTS, null, event.getParams());
            } else {
                return update(event);
            }
        }

        public void storeEvents(ArrayList<Event> events) {
            Semaphore dbSemaphore = null;
            try{
                dbSemaphore = getSemaphore();
                dbSemaphore.acquire();
                db.beginTransaction();
                for (Event event : events) {
                    db.insert(TABLE_EVENTS, null, event.getParams());
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to aquire database semaphore");
            }finally {
                if(dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        public Event get(String eventKey, String[] fields) {
            Cursor cursor = safeQuery(TABLE_EVENTS, fields, Events.KEY + " = ?", new String[]{eventKey}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Event event = ModelInflater.inflateEvent(cursor);
                cursor.close();
                return event;
            } else {
                return null;
            }
        }

        public ArrayList<Event> getInWeek(int year, int week, String[] fields) {
            ArrayList<Event> events = new ArrayList<>();
            Cursor cursor = safeQuery(TABLE_EVENTS, fields, Events.KEY + " LIKE ? AND " + Events.WEEK + " = ?", new String[]{Integer.toString(year) + "%", Integer.toString(week)}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Event event = ModelInflater.inflateEvent(cursor);
                    events.add(event);
                } while (cursor.moveToNext());
                cursor.close();
                return events;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find events in " + year + " week " + week);
                return null;
            }
        }

        public ArrayList<Event> getInYear(int year, String[] fields) {
            ArrayList<Event> events = new ArrayList<>();
            Cursor cursor = safeQuery(TABLE_EVENTS, fields, Events.KEY + " LIKE ?", new String[]{Integer.toString(year) + "%"}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Event event = ModelInflater.inflateEvent(cursor);
                    events.add(event);
                } while (cursor.moveToNext());
                cursor.close();
                return events;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find events in " + year);
                return null;
            }
        }

        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_EVENTS, new String[]{Events.KEY}, Events.KEY + "=?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public void delete(Event in) {
            safeDelete(TABLE_EVENTS, Events.KEY + " = ?", new String[]{in.getEventKey()});
        }

        public int update(Event event) {
            updateSearchItemEvent(event);
            return safeUpdate(TABLE_EVENTS, event.getParams(), Events.KEY + "=?", new String[]{event.getEventKey()});
        }

    }
    public class Awards implements ModelTable<Award>{
        public static final String EVENTKEY = "eventKey",
                NAME = "name",
                YEAR = "year",
                WINNERS = "winners";

        @Override
        public long add(Award in) {
            return safeInsert(TABLE_AWARDS, null, in.getParams());
        }

        @Override
        public int update(Award in) {
            return safeUpdate(TABLE_AWARDS, in.getParams(), Awards.EVENTKEY + " = ? AND " + Awards.NAME + " = ? ", new String[]{in.getEventKey(), in.getName()});
        }

        /*
         * For Awards, key is eventKey:awardName
         */
        @Override
        public Award get(String key, String[] fields) {
            String eventKey = key.split(":")[0];
            String awardName = key.split(":")[1];
            Cursor cursor = safeQuery(TABLE_AWARDS, fields, Awards.EVENTKEY + " = ? AND " + Awards.NAME + " = ? ", new String[]{eventKey, awardName}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Award award = ModelInflater.inflateAward(cursor);
                cursor.close();
                return award;
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            String eventKey = key.split(":")[0];
            String awardName = key.split(":")[1];
            Cursor cursor = safeQuery(TABLE_AWARDS, new String[]{}, Awards.EVENTKEY + " = ? AND " + Awards.NAME + " = ? ", new String[]{eventKey, awardName}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public void delete(Award in) {
            safeDelete(TABLE_AWARDS, Awards.EVENTKEY + " = ? AND " + Awards.NAME + " = ? ", new String[]{in.getEventKey(), in.getName()});
        }
    }
    public class Matches implements ModelTable<Match>{
        public static final String KEY = "key",
            EVENT = "eventKey",
            TIMESTRING = "timeString",
            TIME = "time",
            ALLIANCES = "alliances",
            VIDEOS = "videos";

        @Override
        public long add(Match in) {
            if (!exists(in.getEventKey())) {
                return safeInsert(TABLE_EVENTS, null, in.getParams());
            } else {
                return update(in);
            }
        }

        @Override
        public int update(Match in) {
            return safeUpdate(TABLE_MATCHES, in.getParams(), Matches.KEY + " = ?", new String[]{in.getKey()});
        }

        @Override
        public Match get(String key, String[] fields) {
            Cursor cursor = safeQuery(TABLE_EVENTS, fields, Matches.KEY + " = ?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Match match = ModelInflater.inflateMatch(cursor);
                cursor.close();
                return match;
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_MATCHES, new String[]{Matches.KEY}, Matches.KEY + "=?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public void delete(Match in) {
            safeDelete(TABLE_MATCHES, Matches.KEY + " = ?", new String[]{in.getKey()});
        }
    }
    public class Medias implements ModelTable<Media>{
        public static final String TYPE = "type",
            FOREIGNKEY = "foreignKey",
            TEAMKEY = "teamKey",
            DETAILS = "details",
            YEAR = "year";

        @Override
        public long add(Media in) {
            if (!exists(in.getForeignKey())) {
                return safeInsert(TABLE_MEDIAS, null, in.getParams());
            } else {
                return update(in);
            }
        }

        @Override
        public int update(Media in) {
            return safeUpdate(TABLE_MEDIAS, in.getParams(), Medias.FOREIGNKEY + " = ?", new String[]{in.getForeignKey()});
        }

        @Override
        public Media get(String foreignKey, String[] fields) {
            Cursor cursor = safeQuery(TABLE_MEDIAS, fields, Medias.FOREIGNKEY + " = ?", new String[]{foreignKey}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Media media = ModelInflater.inflateMedia(cursor);
                cursor.close();
                return media;
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_MEDIAS, new String[]{}, Medias.FOREIGNKEY + "=?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public void delete(Media in) {
            safeDelete(TABLE_MEDIAS, Medias.FOREIGNKEY + " = ? ", new String[]{in.getForeignKey()});
        }
    }

    public class SearchTeam {
        public static final String
                KEY = "key",
                TITLES = "titles",
                NUMBER = "number";
    }

    public class SearchEvent {
        public static final String
                KEY = "key",
                TITLES = "titles",
                YEAR = "year";
    }

    public Cursor safeQuery(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try{
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }
    public Cursor safeRawQuery(String query, String[] args){
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try{
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            cursor = db.rawQuery(query, args);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }
    public int safeUpdate(String table, ContentValues values, String whereClause, String[] whereArgs){
        Semaphore dbSemaphore = null;
        int response = -1;
        try{
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            response = db.update(table, values, whereClause, whereArgs);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return response;
    }
    public long safeInsert(String table, String nullColumnHack, ContentValues values){
        Semaphore dbSemaphore = null;
        long response = -1;
        try{
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            response = db.insert(table, nullColumnHack, values);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return response;
    }
    public int safeDelete(String table, String whereClause, String[] whereArgs){
        Semaphore dbSemaphore = null;
        int response = -1;
        try{
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            response = db.delete(table, whereClause, whereArgs);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return response;
    }

    public APIResponse<String> getResponse(String url) {
        Cursor cursor = safeQuery(TABLE_API, new String[]{Response.URL, Response.LASTUPDATE, Response.LASTHIT},
                Response.URL + "=?", new String[]{url}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String lastUpdate = cursor.getString(1);
            long lastHit = cursor.getLong(2);
            cursor.close();
            return new APIResponse<>(null, APIResponse.CODE.LOCAL, lastUpdate, new Date(lastHit));
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find response in database with url " + url);
            return null;
        }
    }

    public long insertSearchItemTeam(Team team) {
        ContentValues cv = new ContentValues();
        cv.put(SearchTeam.KEY, team.getTeamKey());
        cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
        cv.put(SearchTeam.NUMBER, team.getTeamNumber());
        return safeInsert(TABLE_SEARCH_TEAMS, null, cv);
    }

    public long insertSearchItemEvent(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SearchEvent.KEY, event.getEventKey());
        cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
        cv.put(SearchEvent.YEAR, event.getEventYear());
        return safeInsert(TABLE_SEARCH_EVENTS, null, cv);
    }

    public long updateSearchItemTeam(Team team) {
        ContentValues cv = new ContentValues();
        cv.put(SearchTeam.KEY, team.getTeamKey());
        cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
        cv.put(SearchTeam.NUMBER, team.getTeamNumber());
        return db.update(TABLE_SEARCH_TEAMS, cv, SearchTeam.KEY + "=?", new String[]{team.getTeamKey()});
    }

    public long updateSearchItemEvent(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SearchEvent.KEY, event.getEventKey());
        cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
        cv.put(SearchEvent.YEAR, event.getEventYear());
        return safeUpdate(TABLE_SEARCH_EVENTS, cv, SearchEvent.KEY + "=?", new String[]{event.getEventKey()});
    }

    public void deleteSearchItemTeam(Team team){
        safeDelete(TABLE_SEARCH_TEAMS, SearchTeam.KEY + " = ?", new String[]{team.getTeamKey()});
    }

    public void deleteSearchItemEvent(Event event){
        safeDelete(TABLE_SEARCH_EVENTS, SearchEvent.KEY + " = ?", new String[]{event.getEventKey()});
    }

    public Cursor getMatchesForTeamQuery(String query) {
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try{
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            String selection = SearchTeam.TITLES + " MATCH ?";
            String[] selectionArgs = new String[]{query};

            SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
            builder.setTables(TABLE_SEARCH_TEAMS);

            cursor = builder.query(db,
                    new String[]{SearchTeam.KEY, SearchTeam.TITLES, SearchTeam.NUMBER}, selection, selectionArgs, null, null, SearchTeam.NUMBER + " ASC");

            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }

    public Cursor getMatchesForEventQuery(String query) {
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try {
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            String selection = SearchEvent.TITLES + " MATCH ?";
            String[] selectionArgs = new String[]{query};

            SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
            builder.setTables(TABLE_SEARCH_EVENTS);

            cursor = builder.query(db,
                    new String[]{SearchEvent.KEY, SearchEvent.TITLES, SearchEvent.YEAR}, selection, selectionArgs, null, null, SearchEvent.YEAR + " DESC");

            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
        }catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }

    public Cursor getTeamsForTeamQuery(String query) {
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try {
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS tempteams");
            String createTempTeams = "CREATE TEMP TABLE tempteams (tempkey TEXT)";
            db.execSQL(createTempTeams);
            db.execSQL("INSERT INTO tempteams SELECT " + SearchTeam.KEY + " FROM " + TABLE_SEARCH_TEAMS + " WHERE " + SearchTeam.TITLES + " MATCH ?", new String[]{query});
            cursor = db.rawQuery("SELECT " + TABLE_TEAMS + ".rowid as '_id',"
                    + Teams.KEY + ","
                    + Teams.NUMBER + ","
                    + Teams.NAME + ","
                    + Teams.SHORTNAME + ","
                    + Teams.LOCATION
                    + " FROM " + TABLE_TEAMS + " JOIN tempteams ON tempteams.tempkey = " + TABLE_TEAMS + "." + Teams.KEY + " ORDER BY ? ASC", new String[]{Teams.NUMBER});

            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
        }catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }

    public Cursor getEventsForQuery(String query) {
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try {
            dbSemaphore = getSemaphore();
            dbSemaphore.acquire();
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS tempevents");
            String createTempTeams = "CREATE TEMP TABLE tempevents (tempkey TEXT)";
            db.execSQL(createTempTeams);
            db.execSQL("INSERT INTO tempevents SELECT " + SearchTeam.KEY + " FROM " + TABLE_SEARCH_EVENTS + " WHERE " + SearchEvent.TITLES + " MATCH ?", new String[]{query});
            cursor = db.rawQuery("SELECT " + TABLE_EVENTS + ".rowid as '_id',"
                            + Events.KEY + ","
                            + Events.NAME + ","
                            + Events.TYPE + ","
                            + Events.DISTRICT + ","
                            + Events.START + ","
                            + Events.END + ","
                            + Events.LOCATION + ","
                            + Events.VENUE + ","
                            + Events.OFFICIAL + ","
                            + Events.DISTRICT_STRING
                            + " FROM " + TABLE_EVENTS + " JOIN tempevents ON tempevents.tempkey = " + TABLE_EVENTS + "." + Events.KEY + " ORDER BY ? DESC", new String[]{SearchEvent.YEAR}
            );

            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
        }catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }
}
