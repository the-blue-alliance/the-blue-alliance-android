package com.thebluealliance.androidclient.database;

import android.database.sqlite.SQLiteDatabase;

import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.thebluealliance.androidclient.database.Database.TABLE_API;
import static com.thebluealliance.androidclient.database.Database.TABLE_AWARDS;
import static com.thebluealliance.androidclient.database.Database.TABLE_DISTRICTS;
import static com.thebluealliance.androidclient.database.Database.TABLE_DISTRICTTEAMS;
import static com.thebluealliance.androidclient.database.Database.TABLE_EVENTS;
import static com.thebluealliance.androidclient.database.Database.TABLE_EVENTTEAMS;
import static com.thebluealliance.androidclient.database.Database.TABLE_FAVORITES;
import static com.thebluealliance.androidclient.database.Database.TABLE_MATCHES;
import static com.thebluealliance.androidclient.database.Database.TABLE_MEDIAS;
import static com.thebluealliance.androidclient.database.Database.TABLE_NOTIFICATIONS;
import static com.thebluealliance.androidclient.database.Database.TABLE_SUBSCRIPTIONS;
import static com.thebluealliance.androidclient.database.Database.TABLE_TEAMS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private static final String BASE_TABLE_CREATE = "CREATE TABLE %1$s (id INTEGER PRIMARY KEY);";
    private static final String[] TABLES = {TABLE_AWARDS, TABLE_DISTRICTS, TABLE_DISTRICTTEAMS,
            TABLE_EVENTS, TABLE_EVENTTEAMS, TABLE_MATCHES, TABLE_MEDIAS, TABLE_TEAMS, TABLE_API};

    private Database mDbHelper;
    private SQLiteDatabase mDb;

    @Before
    public void setUp() {
        mDbHelper = Database.getInstance(ApplicationProvider.getApplicationContext(), TBAAndroidModule.getGson());
        mDb = SQLiteDatabase.create(null);
    }

    @Test
    public void testCreateDuplicateDb() {
        // make sure that CREATE IF NOT EXISTS works
        mDbHelper.onCreate(mDbHelper.getWritableDatabase());
    }

    @Test
    public void testTableExists() {
        for (String table : TABLES) {
            mDb.execSQL(String.format(BASE_TABLE_CREATE, table));
        }
        for (String table : TABLES) {
            assertTrue(mDbHelper.tableExists(mDb, table));
        }
        assertFalse(mDbHelper.tableExists(mDb, "meow"));
    }

    @Test
    public void testColumnExists() {
        for (String table : TABLES) {
            mDb.execSQL(String.format(BASE_TABLE_CREATE, table));
        }
        for (String table : TABLES) {
            assertTrue(mDbHelper.columnExists(mDb, table, "id"));
            assertFalse(mDbHelper.columnExists(mDb, table, "meow"));
        }
    }

    @Test
    public void testUpdateAll() {
        for (String table : TABLES) {
            mDb.execSQL(String.format(BASE_TABLE_CREATE, table));
        }
        mDbHelper.onUpgrade(mDb, 0, Database.DATABASE_VERSION);
    }

    @Test
    public void testUpdateTo14() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_EVENTS));
        mDbHelper.onUpgrade(mDb, 13, 14);

        // Should add Districts, DistrictTeams, add DistrictPoints to Event
        assertTrue(mDbHelper.tableExists(mDb, TABLE_DISTRICTS));
        assertTrue(mDbHelper.tableExists(mDb, TABLE_DISTRICTTEAMS));
        assertTrue(mDbHelper.columnExists(mDb, TABLE_EVENTS, EventsTable.DISTRICT_POINTS));
    }

    @Test
    public void testUpdateTo15() {
        mDbHelper.onUpgrade(mDb, 14, 15);

        // Should create Favorites and Subscriptions tables
        assertTrue(mDbHelper.tableExists(mDb, TABLE_FAVORITES));
        assertTrue(mDbHelper.tableExists(mDb, TABLE_SUBSCRIPTIONS));
    }

    @Test
    public void testUpdateTo16() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_SUBSCRIPTIONS));
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_FAVORITES));
        mDbHelper.onUpgrade(mDb, 15, 16);

        // Should Add notifications settings and model enum columns for subscriptions/favorites
        assertTrue(mDbHelper.columnExists(mDb, TABLE_SUBSCRIPTIONS, SubscriptionsTable.NOTIFICATION_SETTINGS));
        assertTrue(mDbHelper.columnExists(mDb, TABLE_SUBSCRIPTIONS, SubscriptionsTable.MODEL_ENUM));
        assertTrue(mDbHelper.columnExists(mDb, TABLE_FAVORITES, FavoritesTable.MODEL_ENUM));
    }

    @Test
    public void testUpdateTo17() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_DISTRICTS));
        mDbHelper.onUpgrade(mDb, 16, 17);

        // Should add District name
        assertTrue(mDbHelper.columnExists(mDb, TABLE_DISTRICTS, DistrictsTable.NAME));
    }

    @Test
    public void testUpdateTo18() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_EVENTS));
        mDbHelper.onUpgrade(mDb, 17, 18);

        // Should add event short name
        assertTrue(mDbHelper.columnExists(mDb, TABLE_EVENTS, EventsTable.SHORTNAME));
    }

    @Test
    public void testUpdateTo20() {
        mDbHelper.onUpgrade(mDb, 18, 20);

        // Should add notifications table
        assertTrue(mDbHelper.tableExists(mDb, TABLE_NOTIFICATIONS));
    }

    @Test
    public void testUpdateTo23And24() {
        // Should drop and recreate search indexes
        mDbHelper.onUpgrade(mDb, 20, 24);
    }

    @Test
    public void testUpdateTo25() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_API));
        mDbHelper.onUpgrade(mDb, 24, 25);

        // Should drop the API table
        assertFalse(mDbHelper.tableExists(mDb, TABLE_API));
    }

    @Test
    public void testUpdateTo28() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_NOTIFICATIONS));
        mDbHelper.onUpgrade(mDb, 25, 28);

        // Should drop and recreate notifications table
        assertTrue(mDbHelper.tableExists(mDb, TABLE_NOTIFICATIONS));
    }

    @Test
    public void testUpdateTo29() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_TEAMS));
        mDbHelper.onUpgrade(mDb, 28, 29);

        // Should create team motto column
        assertTrue(mDbHelper.columnExists(mDb, TABLE_TEAMS, TeamsTable.MOTTO));
    }

    @Test
    public void testUpdateTo30() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_MATCHES));
        mDbHelper.onUpgrade(mDb, 29, 30);

        // Should add match breakdown column
        assertTrue(mDbHelper.columnExists(mDb, TABLE_MATCHES, MatchesTable.BREAKDOWN));
    }

    @Test
    public void testUpdateTo31() {
        for (String table : TABLES) {
            mDb.execSQL(String.format(BASE_TABLE_CREATE, table));
        }
        mDbHelper.onUpgrade(mDb, 30, 31);

        // Should add last modified columns
        String[] modifiedTables = {TABLE_AWARDS, TABLE_DISTRICTS, TABLE_DISTRICTTEAMS,
                TABLE_EVENTS, TABLE_EVENTTEAMS, TABLE_MATCHES, TABLE_MEDIAS, TABLE_TEAMS};
        for (String table : modifiedTables) {
            assertTrue(mDbHelper.columnExists(mDb, table, "last_modified"));
        }
    }

    @Test
    public void testUpdateTo32() {
        for (String table : TABLES) {
            mDb.execSQL(String.format(BASE_TABLE_CREATE, table));
        }
        mDbHelper.onUpgrade(mDb, 31, 32);

        // Should drop some tables and recreate the whole thing. Just not erroring is good enough
    }

    @Test
    public void testUpdateTo33() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_EVENTS));
        assertFalse(mDbHelper.columnExists(mDb, TABLE_EVENTS, EventsTable.CITY));
        mDbHelper.onUpgrade(mDb, 32, 33);
        assertTrue(mDbHelper.columnExists(mDb, TABLE_EVENTS, EventsTable.CITY));
    }

    @Test
    public void testUpdateTo36() {
        mDb.execSQL(String.format(BASE_TABLE_CREATE, TABLE_MEDIAS));
        assertFalse(mDbHelper.columnExists(mDb, TABLE_MEDIAS, MediasTable.B64_IMAGE));
        mDbHelper.onUpgrade(mDb,35, 36);
        assertTrue(mDbHelper.columnExists(mDb, TABLE_MEDIAS, MediasTable.B64_IMAGE));
    }
}