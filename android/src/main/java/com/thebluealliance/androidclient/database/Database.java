package com.thebluealliance.androidclient.database;

import com.thebluealliance.androidclient.TbaLogger;
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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.VisibleForTesting;


//SUPPRESS CHECKSTYLE FinalClass
public class Database extends SQLiteOpenHelper {

    public static final String ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE = "all_teams_loaded_for_page_";
    public static final String ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";
    public static final String ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR = "all_districts_loaded_for_year_";

    static final int DATABASE_VERSION = 31;
    private static final String DATABASE_NAME = "the-blue-alliance-android-database";
    static final @Deprecated String TABLE_API = "api";
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

    public static final String CREATE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + "("
            + TeamsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + TeamsTable.NUMBER + " INTEGER NOT NULL, "
            + TeamsTable.NAME + " TEXT DEFAULT '', "
            + TeamsTable.SHORTNAME + " TEXT DEFAULT '', "
            + TeamsTable.LOCATION + " TEXT DEFAULT '',"
            + TeamsTable.WEBSITE + " TEXT DEFAULT '', "
            + TeamsTable.YEARS_PARTICIPATED + " TEXT DEFAULT '', "
            + TeamsTable.MOTTO + " TEXT DEFAULT '', "
            + TeamsTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
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
            + EventsTable.WEBSITE + " TEXT DEFAULT '', "
            + EventsTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_AWARDS = "CREATE TABLE IF NOT EXISTS " + TABLE_AWARDS + "("
            + AwardsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + AwardsTable.ENUM + " INTEGER DEFAULT -1, "
            + AwardsTable.EVENTKEY + " TEXT DEFAULT '', "
            + AwardsTable.NAME + " TEXT DEFAULT '', "
            + AwardsTable.YEAR + " INTEGER DEFAULT -1, "
            + AwardsTable.WINNERS + " TEXT DEFAULT '', "
            + AwardsTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_MATCHES = "CREATE TABLE IF NOT EXISTS " + TABLE_MATCHES + "("
            + MatchesTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + MatchesTable.SETNUM + " INTEGER DEFAULT -1,"
            + MatchesTable.MATCHNUM + " INTEGER DEFAULT -1,"
            + MatchesTable.EVENT + " TEXT DEFAULT '', "
            + MatchesTable.TIMESTRING + " TEXT DEFAULT '', "
            + MatchesTable.TIME + " TIMESTAMP, "
            + MatchesTable.ALLIANCES + " TEXT DEFAULT '', "
            + MatchesTable.VIDEOS + " TEXT DEFAULT '', "
            + MatchesTable.BREAKDOWN + " TEXT DEFAULT '', "
            + MatchesTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_MEDIAS = "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIAS + "("
            + MediasTable.TYPE + " TEXT DEFAULT '', "
            + MediasTable.FOREIGNKEY + " TEXT DEFAULT '', "
            + MediasTable.TEAMKEY + " TEXT DEFAULT '', "
            + MediasTable.DETAILS + " TEXT DEFAULT '', "
            + MediasTable.YEAR + " INTEGER  DEFAULT -1, "
            + MediasTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_EVENTTEAMS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EVENTTEAMS + "("
            + EventTeamsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + EventTeamsTable.TEAMKEY + " TEXT DEFAULT '', "
            + EventTeamsTable.EVENTKEY + " TEXT DEFAULT '', "
            + EventTeamsTable.YEAR + " INTEGER DEFAULT -1, "
            + EventTeamsTable.COMPWEEK + " INTEGER DEFAULT -1, "
            + EventTeamsTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_DISTRICTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_DISTRICTS + "("
            + DistrictsTable.KEY + " TEXT PRIMARY KEY NOT NULL, "
            + DistrictsTable.ABBREV + " TEXT NOT NULL, "
            + DistrictsTable.YEAR + " INTEGER NOT NULL, "
            + DistrictsTable.ENUM + " INTEGER NOT NULL,"
            + DistrictsTable.NAME + " TEXT DEFAULT '', "
            + DistrictsTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_DISTRICTTEAMS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_DISTRICTTEAMS + "("
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
            + DistrictTeamsTable.JSON + " TEXT DEFAULT '', "
            + DistrictTeamsTable.LAST_MODIFIED + " TIMESTAMP"
            + ")";
    public static final String CREATE_FAVORITES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_FAVORITES + "("
            + FavoritesTable.KEY + " TEXT PRIMARY KEY NOT NULL,"
            + FavoritesTable.USER_NAME + " TEXT NOT NULL, "
            + FavoritesTable.MODEL_KEY + " TEXT NOT NULL,"
            + FavoritesTable.MODEL_ENUM + " INTEGER NOT NULL"
            + ")";
    public static final String CREATE_SUBSCRIPTIONS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SUBSCRIPTIONS + "("
            + SubscriptionsTable.KEY + " TEXT PRIMARY KEY NOT NULL,"
            + SubscriptionsTable.USER_NAME + " TEXT NOT NULL,"
            + SubscriptionsTable.MODEL_KEY + " TEXT NOT NULL,"
            + SubscriptionsTable.MODEL_ENUM + " INTEGER NOT NULL,"
            + SubscriptionsTable.NOTIFICATION_SETTINGS + " TEXT DEFAULT '[]'"
            + ")";
    public static final String CREATE_SEARCH_TEAMS = "CREATE VIRTUAL TABLE "
            + TABLE_SEARCH_TEAMS + " USING fts3 ("
            + SearchTeam.KEY + " TEXT PRIMARY KEY, "
            + SearchTeam.TITLES + " TEXT, "
            + SearchTeam.NUMBER + " TEXT, "
            + ")";

    public static final String CREATE_SEARCH_EVENTS = "CREATE VIRTUAL TABLE "
            + TABLE_SEARCH_EVENTS + " USING fts3 ("
            + SearchEvent.KEY + " TEXT PRIMARY KEY, "
            + SearchEvent.TITLES + " TEXT, "
            + SearchEvent.YEAR + " TEXT,  "
            + ")";

    public static final String CREATE_NOTIFICATIONS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NOTIFICATIONS + "("
            + NotificationsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + NotificationsTable.TYPE + " TEXT NOT NULL, "
            + NotificationsTable.TITLE + " TEXT DEFAULT '', "
            + NotificationsTable.BODY + " TEXT DEFAULT '', "
            + NotificationsTable.INTENT + " TEXT DEFAULT '', "
            + NotificationsTable.TIME + " TIMESTAMP, "
            + NotificationsTable.SYSTEM_ID + " INTEGER NOT NULL, "
            + NotificationsTable.ACTIVE + " INTEGER DEFAULT 1, "
            + NotificationsTable.MSG_DATA + " TEXT DEFAULT '')";

    protected SQLiteDatabase mDb;

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
        Database databaseInstance = new Database(context.getApplicationContext());
        databaseInstance.setWriteAheadLoggingEnabled(true);
        return databaseInstance;
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


        if (!tableExists(db, TABLE_SEARCH_EVENTS)) {
            db.execSQL(CREATE_SEARCH_EVENTS);
        }
        if (!tableExists(db, TABLE_SEARCH_TEAMS)) {
            db.execSQL(CREATE_SEARCH_TEAMS);
        }
    }

