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
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 20;
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
            SearchTeam.NUMBER + " TEXT )";

    String CREATE_SEARCH_EVENTS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_EVENTS +
            " USING fts3 (" +
            SearchEvent.KEY + " TEXT PRIMARY KEY, " +
            SearchEvent.TITLES + " TEXT, " +
            SearchEvent.YEAR + " TEXT )";
    
    String CREATE_NOTIFICATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "(" +
            Notifications.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Notifications.TYPE + " TEXT NOT NULL, " +
            Notifications.TITLE + " TEXT DEFAULT '', " +
            Notifications.BODY + " TEXT DEFAULT '', " +
            Notifications.INTENT + " TEXT DEFAULT '', " +
            Notifications.TIME + " TIMESTAMP )";

    protected SQLiteDatabase db;
    private static Database sDatabaseInstance;
    private Semaphore mSemaphore;
    private Semaphore mMyTBASemaphore;

    private Teams teamsTable;
    private Events eventsTable;
    private Awards awardsTable;
    private Matches matchesTable;
    private Medias mediasTable;
    private EventTeams eventTeamsTable;
    private Response responseTable;
    private Districts districtsTable;
    private DistrictTeams districtTeamsTable;
    private Favorites favoritesTable;
    private Subscriptions subscriptionsTable;
    private Notifications notificationsTable;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
        teamsTable = new Teams();
        eventsTable = new Events();
        awardsTable = new Awards();
        matchesTable = new Matches();
        mediasTable = new Medias();
        eventTeamsTable = new EventTeams();
        districtsTable = new Districts();
        districtTeamsTable = new DistrictTeams();
        favoritesTable = new Favorites();
        subscriptionsTable = new Subscriptions();
        responseTable = new Response();
        notificationsTable = new Notifications();
        mSemaphore = new Semaphore(1);
        mMyTBASemaphore = new Semaphore(1);
    }

    public Semaphore getSemaphore() {
        return mSemaphore;
    }

    public Semaphore getMyTBASemaphore() {
        return mMyTBASemaphore;
    }

    public static synchronized Database getInstance(Context context) {
        if (sDatabaseInstance == null) {
            sDatabaseInstance = new Database(context);
        }
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

    public Response getResponseTable() {
        return responseTable;
    }

    public EventTeams getEventTeamsTable() {
        return eventTeamsTable;
    }

    public Districts getDistrictsTable() {
        return districtsTable;
    }

    public DistrictTeams getDistrictTeamsTable() {
        return districtTeamsTable;
    }

    public Favorites getFavoritesTable() {
        return favoritesTable;
    }

    public Subscriptions getSubscriptionsTable() {
        return subscriptionsTable;
    }
    
    public Notifications getNotificationsTable(){
        return notificationsTable;
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
                    Cursor fav = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " LIMIT 0,1", null);
                    if (fav.getColumnIndex(Favorites.MODEL_ENUM) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_FAVORITES + " ADD COLUMN " + Favorites.MODEL_ENUM + " INTEGER NOT NULL");
                    }
                    break;
                case 17:
                    // add column for district name
                    Cursor dist = db.rawQuery("SELECT * FROM " + TABLE_DISTRICTS + " LIMIT 0,1", null);
                    if(dist.getColumnIndex(Districts.NAME) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_DISTRICTS + " ADD COLUMN " + Districts.NAME + " TEXT DEFAULT '' ");
                    }
                    break;
                case 18:
                    // add column for event short name
                    Cursor event = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " LIMIT 0,1", null);
                    if(event.getColumnIndex(Events.SHORTNAME) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + Events.SHORTNAME + " TEXT DEFAULT '' ");
                    }
                    break;
                case 20:
                    // Create table for notification dashboard
                    db.execSQL(CREATE_NOTIFICATIONS);
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
            return safeInsert(TABLE_API, null, cv);
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
         *
         * @param url URL for the record to touch
         * @return update code
         */
        public int touchResponse(String url) {
            if (responseExists(url)) {
                ContentValues cv = new ContentValues();
                cv.put(Response.LASTHIT, new Date().getTime());
                return safeUpdate(TABLE_API, cv, Response.URL + "=?", new String[]{url});
            } else {
                return -1;
            }
        }
    }

    public class Teams implements ModelTable<Team> {
        public static final String KEY = "key",
                NUMBER = "number",
                NAME = "name",
                SHORTNAME = "shortname",
                LOCATION = "location",
                WEBSITE = "website",
                YEARS_PARTICIPATED = "yearsParticipated";

        public long add(Team team) {
            try {
                if (!exists(team.getTeamKey())) {
                    insertSearchItemTeam(team);
                    return safeInsert(TABLE_TEAMS, null, team.getParams());
                } else {
                    return update(team);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add team without Database.Teams.KEY");
                return -1;
            }
        }

        @Override
        public int update(Team in) {
            updateSearchItemTeam(in);
            try {
                return safeUpdate(TABLE_TEAMS, in.getParams(), Teams.KEY + " = ?", new String[]{in.getTeamKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update team without Database.Teams.KEY");
                return -1;
            }
        }

        public void storeTeams(ArrayList<Team> teams) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Team team : teams) {
                    try {
                        if (!unsafeExists(team.getTeamKey())) {
                            db.insert(TABLE_TEAMS, null, team.getParams());

                            //add search team item
                            insertSearchItemTeam(team, false);
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Unable to add team - missing key.");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        public Team get(String teamKey, String[] fields) {
            Cursor cursor = safeQuery(TABLE_TEAMS, fields, Teams.KEY + " = ?", new String[]{teamKey}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Team team = ModelInflater.inflateTeam(cursor);
                cursor.close();
                return team;
            } else {
                return null;
            }
        }

        public Team get(String teamKey) {
            Cursor cursor = safeRawQuery("SELECT * FROM " + TABLE_TEAMS + " WHERE " + Teams.KEY + " = ?", new String[]{teamKey});
            if (cursor != null && cursor.moveToFirst()) {
                Team team = ModelInflater.inflateTeam(cursor);
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
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }

        @Override
        public void delete(Team in) {
            try {
                safeDelete(TABLE_TEAMS, Teams.KEY + " = ? ", new String[]{in.getTeamKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete team without Database.Teams.KEY");
            }
        }

        public ArrayList<Team> getInRange(int lowerBound, int upperBound, String[] fields) {
            ArrayList<Team> teams = new ArrayList<>();
            // ?+0 ensures that string arguments that are really numbers are cast to numbers for the query
            Cursor cursor = safeQuery(TABLE_TEAMS, fields, Teams.NUMBER + " BETWEEN ?+0 AND ?+0", new String[]{String.valueOf(lowerBound), String.valueOf(upperBound)}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    teams.add(ModelInflater.inflateTeam(cursor));
                } while (cursor.moveToNext());
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

        public boolean unsafeExists(String key) {
            Cursor cursor = db.query(TABLE_TEAMS, new String[]{Events.KEY}, Events.KEY + "=?", new String[]{key}, null, null, null, null);
            boolean result;
            if (cursor != null) {
                result = cursor.moveToFirst();
                cursor.close();
            } else {
                result = false;
            }
            return result;
        }
    }

    public class Events implements ModelTable<Event> {
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

        public long add(Event event) {
            try {
                if (!exists(event.getEventKey())) {
                    insertSearchItemEvent(event);
                    return safeInsert(TABLE_EVENTS, null, event.getParams());
                } else {
                    return update(event);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add event without Database.Events.KEY");
                return -1;
            }
        }

        public void storeEvents(ArrayList<Event> events) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Event event : events) {
                    try {
                        if (!unsafeExists(event.getEventKey())) {
                            db.insert(TABLE_EVENTS, null, event.getParams());
                            insertSearchItemEvent(event, false);
                        } else {
                            db.update(TABLE_EVENTS, event.getParams(), KEY + " =?", new String[]{event.getEventKey()});
                            updateSearchItemEvent(event, false);
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Unable to add event - missing key.");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
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

        public Event get(String eventKey) {
            Cursor cursor = safeRawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE " + Events.KEY + " =?", new String[]{eventKey});
            if (cursor != null && cursor.moveToFirst()) {
                Event event = ModelInflater.inflateEvent(cursor);
                cursor.close();
                return event;
            } else {
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

        public boolean unsafeExists(String key) {
            Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY}, Events.KEY + "=?", new String[]{key}, null, null, null, null);
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
            try {
                safeDelete(TABLE_EVENTS, Events.KEY + " = ?", new String[]{in.getEventKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete event without Database.Events.KEY");
            }
        }

        public int update(Event event) {
            updateSearchItemEvent(event);
            try {
                return safeUpdate(TABLE_EVENTS, event.getParams(), Events.KEY + "=?", new String[]{event.getEventKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update event without Database.Events.KEY");
                return -1;
            }
        }

    }

    public class Awards implements ModelTable<Award> {
        public static final String KEY = "key",
                ENUM = "enum",
                EVENTKEY = "eventKey",
                NAME = "name",
                YEAR = "year",
                WINNERS = "winners";

        @Override
        public long add(Award in) {
            try {
                if (!exists(in.getKey())) {
                    return safeInsert(TABLE_AWARDS, null, in.getParams());
                } else {
                    return update(in);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add award without Database.Awards.KEY");
                return -1;
            }
        }

        public void add(ArrayList<Award> awards) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Award award : awards) {
                    try {
                        if (!unsafeExists(award.getKey())) {
                            db.insert(TABLE_AWARDS, null, award.getParams());
                        } else {
                            db.update(TABLE_AWARDS, award.getParams(), Awards.KEY + " = ? ", new String[]{award.getKey()});
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Unable to add award - missing event key or award name");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        @Override
        public int update(Award in) {
            try {
                return safeUpdate(TABLE_AWARDS, in.getParams(), Awards.EVENTKEY + " = ? AND " + Awards.NAME + " = ? ", new String[]{in.getEventKey(), in.getName()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update award without Database.Awards.EVENTKEY and Database.Awards.NAME");
                return -1;
            }
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
            Cursor cursor = safeQuery(TABLE_AWARDS, new String[]{}, KEY + " = ?", new String[]{key}, null, null, null, null);
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
            Cursor cursor = db.query(TABLE_AWARDS, new String[]{}, Awards.KEY + " = ? ", new String[]{key}, null, null, null, null);
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
            try {
                safeDelete(TABLE_AWARDS, Awards.EVENTKEY + " = ? AND " + Awards.NAME + " = ? ", new String[]{in.getEventKey(), in.getName()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete award without Database.Awards.EVENTKEY and Database.Awards.NAME");
            }
        }
    }

    public class Matches implements ModelTable<Match> {
        public static final String KEY = "key",
                MATCHNUM = "matchNumber",
                SETNUM = "setNumber",
                EVENT = "eventKey",
                TIMESTRING = "timeString",
                TIME = "time",
                ALLIANCES = "alliances",
                VIDEOS = "videos";

        @Override
        public long add(Match in) {
            try {
                if (!exists(in.getEventKey())) {
                    return safeInsert(TABLE_EVENTS, null, in.getParams());
                } else {
                    return update(in);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add match without Database.Matches.KEY");
                return -1;
            }
        }

        public void add(ArrayList<Match> matches) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Match match : matches) {
                    try {
                        if (!unsafeExists(match.getKey())) {
                            db.insert(TABLE_MATCHES, null, match.getParams());
                        } else {
                            db.update(TABLE_MATCHES, match.getParams(), KEY + " =?", new String[]{match.getKey()});
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Unable to add event - missing key.");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        @Override
        public int update(Match in) {
            try {
                return safeUpdate(TABLE_MATCHES, in.getParams(), Matches.KEY + " = ?", new String[]{in.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update match without Database.Matches.KEY");
                return -1;
            }
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

        public boolean unsafeExists(String key) {
            Cursor cursor = db.query(TABLE_MATCHES, new String[]{Matches.KEY}, Matches.KEY + "=?", new String[]{key}, null, null, null, null);
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
            try {
                safeDelete(TABLE_MATCHES, Matches.KEY + " = ?", new String[]{in.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete match without Database.Matches.KEY");
            }
        }
    }

    public class Medias implements ModelTable<Media> {
        public static final String TYPE = "type",
                FOREIGNKEY = "foreignKey",
                TEAMKEY = "teamKey",
                DETAILS = "details",
                YEAR = "year";

        @Override
        public long add(Media in) {
            try {
                if (!exists(in.getForeignKey())) {
                    return safeInsert(TABLE_MEDIAS, null, in.getParams());
                } else {
                    return update(in);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add media without Database.Medias.FOREIGNKEY");
                return -1;
            }
        }

        public void add(ArrayList<Media> medias) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Media media : medias) {
                    try {
                        if (!unsafeExists(media.getForeignKey())) {
                            db.insert(TABLE_MEDIAS, null, media.getParams());
                        } else {
                            db.update(TABLE_MEDIAS, media.getParams(), FOREIGNKEY + " = ?", new String[]{media.getForeignKey()});
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Can't update award. Missing fields");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        @Override
        public int update(Media in) {
            try {
                return safeUpdate(TABLE_MEDIAS, in.getParams(), Medias.FOREIGNKEY + " = ?", new String[]{in.getForeignKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update media without Database.Medias.FOREIGNKEY");
                return -1;
            }
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
            Cursor cursor = safeQuery(TABLE_MEDIAS, new String[]{}, Medias.FOREIGNKEY + "= ?", new String[]{key}, null, null, null, null);
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
            Cursor cursor = db.query(TABLE_MEDIAS, new String[]{}, Medias.FOREIGNKEY + "= ?", new String[]{key}, null, null, null, null);
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
            try {
                safeDelete(TABLE_MEDIAS, Medias.FOREIGNKEY + " = ? ", new String[]{in.getForeignKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete media without Database.Medias.FOREIGNKEY");
            }
        }
    }

    public class EventTeams implements ModelTable<EventTeam> {

        public static final String KEY = "key",
                TEAMKEY = "teamKey",
                EVENTKEY = "eventKey",
                YEAR = "year",
                COMPWEEK = "week";

        @Override
        public long add(EventTeam in) {
            try {
                if (!exists(in.getKey())) {
                    return safeInsert(TABLE_EVENTTEAMS, null, in.getParams());
                } else {
                    return update(in);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add media without Database.EventTeams.TEAMKEY and EVENTKEY");
                return -1;
            }
        }

        public void add(ArrayList<EventTeam> events) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (EventTeam eventTeam : events) {
                    try {
                        if (!unsafeExists(eventTeam.getKey())) {
                            db.insert(TABLE_EVENTTEAMS, null, eventTeam.getParams());
                        } else {
                            db.update(TABLE_EVENTTEAMS, eventTeam.getParams(), EventTeams.TEAMKEY + " = ? AND " + EventTeams.EVENTKEY + " + ?", new String[]{eventTeam.getTeamKey(), eventTeam.getEventKey()});
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Can't update eventTeam. Missing fields");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        @Override
        public int update(EventTeam in) {
            try {
                return safeUpdate(TABLE_EVENTTEAMS, in.getParams(), EventTeams.TEAMKEY + " = ? AND " + EventTeams.EVENTKEY + " + ?", new String[]{in.getTeamKey(), in.getEventKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update media without Database.EventTeams.TEAMKEY and EVENTKEY");
                return -1;
            }
        }

        @Override
        public EventTeam get(String key, String[] fields) {
            String eventKey = key.split("_")[0];
            String teamKey = key.split("_")[1];
            Cursor cursor = safeQuery(TABLE_EVENTTEAMS, fields, EventTeams.TEAMKEY + " = ? AND " + EventTeams.EVENTKEY + " + ?", new String[]{teamKey, eventKey}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                EventTeam eventTeam = ModelInflater.inflateEventTeam(cursor);
                cursor.close();
                return eventTeam;
            } else {
                return null;
            }
        }

        public ArrayList<EventTeam> get(String teamKey, int year, String[] fields) {
            Cursor cursor = safeQuery(TABLE_EVENTTEAMS, fields, EventTeams.TEAMKEY + " = ? AND " + EventTeams.YEAR + " + ?", new String[]{teamKey, Integer.toString(year)}, null, null, null, null);
            ArrayList<EventTeam> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    results.add(ModelInflater.inflateEventTeam(cursor));
                } while (cursor.moveToNext());
            }
            return results;
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_EVENTTEAMS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
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
            Cursor cursor = db.query(TABLE_EVENTTEAMS, new String[]{}, KEY + " = ?", new String[]{key}, null, null, null, null);
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
        public void delete(EventTeam in) {
            try {
                safeDelete(TABLE_EVENTTEAMS, TEAMKEY + " = ? AND " + EVENTKEY + " + ?", new String[]{in.getTeamKey(), in.getEventKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete eventTeam without Database.EventTeams.TEAMKEY and EVENTKEY");
            }
        }
    }

    public class Districts implements ModelTable<District> {

        public static final String KEY = "key",
                ABBREV = "abbrev",
                ENUM = "enum",
                YEAR = "year",
                NAME = "name";

        @Override
        public long add(District in) {
            try {
                if (!exists(in.getKey())) {
                    return safeInsert(TABLE_DISTRICTS, null, in.getParams());
                } else {
                    return update(in);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add District without KEY or ENUM+YEAR");
                return -1;
            }
        }

        public void add(ArrayList<District> districts) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (District district : districts) {
                    try {
                        if (!unsafeExists(district.getKey())) {
                            db.insert(TABLE_DISTRICTS, null, district.getParams());
                        } else {
                            db.update(TABLE_DISTRICTS, district.getParams(), Districts.KEY + " = ?", new String[]{district.getKey()});
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Can't update district. Missing fields");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        @Override
        public int update(District in) {
            try {
                return safeUpdate(TABLE_DISTRICTS, in.getParams(), Districts.KEY + " = ?", new String[]{in.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update district without KEY");
                return -1;
            }
        }

        @Override
        public District get(String key, String[] fields) {
            Cursor cursor = safeQuery(TABLE_DISTRICTS, fields, Districts.KEY + " = ?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                District district = ModelInflater.inflateDistrict(cursor);
                cursor.close();
                return district;
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_DISTRICTS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
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
            Cursor cursor = db.query(TABLE_DISTRICTS, new String[]{}, KEY + " = ?", new String[]{key}, null, null, null, null);
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
        public void delete(District in) {
            try {
                safeDelete(TABLE_DISTRICTS, KEY + " + ?", new String[]{in.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete district without KEY");
            }
        }
    }

    public class DistrictTeams implements ModelTable<DistrictTeam> {

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

        @Override
        public long add(DistrictTeam in) {
            try {
                if (!exists(in.getKey())) {
                    return safeInsert(TABLE_DISTRICTTEAMS, null, in.getParams());
                } else {
                    return update(in);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't add DistrictTeam without KEY");
                return -1;
            }
        }

        public void add(ArrayList<DistrictTeam> districts) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (DistrictTeam district : districts) {
                    try {
                        if (!unsafeExists(district.getKey())) {
                            db.insert(TABLE_DISTRICTTEAMS, null, district.getParams());
                        } else {
                            db.update(TABLE_DISTRICTTEAMS, district.getParams(), KEY + " = ?", new String[]{district.getKey()});
                        }
                    } catch (BasicModel.FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Can't update districtTeam. Missing fields");
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        @Override
        public int update(DistrictTeam in) {
            try {
                return safeUpdate(TABLE_DISTRICTTEAMS, in.getParams(), Districts.KEY + " = ?", new String[]{in.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't update districtTeam without KEY");
                return -1;
            }
        }

        @Override
        public DistrictTeam get(String key, String[] fields) {
            Cursor cursor = safeQuery(TABLE_DISTRICTTEAMS, fields, KEY + " = ?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                DistrictTeam districtTeam = ModelInflater.inflateDistrictTeam(cursor);
                cursor.close();
                return districtTeam;
            } else {
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_DISTRICTTEAMS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
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
            Cursor cursor = db.query(TABLE_DISTRICTTEAMS, new String[]{}, KEY + " = ?", new String[]{key}, null, null, null, null);
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
        public void delete(DistrictTeam in) {
            try {
                safeDelete(TABLE_DISTRICTTEAMS, KEY + " + ?", new String[]{in.getKey()});
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't delete districtTeam without KEY");
            }
        }
    }

    public class Favorites {
        public static final String KEY = "key",
                USER_NAME = "userName",
                MODEL_KEY = "modelKey",
                MODEL_ENUM = "model_enum";

        public long add(Favorite in) {
            if (!exists(in.getKey())) {
                return safeInsert(TABLE_FAVORITES, null, in.getParams(), getMyTBASemaphore());
            }
            return -1;
        }

        public void add(ArrayList<Favorite> in) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getMyTBASemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Favorite favorite : in) {
                    if (!unsafeExists(favorite.getKey())) {
                        db.insert(TABLE_FAVORITES, null, favorite.getParams());
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
                e.printStackTrace();
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        public void remove(String key) {
            safeDelete(TABLE_FAVORITES, KEY + " = ?", new String[]{key}, getMyTBASemaphore());
        }

        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null, getMyTBASemaphore());
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
            Cursor cursor = safeQuery(TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null, getMyTBASemaphore());
            if (cursor != null && cursor.moveToFirst()) {
                return ModelInflater.inflateFavorite(cursor);
            }
            return null;
        }

        public boolean unsafeExists(String key) {
            Cursor cursor = db.query(TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null);
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
            Cursor cursor = safeQuery(TABLE_FAVORITES, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null, getMyTBASemaphore());
            ArrayList<Favorite> favorites = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    favorites.add(ModelInflater.inflateFavorite(cursor));
                } while (cursor.moveToNext());
            }
            return favorites;
        }

        public void recreate(String user) {
            safeDelete(TABLE_FAVORITES, USER_NAME + " = ?", new String[]{user}, getMyTBASemaphore());
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
                return safeInsert(TABLE_SUBSCRIPTIONS, null, in.getParams(), getMyTBASemaphore());
            } else {
                return update(in.getKey(), in);
            }
        }

        public int update(String key, Subscription in) {
            return safeUpdate(TABLE_SUBSCRIPTIONS, in.getParams(), KEY + " = ?", new String[]{key}, getMyTBASemaphore());
        }

        public void add(ArrayList<Subscription> in) {
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getMyTBASemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (Subscription subscription : in) {
                    if (!unsafeExists(subscription.getKey())) {
                        db.insert(TABLE_SUBSCRIPTIONS, null, subscription.getParams());
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
                e.printStackTrace();
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }

        public boolean exists(String key) {
            Cursor cursor = safeQuery(TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null, getMyTBASemaphore());
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
            Cursor cursor = db.query(TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
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
            safeDelete(TABLE_SUBSCRIPTIONS, KEY + " = ?", new String[]{key}, getMyTBASemaphore());
        }

        public Subscription get(String key) {
            Cursor cursor = safeQuery(TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null, getMyTBASemaphore());
            if (cursor != null && cursor.moveToFirst()) {
                return ModelInflater.inflateSubscription(cursor);
            }
            return null;
        }

        public ArrayList<Subscription> getForUser(String user) {
            Cursor cursor = safeQuery(TABLE_SUBSCRIPTIONS, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null, getMyTBASemaphore());
            ArrayList<Subscription> subscriptions = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    subscriptions.add(ModelInflater.inflateSubscription(cursor));
                } while (cursor.moveToNext());
            }
            return subscriptions;
        }

        public void recreate(String user) {
            safeDelete(TABLE_SUBSCRIPTIONS, USER_NAME + " = ?", new String[]{user}, getMyTBASemaphore());
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
    
    public class Notifications{
        public static final String
                ID = "id",
                TYPE = "type",
                TITLE = "title",
                BODY = "body",
                INTENT = "intent",
                TIME = "time";
        
        public void add(StoredNotification... in){
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                for (StoredNotification notification : in) {
                    db.insert(TABLE_NOTIFICATIONS, null, notification.getParams());
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }
        
        public ArrayList<StoredNotification> get(){
            ArrayList<StoredNotification> out = new ArrayList<>();
            Cursor cursor = safeRawQuery("SELECT * FROM "+TABLE_NOTIFICATIONS+" ORDER BY "+ID+" DESC", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    out.add(ModelInflater.inflateStoredNotification(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return out;
        }
        
        private void delete(int id){
            safeDelete(TABLE_NOTIFICATIONS, ID + " = ? ", new String[]{Integer.toString(id)});
        }
        
        // Only allow 100 notificanulltions to be stored
        public void prune(){
            Semaphore dbSemaphore = null;
            try {
                dbSemaphore = getSemaphore();
                dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                db.beginTransaction();
                Cursor cursor = db.query(TABLE_NOTIFICATIONS, new String[]{ID}, "", new String[]{}, null, null, ID + " ASC", null);
                if(cursor != null && cursor.moveToFirst()){
                    for(int i=cursor.getCount(); i>100; i--){
                        db.delete(TABLE_NOTIFICATIONS, ID + " = ?", new String[]{cursor.getString(cursor.getColumnIndex(ID))});
                        if(!cursor.moveToNext()){
                            break;
                        }
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (InterruptedException e) {
                Log.w("database", "Unable to acquire database semaphore");
            } finally {
                if (dbSemaphore != null) {
                    dbSemaphore.release();
                }
            }
        }
    }

    public Cursor safeQuery(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return safeQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, null);
    }

    public Cursor safeQuery(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, Semaphore semaphore) {
        if (semaphore == null) {
            semaphore = getSemaphore();
        }
        Cursor cursor = null;
        try {
            semaphore.tryAcquire(10, TimeUnit.SECONDS);
            cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to acquire database semaphore");
        } finally {
            if (semaphore != null) {
                semaphore.release();
            }
        }
        return cursor;
    }

    public Cursor safeRawQuery(String query, String[] args) {
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try {
            dbSemaphore = getSemaphore();
            dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
            cursor = db.rawQuery(query, args);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to acquire database semaphore");
        } finally {
            if (dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }

    public int safeUpdate(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return safeUpdate(table, values, whereClause, whereArgs, null);
    }

    public int safeUpdate(String table, ContentValues values, String whereClause, String[] whereArgs, Semaphore semaphore) {
        if (semaphore == null) {
            semaphore = getSemaphore();
        }
        int response = -1;
        try {
            semaphore = getSemaphore();
            semaphore.tryAcquire(10, TimeUnit.SECONDS);
            response = db.update(table, values, whereClause, whereArgs);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        } finally {
            if (semaphore != null) {
                semaphore.release();
            }
        }
        return response;
    }

    public long safeInsert(String table, String nullColumnHack, ContentValues values) {
        return safeInsert(table, nullColumnHack, values, null);
    }

    public long safeInsert(String table, String nullColumnHack, ContentValues values, Semaphore semaphore) {
        if (semaphore == null) {
            semaphore = getSemaphore();
        }
        long response = -1;
        try {
            semaphore.tryAcquire(10, TimeUnit.SECONDS);
            response = db.insert(table, nullColumnHack, values);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to acquire database semaphore");
        } finally {
            if (semaphore != null) {
                semaphore.release();
            }
        }
        return response;
    }

    public int safeDelete(String table, String whereClause, String[] whereArgs){
        return safeDelete(table, whereClause, whereArgs, null);
    }

    public int safeDelete(String table, String whereClause, String[] whereArgs, Semaphore semaphore) {
        if (semaphore == null) {
            semaphore = getSemaphore();
        }
        int response = -1;
        try {
            semaphore.tryAcquire(10, TimeUnit.SECONDS);
            response = db.delete(table, whereClause, whereArgs);
        } catch (InterruptedException e) {
            Log.w("database", "Unable to acquire database semaphore");
        } finally {
            if (semaphore != null) {
                semaphore.release();
            }
        }
        return response;
    }

    public long insertSearchItemTeam(Team team) {
        return insertSearchItemTeam(team, true);
    }

    public long insertSearchItemTeam(Team team, boolean safe) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(SearchTeam.KEY, team.getTeamKey());
            cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
            cv.put(SearchTeam.NUMBER, team.getTeamNumber());
            return safe ? safeInsert(TABLE_SEARCH_TEAMS, null, cv) : db.insert(TABLE_SEARCH_TEAMS, null, cv);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert search team without the following fields:" +
                    "Database.Teams.KEY, Database.Teams.NUMBER");
            return -1;
        }
    }

    public long insertSearchItemEvent(Event event) {
        return insertSearchItemEvent(event, true);
    }

    public long insertSearchItemEvent(Event event, boolean safe) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(SearchEvent.KEY, event.getEventKey());
            cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(SearchEvent.YEAR, event.getEventYear());
            return safe ? safeInsert(TABLE_SEARCH_EVENTS, null, cv) : db.insert(TABLE_SEARCH_EVENTS, null, cv);

        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                    "Database.Events.KEY, Database.Events.YEAR");
            return -1;
        } catch (SQLiteException e) {
            return updateSearchItemEvent(event, safe);
        }
    }

    public long updateSearchItemTeam(Team team) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(SearchTeam.KEY, team.getTeamKey());
            cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
            cv.put(SearchTeam.NUMBER, team.getTeamNumber());
            return db.update(TABLE_SEARCH_TEAMS, cv, SearchTeam.KEY + "=?", new String[]{team.getTeamKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                    "Database.Events.KEY, Database.Events.YEAR");
            return -1;
        }
    }

    public long updateSearchItemEvent(Event event) {
        return updateSearchItemEvent(event, true);
    }

    public long updateSearchItemEvent(Event event, boolean safe) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(SearchEvent.KEY, event.getEventKey());
            cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(SearchEvent.YEAR, event.getEventYear());

            return safe ? safeUpdate(TABLE_SEARCH_EVENTS, cv, SearchEvent.KEY + "=?", new String[]{event.getEventKey()}) :
                    db.update(TABLE_SEARCH_EVENTS, cv, SearchEvent.KEY + "=?", new String[]{event.getEventKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                    "Database.Events.KEY, Database.Events.YEAR");
            return -1;
        }
    }

    public void deleteSearchItemTeam(Team team) {
        try {
            safeDelete(TABLE_SEARCH_TEAMS, SearchTeam.KEY + " = ?", new String[]{team.getTeamKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't delete search team without Database.Teams.KEY");
        }
    }

    public void deleteSearchItemEvent(Event event) {
        try {
            safeDelete(TABLE_SEARCH_EVENTS, SearchEvent.KEY + " = ?", new String[]{event.getEventKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't delete search event without Database.Events.KEY");
        }
    }

    public Cursor getMatchesForTeamQuery(String query) {
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try {
            dbSemaphore = getSemaphore();
            dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
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
        } finally {
            if (dbSemaphore != null) {
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
            dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
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
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        } finally {
            if (dbSemaphore != null) {
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
            dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
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
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        } finally {
            if (dbSemaphore != null) {
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
            dbSemaphore.tryAcquire(10, TimeUnit.SECONDS);
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
        } catch (InterruptedException e) {
            Log.w("database", "Unable to aquire database semaphore");
        } finally {
            if (dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }
}
