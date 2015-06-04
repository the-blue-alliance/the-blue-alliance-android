package com.thebluealliance.androidclient.datafeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.interfaces.ModelTable;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 24;
    private Context context;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",
            TABLE_API = "api",
            TABLE_TEAMS = "teams",
            TABLE_EVENTS = "events",
            TABLE_AWARDS = "awards",
            TABLE_MATCHES = "matches",
            TABLE_MEDIAS = "medias",
            TABLE_EVENTTEAMS = "eventTeams",
            TABLE_DISTRICTS = "districts",
            TABLE_DISTRICTTEAMS = "districtTeams",
            TABLE_FAVORITES = "favorites",
            TABLE_SUBSCRIPTIONS = "subscriptions",
            TABLE_SEARCH = "search",
            TABLE_SEARCH_TEAMS = "search_teams",
            TABLE_SEARCH_EVENTS = "search_events",
            TABLE_NOTIFICATIONS = "notifications";

    String CREATE_API = "CREATE TABLE IF NOT EXISTS " + TABLE_API + "("
            + Response.URL + " TEXT PRIMARY KEY NOT NULL, "
            + Response.LASTUPDATE + " TIMESTAMP, "
            + Response.LASTHIT + " TIMESTAMP "
            + ")";
    String CREATE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + "("
            + Teams.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + Teams.NUMBER + " INTEGER NOT NULL, "
            + Teams.NAME + " TEXT DEFAULT '', "
            + Teams.SHORTNAME + " TEXT DEFAULT '', "
            + Teams.LOCATION + " TEXT DEFAULT '',"
            + Teams.WEBSITE + " TEXT DEFAULT '', "
            + Teams.YEARS_PARTICIPATED + " TEXT DEFAULT '' "
            + ")";
    String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
            + Events.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + Events.YEAR + " INTEGER NOT NULL, "
            + Events.NAME + " TEXT DEFAULT '', "
            + Events.SHORTNAME + " TEXT DEFAULT '', "
            + Events.LOCATION + " TEXT DEFAULT '', "
            + Events.VENUE + " TEXT DEFAULT '', "
            + Events.TYPE + " INTEGER DEFAULT -1, "
            + Events.DISTRICT + " INTEGER DEFAULT -1, "
            + Events.DISTRICT_STRING + " TEXT DEFAULT '', "
            + Events.DISTRICT_POINTS + " TEXT DEFAULT '', "
            + Events.START + " TIMESTAMP, "
            + Events.END + " TIMESTAMP, "
            + Events.OFFICIAL + " INTEGER DEFAULT 0, "
            + Events.WEEK + " INTEGER DEFAULT -1, "
            + Events.TEAMS + " TEXT DEFAULT '', "
            + Events.RANKINGS + " TEXT DEFAULT '', "
            + Events.ALLIANCES + " TEXT DEFAULT '', "
            + Events.WEBCASTS + " TEXT DEFAULT '', "
            + Events.STATS + " TEXT DEFAULT '', "
            + Events.WEBSITE + " TEXT DEFAULT '' "
            + ")";
    String CREATE_AWARDS = "CREATE TABLE IF NOT EXISTS " + TABLE_AWARDS + "("
            + Awards.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + Awards.ENUM + " INTEGER DEFAULT -1, "
            + Awards.EVENTKEY + " TEXT DEFAULT '', "
            + Awards.NAME + " TEXT DEFAULT '', "
            + Awards.YEAR + " INTEGER DEFAULT -1, "
            + Awards.WINNERS + " TEXT DEFAULT '' "
            + ")";
    String CREATE_MATCHES = "CREATE TABLE IF NOT EXISTS " + TABLE_MATCHES + "("
            + Matches.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + Matches.SETNUM + " INTEGER DEFAULT -1,"
            + Matches.MATCHNUM + " INTEGER DEFAULT -1,"
            + Matches.EVENT + " TEXT DEFAULT '', "
            + Matches.TIMESTRING + " TEXT DEFAULT '', "
            + Matches.TIME + " TIMESTAMP, "
            + Matches.ALLIANCES + " TEXT DEFAULT '', "
            + Matches.VIDEOS + " TEXT DEFAULT '' "
            + ")";
    String CREATE_MEDIAS = "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIAS + "("
            + Medias.TYPE + " TEXT DEFAULT '', "
            + Medias.FOREIGNKEY + " TEXT DEFAULT '', "
            + Medias.TEAMKEY + " TEXT DEFAULT '', "
            + Medias.DETAILS + " TEXT DEFAULT '', "
            + Medias.YEAR + " INTEGER  DEFAULT -1"
            + ")";
    String CREATE_EVENTTEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTTEAMS + "("
            + EventTeams.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + EventTeams.TEAMKEY + " TEXT DEFAULT '', "
            + EventTeams.EVENTKEY + " TEXT DEFAULT '', "
            + EventTeams.YEAR + " INTEGER DEFAULT -1, "
            + EventTeams.COMPWEEK + " INTEGER DEFAULT -1 "
            + ")";
    String CREATE_DISTRICTS = "CREATE TABLE IF NOT EXISTS " + TABLE_DISTRICTS + "("
            + Districts.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + Districts.ABBREV + " TEXT NOT NULL, "
            + Districts.YEAR + " INTEGER NOT NULL, "
            + Districts.ENUM + " INTEGER NOT NULL,"
            + Districts.NAME + " TEXT DEFAULT ''"
            + ")";
    String CREATE_DISTRICTTEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_DISTRICTTEAMS + "("
            + DistrictTeams.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + DistrictTeams.TEAM_KEY + " TEXT NOT NULL, "
            + DistrictTeams.DISTRICT_KEY + " TEXT NOT NULL, "
            + DistrictTeams.DISTRICT_ENUM + " INTEGER NOT NULL, "
            + DistrictTeams.YEAR + " INTEGER NOT NULL, "
            + DistrictTeams.RANK + " INTEGER DEFAULT -1, "
            + DistrictTeams.EVENT1_KEY + " TEXT DEFAULT '', "
            + DistrictTeams.EVENT1_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeams.EVENT2_KEY + " TEXT DEFAULT '', "
            + DistrictTeams.EVENT2_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeams.CMP_KEY + " TEXT DEFAULT '', "
            + DistrictTeams.CMP_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeams.ROOKIE_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeams.TOTAL_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeams.JSON + " TEXT DEFAULT '' "
            + ")";
    String CREATE_FAVORITES = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + "("
            + Favorites.KEY + " TEXT PRIMARY KEY NOT NULL,"
            + Favorites.USER_NAME + " TEXT NOT NULL, "
            + Favorites.MODEL_KEY + " TEXT NOT NULL,"
            + Favorites.MODEL_ENUM + " INTEGER NOT NULL"
            + ")";
    String CREATE_SUBSCRIPTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBSCRIPTIONS + "("
            + Subscriptions.KEY + " TEXT PRIMARY KEY NOT NULL,"
            + Subscriptions.USER_NAME + " TEXT NOT NULL,"
            + Subscriptions.MODEL_KEY + " TEXT NOT NULL,"
            + Subscriptions.MODEL_ENUM + " INTEGER NOT NULL,"
            + Subscriptions.NOTIFICATION_SETTINGS + " TEXT DEFAULT '[]'"
            + ")";
    String CREATE_SEARCH_TEAMS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_TEAMS +
            " USING fts3 (" +
            SearchTeam.KEY + " TEXT PRIMARY KEY, " +
            SearchTeam.TITLES + " TEXT, " +
            SearchTeam.NUMBER + " TEXT, " +
            ")";

    String CREATE_SEARCH_EVENTS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_EVENTS +
            " USING fts3 (" +
            SearchEvent.KEY + " TEXT PRIMARY KEY, " +
            SearchEvent.TITLES + " TEXT, " +
            SearchEvent.YEAR + " TEXT,  " +
            ")";

    String CREATE_NOTIFICATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "(" +
            Notifications.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            Notifications.TYPE + " TEXT NOT NULL, " +
            Notifications.TITLE + " TEXT DEFAULT '', " +
            Notifications.BODY + " TEXT DEFAULT '', " +
            Notifications.INTENT + " TEXT DEFAULT '', " +
            Notifications.TIME + " TIMESTAMP, " +
            Notifications.SYSTEM_ID + " INTEGER NOT NULL, " +
            Notifications.ACTIVE + " INTEGER DEFAULT 1 )";

    protected SQLiteDatabase mDb;
    private static Database sDatabaseInstance;

    private Teams mTeamsTable;
    private Events mEventsTable;
    private Awards mAwardsTable;
    private Matches mMatchesTable;
    private Medias mMediasTable;
    private EventTeams mEventTeamsTable;
    private Response mResponseTable;
    private Districts mDistrictsTable;
    private DistrictTeams mDistrictTeamsTable;
    private Favorites mFavoritesTable;
    private Subscriptions mSubscriptionsTable;
    private Notifications mNotificationsTable;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        mDb = getWritableDatabase();
        mTeamsTable = new Teams();
        mEventsTable = new Events();
        mAwardsTable = new Awards();
        mMatchesTable = new Matches();
        mMediasTable = new Medias();
        mEventTeamsTable = new EventTeams();
        mDistrictsTable = new Districts();
        mDistrictTeamsTable = new DistrictTeams();
        mFavoritesTable = new Favorites();
        mSubscriptionsTable = new Subscriptions();
        mResponseTable = new Response();
        mNotificationsTable = new Notifications();
    }

    public static synchronized Database getInstance(Context context) {
        if (sDatabaseInstance == null) {
            sDatabaseInstance = new Database(context);
            sDatabaseInstance.setWriteAheadLoggingEnabled(true);
        }
        return sDatabaseInstance;
    }

    public Teams getTeamsTable() {
        return mTeamsTable;
    }

    public Events getEventsTable() {
        return mEventsTable;
    }

    public Awards getAwardsTable() {
        return mAwardsTable;
    }

    public Matches getMatchesTable() {
        return mMatchesTable;
    }

    public Medias getMediasTable() {
        return mMediasTable;
    }

    public Response getResponseTable() {
        return mResponseTable;
    }

    public EventTeams getEventTeamsTable() {
        return mEventTeamsTable;
    }

    public Districts getDistrictsTable() {
        return mDistrictsTable;
    }

    public DistrictTeams getDistrictTeamsTable() {
        return mDistrictTeamsTable;
    }

    public Favorites getmFavoritesTable() {
        return mFavoritesTable;
    }

    public Subscriptions getSubscriptionsTable() {
        return mSubscriptionsTable;
    }

    public Notifications getNotificationsTable() {
        return mNotificationsTable;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_API);
        db.execSQL(CREATE_TEAMS);
        db.execSQL(CREATE_EVENTS);
        db.execSQL(CREATE_AWARDS);
        db.execSQL(CREATE_MATCHES);
        db.execSQL(CREATE_MEDIAS);
        db.execSQL(CREATE_EVENTTEAMS);
        db.execSQL(CREATE_DISTRICTS);
        db.execSQL(CREATE_DISTRICTTEAMS);
        db.execSQL(CREATE_FAVORITES);
        db.execSQL(CREATE_SUBSCRIPTIONS);
        db.execSQL(CREATE_NOTIFICATIONS);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            // bugfix for Android 4.0.x versions, using 'IF NOT EXISTS' throws errors
            // http://stackoverflow.com/questions/19849068/near-not-syntax-error-while-compiling-create-virtual-table-if-not-exists
            CREATE_SEARCH_EVENTS = CREATE_SEARCH_EVENTS.replace("IF NOT EXISTS", "");
            CREATE_SEARCH_TEAMS = CREATE_SEARCH_TEAMS.replace("IF NOT EXISTS", "");
        }
        db.execSQL(CREATE_SEARCH_TEAMS);
        db.execSQL(CREATE_SEARCH_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Constants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 14:
                    //add districts tables
                    db.execSQL(CREATE_DISTRICTS);
                    db.execSQL(CREATE_DISTRICTTEAMS);
                    db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + Events.DISTRICT_POINTS + " TEXT DEFAULT '' ");
                    break;
                case 15:
                    //add favorites and subscriptions
                    db.execSQL(CREATE_FAVORITES);
                    db.execSQL(CREATE_SUBSCRIPTIONS);
                    break;
                case 16:
                    // add column for individual notification settings and sorting by model type
                    Cursor sub = db.rawQuery("SELECT * FROM " + TABLE_SUBSCRIPTIONS + " LIMIT 0,1", null);
                    if (sub.getColumnIndex(Subscriptions.NOTIFICATION_SETTINGS) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_SUBSCRIPTIONS + " ADD COLUMN " + Subscriptions.NOTIFICATION_SETTINGS + " TEXT DEFAULT '[]' ");
                    }
                    if (sub.getColumnIndex(Subscriptions.MODEL_ENUM) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_SUBSCRIPTIONS + " ADD COLUMN " + Subscriptions.MODEL_ENUM + " INTEGER NOT NULL");
                    }
                    sub.close();
                    Cursor fav = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " LIMIT 0,1", null);
                    if (fav.getColumnIndex(Favorites.MODEL_ENUM) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_FAVORITES + " ADD COLUMN " + Favorites.MODEL_ENUM + " INTEGER NOT NULL");
                    }
                    fav.close();
                    break;
                case 17:
                    // add column for district name
                    Cursor dist = db.rawQuery("SELECT * FROM " + TABLE_DISTRICTS + " LIMIT 0,1", null);
                    if (dist.getColumnIndex(Districts.NAME) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_DISTRICTS + " ADD COLUMN " + Districts.NAME + " TEXT DEFAULT '' ");
                    }
                    dist.close();
                    break;
                case 18:
                    // add column for event short name
                    Cursor event = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " LIMIT 0,1", null);
                    if (event.getColumnIndex(Events.SHORTNAME) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + Events.SHORTNAME + " TEXT DEFAULT '' ");
                    }
                    event.close();
                    break;
                case 20:
                    // Create table for recent notification
                    db.execSQL(CREATE_NOTIFICATIONS);
                    break;
                case 23:
                case 24:
                    // remove and recreate search indexes to we can create them with foreign keys
                    db.execSQL("DROP TABLE " + TABLE_SEARCH_TEAMS);
                    db.execSQL("DROP TABLE " + TABLE_SEARCH_EVENTS);
                    onCreate(db);
                    break;
            }
            upgradeTo++;
        }
    }

    private void recreateDb(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AWARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTTEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRICTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRICTTEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_API);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);

        // Clear the data-related shared prefs
        Map<String, ?> allEntries = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().contains(DataManager.Events.ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR) ||
                    entry.getKey().contains(DataManager.Teams.ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE) ||
                    entry.getKey().contains(DataManager.Districts.ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR)) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().
                        remove(entry.getKey()).commit();
            }
        }

        PreferenceManager.getDefaultSharedPreferences(context).edit().
                remove(LaunchActivity.ALL_DATA_LOADED).commit();

        onCreate(db);
    }

    public class Response {
        public static final String URL = "url", //text
                LASTUPDATE = "lastUpdated",     //timestamp for Last-Modified from API
                LASTHIT = "lastHit";           //last time we hit the API

        public long storeResponse(String url, String updated) {
            if (responseExists(url)) {
                return updateResponse(url, updated);
            }
            ContentValues cv = new ContentValues();
            cv.put(Response.URL, url);
            cv.put(Response.LASTUPDATE, updated);
            cv.put(Response.LASTHIT, new Date().getTime());
            return mDb.insert(TABLE_API, null, cv);
        }

        public APIResponse<String> getResponseIfExists(String url) {
            Cursor cursor = mDb.query(TABLE_API, new String[]{Response.URL, Response.LASTUPDATE, Response.LASTHIT},
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

        public boolean responseExists(String url) {
            Cursor cursor = mDb.query(TABLE_API, new String[]{Response.URL}, Response.URL + "=?", new String[]{url}, null, null, null, null);
            boolean exists = (cursor.moveToFirst()) || (cursor.getCount() != 0);
            cursor.close();
            return exists;
        }

        public int deleteResponse(String url) {
            if (responseExists(url)) {
                return mDb.delete(TABLE_API, Response.URL + "=?", new String[]{url});
            }
            return 0;
        }

        public void deleteAllResponses() {
            mDb.delete(TABLE_API, "", new String[]{});
        }

        public int updateResponse(String url, String updated) {
            ContentValues cv = new ContentValues();
            cv.put(Response.LASTUPDATE, updated);
            cv.put(Response.LASTHIT, new Date().getTime());
            return mDb.update(TABLE_API, cv, Response.URL + "=?", new String[]{url});
        }

        /**
         * Just updates the last hit time in the database. Like UNIX `touch`
         *
         * @param url URL for the record to touch
         * @return update code
         */
        public int touchResponse(String url) {
            if (responseExists(url)) {
                ContentValues cv = new ContentValues();
                cv.put(Response.LASTHIT, new Date().getTime());
                return mDb.update(TABLE_API, cv, Response.URL + "=?", new String[]{url});
            } else {
                return -1;
            }
        }
    }

    public class Teams extends ModelTable<Team> {
        public static final String KEY = "key",
                NUMBER = "number",
                NAME = "name",
                SHORTNAME = "shortname",
                LOCATION = "location",
                WEBSITE = "website",
                YEARS_PARTICIPATED = "yearsParticipated";

        public Teams() {
            super(mDb);
        }

        @Override
        protected void insertCallback(Team team) {
            ContentValues cv = new ContentValues();
            try {
                cv.put(SearchTeam.KEY, team.getKey());
                cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
                cv.put(SearchTeam.NUMBER, team.getTeamNumber());
                mDb.insert(TABLE_SEARCH_TEAMS, null, cv);
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't insert search team without the following fields:" +
                        "Database.Teams.KEY, Database.Teams.NUMBER");
            } catch (SQLiteException e) {
                Log.w(Constants.LOG_TAG, "Trying to add a SearchTeam that already exists. "+team.getKey());
            }
        }

        @Override
        protected void updateCallback(Team team) {
            try {
                ContentValues cv = new ContentValues();
                cv.put(SearchTeam.KEY, team.getKey());
                cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
                cv.put(SearchTeam.NUMBER, team.getTeamNumber());
                mDb.update(TABLE_SEARCH_TEAMS, cv, SearchTeam.KEY + "=?", new String[]{team.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                        "Database.Events.KEY, Database.Events.YEAR");
            }
        }

        @Override
        protected void deleteCallback(Team team) {
                mDb.delete(TABLE_SEARCH_TEAMS, SearchTeam.KEY + " = ?", new String[]{team.getKey()});
        }
        protected String getTableName() {
            return TABLE_TEAMS;
        }

        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public Team inflate(Cursor cursor) {
            return ModelInflater.inflateTeam(cursor);
        }

        public Cursor getCursorForTeamsInRange(int lowerBound, int upperBound) {
            Cursor cursor = mDb.rawQuery("SELECT " + TABLE_TEAMS + ".rowid as '_id',"
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

        public void deleteAllSearchIndexes(){
            mDb.rawQuery("DELETE FROM " + getTableName(), new String[]{});
        }

        public void deleteSearchIndex(Team team){
            deleteCallback(team);
        }

        public void recreateAllSearchIndexes(List<Team> teams){
            mDb.beginTransaction();
            try{
                for(Team t: teams){
                    insertCallback(t);
                }
            }finally {
                mDb.setTransactionSuccessful();
            }
            mDb.endTransaction();
        }
    }

    public class Events extends ModelTable<Event> {
        public static final String KEY = "key",
                YEAR = "year",
                NAME = "name",
                SHORTNAME = "shortName",
                LOCATION = "location",
                VENUE = "venue",
                TYPE = "eventType",
                DISTRICT = "eventDistrict",
                DISTRICT_STRING = "districtString",
                DISTRICT_POINTS = "districtPoints",
                START = "startDate",
                END = "endDate",
                OFFICIAL = "official",
                WEEK = "competitionWeek",
                TEAMS = "teams",
                RANKINGS = "rankings",
                ALLIANCES = "alliances",
                WEBCASTS = "webcasts",
                STATS = "stats",
                WEBSITE = "website";

        public Events(){
            super(mDb);
        }

        @Override
        protected void insertCallback(Event event) {
            ContentValues cv = new ContentValues();
            try {
                cv.put(SearchEvent.KEY, event.getKey());
                cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
                cv.put(SearchEvent.YEAR, event.getEventYear());
                mDb.insert(TABLE_SEARCH_EVENTS, null, cv);

            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                        "Database.Events.KEY, Database.Events.YEAR");
            } catch (SQLiteException e) {
                Log.w(Constants.LOG_TAG, "Trying to add a SearchEvent that already exists. "+event.getKey());
            }
        }

        @Override
        protected void updateCallback(Event event) {
            try {
                ContentValues cv = new ContentValues();
                cv.put(SearchEvent.KEY, event.getKey());
                cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
                cv.put(SearchEvent.YEAR, event.getEventYear());

                mDb.update(TABLE_SEARCH_EVENTS, cv, SearchEvent.KEY + "=?", new String[]{event.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                        "Database.Events.KEY, Database.Events.YEAR");
            }
        }

        @Override
        protected void deleteCallback(Event event) {
            mDb.delete(TABLE_SEARCH_EVENTS, SearchEvent.KEY + " = ?", new String[]{event.getKey()});
        }

        @Override
        protected String getTableName() {
            return TABLE_EVENTS;
        }

        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public Event inflate(Cursor cursor) {
            return ModelInflater.inflateEvent(cursor);
        }

        public void deleteAllSearchIndexes(){
            mDb.rawQuery("DELETE FROM " + getTableName(), new String[]{});
        }

        public void deleteSearchIndex(Event event){
            deleteCallback(event);
        }

        public void recreateAllSearchIndexes(List<Event> events){
            mDb.beginTransaction();
            try{
                for(Event e:events){
                    insertCallback(e);
                }
            }finally {
                mDb.setTransactionSuccessful();
            }
            mDb.endTransaction();
        }
    }

    public class Awards extends ModelTable<Award> {
        public static final String KEY = "key",
                ENUM = "enum",
                EVENTKEY = "eventKey",
                NAME = "name",
                YEAR = "year",
                WINNERS = "winners";

        public Awards(){
            super(mDb);
        }

        @Override
        protected String getTableName() {
            return TABLE_AWARDS;
        }

        /*
         * For Awards, key is eventKey:awardName
         */
        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public Award inflate(Cursor cursor) {
            return ModelInflater.inflateAward(cursor);
        }
    }

    public class Matches extends ModelTable<Match> {
        public static final String KEY = "key",
                MATCHNUM = "matchNumber",
                SETNUM = "setNumber",
                EVENT = "eventKey",
                TIMESTRING = "timeString",
                TIME = "time",
                ALLIANCES = "alliances",
                VIDEOS = "videos";

        public Matches(){
            super(mDb);
        }

        @Override
        protected String getTableName() {
            return TABLE_MATCHES;
        }

        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public Match inflate(Cursor cursor) {
            return ModelInflater.inflateMatch(cursor);
        }
    }

    public class Medias extends ModelTable<Media> {
        public static final String TYPE = "type",
                FOREIGNKEY = "foreignKey",
                TEAMKEY = "teamKey",
                DETAILS = "details",
                YEAR = "year";

        public Medias(){
            super(mDb);
        }

        @Override
        protected String getTableName() {
            return TABLE_MEDIAS;
        }

        @Override
        protected String getKeyColumn() {
            return FOREIGNKEY;
        }

        @Override
        public Media inflate(Cursor cursor) {
            return ModelInflater.inflateMedia(cursor);
        }
    }

    public class EventTeams extends ModelTable<EventTeam> {

        public static final String KEY = "key",
                TEAMKEY = "teamKey",
                EVENTKEY = "eventKey",
                YEAR = "year",
                COMPWEEK = "week";

        public EventTeams(){
            super(mDb);
        }

        public List<EventTeam> get(String teamKey, int year, String[] fields) {
            Cursor cursor = mDb.query(getTableName(), fields, TEAMKEY + " = ? AND " + YEAR + " + ?", new String[]{teamKey, Integer.toString(year)}, null, null, null, null);
            ArrayList<EventTeam> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    results.add(ModelInflater.inflateEventTeam(cursor));
                } while (cursor.moveToNext());
            }
            return results;
        }

        @Override
        protected String getTableName() {
            return TABLE_EVENTTEAMS;
        }

        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public EventTeam inflate(Cursor cursor) {
            return ModelInflater.inflateEventTeam(cursor);
        }
    }

    public class Districts extends ModelTable<District> {

        public static final String KEY = "key",
                ABBREV = "abbrev",
                ENUM = "enum",
                YEAR = "year",
                NAME = "name";

        public Districts(){
            super(mDb);
        }

        @Override
        protected String getTableName() {
            return TABLE_DISTRICTS;
        }

        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public District inflate(Cursor cursor) {
            return ModelInflater.inflateDistrict(cursor);
        }
    }

    public class DistrictTeams extends ModelTable<DistrictTeam> {

        public static final String KEY = "key",
                TEAM_KEY = "teamKey",
                DISTRICT_KEY = "districtKey",
                DISTRICT_ENUM = "districtEnum",
                YEAR = "year",
                RANK = "rank",
                EVENT1_KEY = "event1Key",
                EVENT1_POINTS = "event1Points",
                EVENT2_KEY = "event2Key",
                EVENT2_POINTS = "event2Points",
                CMP_KEY = "cmpKey",
                CMP_POINTS = "cmpPoints",
                ROOKIE_POINTS = "rookiePoints",
                TOTAL_POINTS = "totalPoints",
                JSON = "json";

        public DistrictTeams(){
            super(mDb);
        }

        @Override
        protected String getTableName() {
            return TABLE_DISTRICTTEAMS;
        }

        @Override
        protected String getKeyColumn() {
            return KEY;
        }

        @Override
        public DistrictTeam inflate(Cursor cursor) {
            return ModelInflater.inflateDistrictTeam(cursor);
        }
    }

    public class Favorites {
        public static final String KEY = "key",
                USER_NAME = "userName",
                MODEL_KEY = "modelKey",
                MODEL_ENUM = "model_enum";

        public long add(Favorite in) {
            if (!exists(in.getKey())) {
                return mDb.insert(TABLE_FAVORITES, null, in.getParams());
            }
            return -1;
        }

        public void add(ArrayList<Favorite> in) {
                mDb.beginTransaction();
                for (Favorite favorite : in) {
                    if (!unsafeExists(favorite.getKey())) {
                        mDb.insert(TABLE_FAVORITES, null, favorite.getParams());
                    }
                }
                mDb.setTransactionSuccessful();
        }

        public void remove(String key) {
            mDb.delete(TABLE_FAVORITES, KEY + " = ?", new String[]{key});
        }

        public boolean exists(String key) {
            Cursor cursor = mDb.query(TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        public Favorite get(String key) {
            Cursor cursor = mDb.query(TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return ModelInflater.inflateFavorite(cursor);
            }
            return null;
        }

        public boolean unsafeExists(String key) {
            Cursor cursor = mDb.query(TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        public ArrayList<Favorite> getForUser(String user) {
            Cursor cursor = mDb.query(TABLE_FAVORITES, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null);
            ArrayList<Favorite> favorites = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    favorites.add(ModelInflater.inflateFavorite(cursor));
                } while (cursor.moveToNext());
            }
            return favorites;
        }

        public void recreate(String user) {
            mDb.delete(TABLE_FAVORITES, USER_NAME + " = ?", new String[]{user});
        }
    }

    public class Subscriptions {
        public static final String KEY = "key",
                USER_NAME = "userName",
                MODEL_KEY = "modelKey",
                MODEL_ENUM = "model_enum",
                NOTIFICATION_SETTINGS = "settings";

        public long add(Subscription in) {
            if (!exists(in.getKey())) {
                return mDb.insert(TABLE_SUBSCRIPTIONS, null, in.getParams());
            } else {
                return update(in.getKey(), in);
            }
        }

        public int update(String key, Subscription in) {
            return mDb.update(TABLE_SUBSCRIPTIONS, in.getParams(), KEY + " = ?", new String[]{key});
        }

        public void add(ArrayList<Subscription> in) {
            mDb.beginTransaction();
            for (Subscription subscription : in) {
                if (!unsafeExists(subscription.getKey())) {
                    mDb.insert(TABLE_SUBSCRIPTIONS, null, subscription.getParams());
                }
            }
            mDb.setTransactionSuccessful();
        }

        public boolean exists(String key) {
            Cursor cursor = mDb.query(TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        public boolean unsafeExists(String key) {
            Cursor cursor = mDb.query(TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }

        public void remove(String key) {
            mDb.delete(TABLE_SUBSCRIPTIONS, KEY + " = ?", new String[]{key});
        }

        public Subscription get(String key) {
            Cursor cursor = mDb.query(TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return ModelInflater.inflateSubscription(cursor);
            }
            return null;
        }

        public ArrayList<Subscription> getForUser(String user) {
            Cursor cursor = mDb.query(TABLE_SUBSCRIPTIONS, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null);
            ArrayList<Subscription> subscriptions = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    subscriptions.add(ModelInflater.inflateSubscription(cursor));
                } while (cursor.moveToNext());
            }
            return subscriptions;
        }

        public void recreate(String user) {
            mDb.delete(TABLE_SUBSCRIPTIONS, USER_NAME + " = ?", new String[]{user});
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

    public class Notifications {
        public static final String
                ID = "_id",
                TYPE = "type",
                TITLE = "title",
                BODY = "body",
                INTENT = "intent",
                TIME = "time",
                SYSTEM_ID = "system_id",
                ACTIVE = "active";

        public void add(StoredNotification... in) {
            mDb.beginTransaction();
            for (StoredNotification notification : in) {
                mDb.insert(TABLE_NOTIFICATIONS, null, notification.getParams());
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
        }

        public ArrayList<StoredNotification> get() {
            ArrayList<StoredNotification> out = new ArrayList<>();
            Cursor cursor = mDb.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + ID + " DESC", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    out.add(ModelInflater.inflateStoredNotification(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return out;
        }

        public ArrayList<StoredNotification> getActive() {
            ArrayList<StoredNotification> out = new ArrayList<>();
            Cursor cursor = mDb.query(TABLE_NOTIFICATIONS, null, ACTIVE + " = 1", null, null, null, ID + " DESC", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    out.add(ModelInflater.inflateStoredNotification(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return out;
        }

        public void dismissAll() {
            ContentValues cv = new ContentValues();
            cv.put(ACTIVE, 0);
            mDb.update(TABLE_NOTIFICATIONS, cv, ACTIVE + "= 1", null);
        }

        private void delete(int id) {
            mDb.delete(TABLE_NOTIFICATIONS, ID + " = ? ", new String[]{Integer.toString(id)});
        }

        // Only allow 50 notifications to be stored
        public void prune() {
            mDb.beginTransaction();
            Cursor cursor = mDb.query(TABLE_NOTIFICATIONS, new String[]{ID}, "", new String[]{}, null, null, ID + " ASC", null);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = cursor.getCount(); i > 50; i--) {
                    mDb.delete(TABLE_NOTIFICATIONS, ID + " = ?", new String[]{cursor.getString(cursor.getColumnIndex(ID))});
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            }
            cursor.close();
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
        }
    }

    public Cursor getMatchesForTeamQuery(String query) {
        Cursor cursor = null;
        String selection = SearchTeam.TITLES + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_SEARCH_TEAMS);

        cursor = builder.query(mDb,
                new String[]{SearchTeam.KEY, SearchTeam.TITLES, SearchTeam.NUMBER}, selection, selectionArgs, null, null, SearchTeam.NUMBER + " ASC");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getMatchesForEventQuery(String query) {
        Cursor cursor = null;
        String selection = SearchEvent.TITLES + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_SEARCH_EVENTS);

        cursor = builder.query(mDb,
                new String[]{SearchEvent.KEY, SearchEvent.TITLES, SearchEvent.YEAR}, selection, selectionArgs, null, null, SearchEvent.YEAR + " DESC");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getTeamsForTeamQuery(String query) {
        Cursor cursor = null;
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
        return cursor;
    }

    public Cursor getEventsForQuery(String query) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tempevents");
        String createTempTeams = "CREATE TEMP TABLE tempevents (tempkey TEXT)";
        db.execSQL(createTempTeams);
        db.execSQL("INSERT INTO tempevents SELECT " + SearchTeam.KEY + " FROM " + TABLE_SEARCH_EVENTS + " WHERE " + SearchEvent.TITLES + " MATCH ?", new String[]{query});
        cursor = db.rawQuery("SELECT " + TABLE_EVENTS + ".rowid as '_id',"
                        + Events.KEY + ","
                        + Events.NAME + ","
                        + Events.SHORTNAME + ","
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
        return cursor;
    }
}