    @VisibleForTesting
    boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT 1 FROM sqlite_master WHERE type = ? AND name = ?",
                                    new String[] {"table", tableName});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    @VisibleForTesting
    boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        if (tableName == null || db == null || columnName == null) {
            return false;
        }
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if (cursor.moveToFirst()) {
            do {
                int value = cursor.getColumnIndex("name");
                if (value != -1 && cursor.getString(value).equals(columnName)) {
                    cursor.close();
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TbaLogger.w("Upgrading database from version " + oldVersion + " to " + newVersion);

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 14:
                    //add districts tables
                    db.execSQL(CREATE_DISTRICTS);
                    db.execSQL(CREATE_DISTRICTTEAMS);
                    if (!columnExists(db, TABLE_EVENTS, EventsTable.DISTRICT_POINTS)) {
                        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN "
                                   + EventsTable.DISTRICT_POINTS + " TEXT DEFAULT '' ");
                    }
                    break;
                case 15:
                    //add favorites and subscriptions
                    db.execSQL(CREATE_FAVORITES);
                    db.execSQL(CREATE_SUBSCRIPTIONS);
                    break;
                case 16:
                    // add column for individual notification settings and sorting by model type
                    if (!columnExists(db, TABLE_SUBSCRIPTIONS, SubscriptionsTable.NOTIFICATION_SETTINGS)) {
                        db.execSQL("ALTER TABLE " + TABLE_SUBSCRIPTIONS + " ADD COLUMN "
                                   + SubscriptionsTable.NOTIFICATION_SETTINGS + " TEXT DEFAULT '[]' ");
                    }
                    if (!columnExists(db, TABLE_SUBSCRIPTIONS, SubscriptionsTable.MODEL_ENUM)) {
                        db.execSQL("ALTER TABLE " + TABLE_SUBSCRIPTIONS + " ADD COLUMN "
                                   + SubscriptionsTable.MODEL_ENUM + " INTEGER NOT NULL DEFAULT -1");
                    }
                    if (!columnExists(db, TABLE_FAVORITES, FavoritesTable.MODEL_ENUM)) {
                        db.execSQL("ALTER TABLE " + TABLE_FAVORITES + " ADD COLUMN "
                                    + FavoritesTable.MODEL_ENUM + " INTEGER NOT NULL DEFAULT -1");
                    }
                    break;
                case 17:
                    // add column for district name
                    if (!columnExists(db, TABLE_DISTRICTS, DistrictsTable.NAME)) {
                        db.execSQL("ALTER TABLE " + TABLE_DISTRICTS + " ADD COLUMN " + DistrictsTable.NAME + " TEXT DEFAULT '' ");
                    }
                    break;
                case 18:
                    // add column for event short name
                    if (!columnExists(db, TABLE_EVENTS, EventsTable.SHORTNAME)) {
                        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EventsTable.SHORTNAME + " TEXT DEFAULT '' ");
                    }
                    break;
                case 20:
                    // Create table for recent notification
                    db.execSQL(CREATE_NOTIFICATIONS);
                    break;
                case 23:
                case 24:
                    // remove and recreate search indexes to we can create them with foreign keys
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_TEAMS);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_EVENTS);
                    onCreate(db);
                    break;
                case 25:
                    // delete deprecated responses table
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_API);
                    break;
                case 28:
                    // recreate stored notifications table
                    db.beginTransaction();
                    try {
                        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
                        db.execSQL(CREATE_NOTIFICATIONS);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
                case 29:
                    // Add team motto
                    db.beginTransaction();
                    try {
                        if (!columnExists(db, TABLE_TEAMS, TeamsTable.MOTTO)) {
                            db.execSQL("ALTER TABLE " + TABLE_TEAMS + " ADD COLUMN " + TeamsTable.MOTTO + " TEXT DEFAULT '' ");
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
                case 30:
                    // Add match breakdown
                    db.beginTransaction();
                    try {
                        if (!columnExists(db, TABLE_MATCHES, MatchesTable.BREAKDOWN)) {
                            db.execSQL("ALTER TABLE " + TABLE_MATCHES + " ADD COLUMN " + MatchesTable.BREAKDOWN + " TEXT DEFAULT '' ");
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
                case 31:
                    // Add last_modified columns
                    String[] tables = {TABLE_AWARDS, TABLE_DISTRICTS, TABLE_DISTRICTTEAMS,
                            TABLE_EVENTS, TABLE_EVENTTEAMS, TABLE_MATCHES, TABLE_MEDIAS,
                            TABLE_TEAMS};
                    db.beginTransaction();
                    try {
                        for (int i = 0; i < tables.length; i++) {
                            if (!columnExists(db, tables[i], "last_modified")) {
                                db.execSQL(String.format("ALTER TABLE %1$s ADD COLUMN last_modified TIMESTAMP", tables[i]));
                            }
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
            }
            upgradeTo++;
        }
    }

    public final class SearchTeam {
        public static final String
                KEY = "key",
                TITLES = "titles",
                NUMBER = "number";

        private SearchTeam() {
            // unused
        }
    }

    public final class SearchEvent {
        public static final String
                KEY = "key",
                TITLES = "titles",
                YEAR = "year";

        private SearchEvent() {
            // unused
        }
    }

    public Cursor getMatchesForTeamQuery(String query) {
        String selection = SearchTeam.TITLES + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_SEARCH_TEAMS);
        builder.setDistinct(true);

        Cursor cursor = builder.query(mDb,
                new String[]{SearchTeam.KEY + " as _id", SearchTeam.TITLES, SearchTeam.NUMBER}, selection, selectionArgs, null, null, SearchTeam.NUMBER + " ASC");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getMatchesForEventQuery(String query) {
        String selection = SearchEvent.TITLES + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_SEARCH_EVENTS);
        builder.setDistinct(true);

        Cursor cursor = builder.query(mDb,
                new String[]{SearchEvent.KEY + " as _id", SearchEvent.TITLES, SearchEvent.YEAR}, selection, selectionArgs, null, null, SearchEvent.YEAR + " DESC");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
