package com.thebluealliance.androidclient.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.DataManager;

import java.util.Map;


public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 25;
    private Context context;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database";
    public static final @Deprecated String TABLE_API = "api";
    public static final String TABLE_TEAMS = "teams",
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

    String CREATE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + "("
            + TeamsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + TeamsTable.NUMBER + " INTEGER NOT NULL, "
            + TeamsTable.NAME + " TEXT DEFAULT '', "
            + TeamsTable.SHORTNAME + " TEXT DEFAULT '', "
            + TeamsTable.LOCATION + " TEXT DEFAULT '',"
            + TeamsTable.WEBSITE + " TEXT DEFAULT '', "
            + TeamsTable.YEARS_PARTICIPATED + " TEXT DEFAULT '' "
            + ")";
    String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
            + EventsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + EventsTable.YEAR + " INTEGER NOT NULL, "
            + EventsTable.NAME + " TEXT DEFAULT '', "
            + EventsTable.SHORTNAME + " TEXT DEFAULT '', "
            + EventsTable.LOCATION + " TEXT DEFAULT '', "
            + EventsTable.VENUE + " TEXT DEFAULT '', "
            + EventsTable.TYPE + " INTEGER DEFAULT -1, "
            + EventsTable.DISTRICT + " INTEGER DEFAULT -1, "
            + EventsTable.DISTRICT_STRING + " TEXT DEFAULT '', "
            + EventsTable.DISTRICT_POINTS + " TEXT DEFAULT '', "
            + EventsTable.START + " TIMESTAMP, "
            + EventsTable.END + " TIMESTAMP, "
            + EventsTable.OFFICIAL + " INTEGER DEFAULT 0, "
            + EventsTable.WEEK + " INTEGER DEFAULT -1, "
            + EventsTable.TEAMS + " TEXT DEFAULT '', "
            + EventsTable.RANKINGS + " TEXT DEFAULT '', "
            + EventsTable.ALLIANCES + " TEXT DEFAULT '', "
            + EventsTable.WEBCASTS + " TEXT DEFAULT '', "
            + EventsTable.STATS + " TEXT DEFAULT '', "
            + EventsTable.WEBSITE + " TEXT DEFAULT '' "
            + ")";
    String CREATE_AWARDS = "CREATE TABLE IF NOT EXISTS " + TABLE_AWARDS + "("
            + AwardsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + AwardsTable.ENUM + " INTEGER DEFAULT -1, "
            + AwardsTable.EVENTKEY + " TEXT DEFAULT '', "
            + AwardsTable.NAME + " TEXT DEFAULT '', "
            + AwardsTable.YEAR + " INTEGER DEFAULT -1, "
            + AwardsTable.WINNERS + " TEXT DEFAULT '' "
            + ")";
    String CREATE_MATCHES = "CREATE TABLE IF NOT EXISTS " + TABLE_MATCHES + "("
            + MatchesTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + MatchesTable.SETNUM + " INTEGER DEFAULT -1,"
            + MatchesTable.MATCHNUM + " INTEGER DEFAULT -1,"
            + MatchesTable.EVENT + " TEXT DEFAULT '', "
            + MatchesTable.TIMESTRING + " TEXT DEFAULT '', "
            + MatchesTable.TIME + " TIMESTAMP, "
            + MatchesTable.ALLIANCES + " TEXT DEFAULT '', "
            + MatchesTable.VIDEOS + " TEXT DEFAULT '' "
            + ")";
    String CREATE_MEDIAS = "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIAS + "("
            + MediasTable.TYPE + " TEXT DEFAULT '', "
            + MediasTable.FOREIGNKEY + " TEXT DEFAULT '', "
            + MediasTable.TEAMKEY + " TEXT DEFAULT '', "
            + MediasTable.DETAILS + " TEXT DEFAULT '', "
            + MediasTable.YEAR + " INTEGER  DEFAULT -1"
            + ")";
    String CREATE_EVENTTEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTTEAMS + "("
            + EventTeamsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + EventTeamsTable.TEAMKEY + " TEXT DEFAULT '', "
            + EventTeamsTable.EVENTKEY + " TEXT DEFAULT '', "
            + EventTeamsTable.YEAR + " INTEGER DEFAULT -1, "
            + EventTeamsTable.COMPWEEK + " INTEGER DEFAULT -1 "
            + ")";
    String CREATE_DISTRICTS = "CREATE TABLE IF NOT EXISTS " + TABLE_DISTRICTS + "("
            + DistrictsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + DistrictsTable.ABBREV + " TEXT NOT NULL, "
            + DistrictsTable.YEAR + " INTEGER NOT NULL, "
            + DistrictsTable.ENUM + " INTEGER NOT NULL,"
            + DistrictsTable.NAME + " TEXT DEFAULT ''"
            + ")";
    String CREATE_DISTRICTTEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_DISTRICTTEAMS + "("
            + DistrictTeamsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + DistrictTeamsTable.TEAM_KEY + " TEXT NOT NULL, "
            + DistrictTeamsTable.DISTRICT_KEY + " TEXT NOT NULL, "
            + DistrictTeamsTable.DISTRICT_ENUM + " INTEGER NOT NULL, "
            + DistrictTeamsTable.YEAR + " INTEGER NOT NULL, "
            + DistrictTeamsTable.RANK + " INTEGER DEFAULT -1, "
            + DistrictTeamsTable.EVENT1_KEY + " TEXT DEFAULT '', "
            + DistrictTeamsTable.EVENT1_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeamsTable.EVENT2_KEY + " TEXT DEFAULT '', "
            + DistrictTeamsTable.EVENT2_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeamsTable.CMP_KEY + " TEXT DEFAULT '', "
            + DistrictTeamsTable.CMP_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeamsTable.ROOKIE_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeamsTable.TOTAL_POINTS + " INTEGER DEFAULT 0, "
            + DistrictTeamsTable.JSON + " TEXT DEFAULT '' "
            + ")";
    String CREATE_FAVORITES = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + "("
            + FavoritesTable.KEY + " TEXT PRIMARY KEY NOT NULL,"
            + FavoritesTable.USER_NAME + " TEXT NOT NULL, "
            + FavoritesTable.MODEL_KEY + " TEXT NOT NULL,"
            + FavoritesTable.MODEL_ENUM + " INTEGER NOT NULL"
            + ")";
    String CREATE_SUBSCRIPTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBSCRIPTIONS + "("
            + SubscriptionsTable.KEY + " TEXT PRIMARY KEY NOT NULL,"
            + SubscriptionsTable.USER_NAME + " TEXT NOT NULL,"
            + SubscriptionsTable.MODEL_KEY + " TEXT NOT NULL,"
            + SubscriptionsTable.MODEL_ENUM + " INTEGER NOT NULL,"
            + SubscriptionsTable.NOTIFICATION_SETTINGS + " TEXT DEFAULT '[]'"
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
            NotificationsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            NotificationsTable.TYPE + " TEXT NOT NULL, " +
            NotificationsTable.TITLE + " TEXT DEFAULT '', " +
            NotificationsTable.BODY + " TEXT DEFAULT '', " +
            NotificationsTable.INTENT + " TEXT DEFAULT '', " +
            NotificationsTable.TIME + " TIMESTAMP, " +
            NotificationsTable.SYSTEM_ID + " INTEGER NOT NULL, " +
            NotificationsTable.ACTIVE + " INTEGER DEFAULT 1 )";

    protected SQLiteDatabase mDb;
    private static Database sDatabaseInstance;

    private TeamsTable mTeamsTable;
    private EventsTable mEventsTable;
    private AwardsTable mAwardsTable;
    private MatchesTable mMatchesTable;
    private MediasTable mMediasTable;
    private EventTeamsTable mEventTeamsTable;
    private DistrictsTable mDistrictsTable;
    private DistrictTeamsTable mDistrictTeamsTable;
    private FavoritesTable mFavoritesTable;
    private SubscriptionsTable mSubscriptionsTable;
    private NotificationsTable mNotificationsTable;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        mDb = getWritableDatabase();
        mTeamsTable = new TeamsTable(mDb);
        mEventsTable = new EventsTable(mDb);
        mAwardsTable = new AwardsTable(mDb);
        mMatchesTable = new MatchesTable(mDb);
        mMediasTable = new MediasTable(mDb);
        mEventTeamsTable = new EventTeamsTable(mDb);
        mDistrictsTable = new DistrictsTable(mDb);
        mDistrictTeamsTable = new DistrictTeamsTable(mDb);
        mFavoritesTable = new FavoritesTable(mDb);
        mSubscriptionsTable = new SubscriptionsTable(mDb);
        mNotificationsTable = new NotificationsTable(mDb);
    }

    public static synchronized Database getInstance(Context context) {
        if (sDatabaseInstance == null) {
            sDatabaseInstance = new Database(context);
            sDatabaseInstance.setWriteAheadLoggingEnabled(true);
        }
        return sDatabaseInstance;
    }

    public TeamsTable getTeamsTable() {
        return mTeamsTable;
    }

    public EventsTable getEventsTable() {
        return mEventsTable;
    }

    public AwardsTable getAwardsTable() {
        return mAwardsTable;
    }

    public MatchesTable getMatchesTable() {
        return mMatchesTable;
    }

    public MediasTable getMediasTable() {
        return mMediasTable;
    }

    public EventTeamsTable getEventTeamsTable() {
        return mEventTeamsTable;
    }

    public DistrictsTable getDistrictsTable() {
        return mDistrictsTable;
    }

    public DistrictTeamsTable getDistrictTeamsTable() {
        return mDistrictTeamsTable;
    }

    public FavoritesTable getFavoritesTable() {
        return mFavoritesTable;
    }

    public SubscriptionsTable getSubscriptionsTable() {
        return mSubscriptionsTable;
    }

    public NotificationsTable getNotificationsTable() {
        return mNotificationsTable;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
                    db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EventsTable.DISTRICT_POINTS + " TEXT DEFAULT '' ");
                    break;
                case 15:
                    //add favorites and subscriptions
                    db.execSQL(CREATE_FAVORITES);
                    db.execSQL(CREATE_SUBSCRIPTIONS);
                    break;
                case 16:
                    // add column for individual notification settings and sorting by model type
                    Cursor sub = db.rawQuery("SELECT * FROM " + TABLE_SUBSCRIPTIONS + " LIMIT 0,1", null);
                    if (sub.getColumnIndex(SubscriptionsTable.NOTIFICATION_SETTINGS) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_SUBSCRIPTIONS + " ADD COLUMN " + SubscriptionsTable.NOTIFICATION_SETTINGS + " TEXT DEFAULT '[]' ");
                    }
                    if (sub.getColumnIndex(SubscriptionsTable.MODEL_ENUM) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_SUBSCRIPTIONS + " ADD COLUMN " + SubscriptionsTable.MODEL_ENUM + " INTEGER NOT NULL");
                    }
                    sub.close();
                    Cursor fav = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " LIMIT 0,1", null);
                    if (fav.getColumnIndex(FavoritesTable.MODEL_ENUM) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_FAVORITES + " ADD COLUMN " + FavoritesTable.MODEL_ENUM + " INTEGER NOT NULL");
                    }
                    fav.close();
                    break;
                case 17:
                    // add column for district name
                    Cursor dist = db.rawQuery("SELECT * FROM " + TABLE_DISTRICTS + " LIMIT 0,1", null);
                    if (dist.getColumnIndex(DistrictsTable.NAME) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_DISTRICTS + " ADD COLUMN " + DistrictsTable.NAME + " TEXT DEFAULT '' ");
                    }
                    dist.close();
                    break;
                case 18:
                    // add column for event short name
                    Cursor event = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " LIMIT 0,1", null);
                    if (event.getColumnIndex(EventsTable.SHORTNAME) == -1) {
                        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EventsTable.SHORTNAME + " TEXT DEFAULT '' ");
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
                case 25:
                    // delete deprecated responses table
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_API);
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
                + TeamsTable.KEY + ","
                + TeamsTable.NUMBER + ","
                + TeamsTable.NAME + ","
                + TeamsTable.SHORTNAME + ","
                + TeamsTable.LOCATION
                + " FROM " + TABLE_TEAMS + " JOIN tempteams ON tempteams.tempkey = " + TABLE_TEAMS + "." + TeamsTable.KEY + " ORDER BY ? ASC", new String[]{TeamsTable.NUMBER});

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
                        + EventsTable.KEY + ","
                        + EventsTable.NAME + ","
                        + EventsTable.SHORTNAME + ","
                        + EventsTable.TYPE + ","
                        + EventsTable.DISTRICT + ","
                        + EventsTable.START + ","
                        + EventsTable.END + ","
                        + EventsTable.LOCATION + ","
                        + EventsTable.VENUE + ","
                        + EventsTable.OFFICIAL + ","
                        + EventsTable.DISTRICT_STRING
                        + " FROM " + TABLE_EVENTS + " JOIN tempevents ON tempevents.tempkey = " + TABLE_EVENTS + "." + EventsTable.KEY + " ORDER BY ? DESC", new String[]{SearchEvent.YEAR}
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
