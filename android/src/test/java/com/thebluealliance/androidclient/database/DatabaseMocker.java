package com.thebluealliance.androidclient.database;

import android.database.sqlite.SQLiteDatabase;

import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.HttpModule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class that mocks the classes surrounding database tables
 */
public final class DatabaseMocker {

    private DatabaseMocker() {
        // unused
    }

    public static TeamsTable mockTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        TeamsTable table = new TeamsTable(db, HttpModule.getGson());
        when(database.getTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static EventsTable mockEventsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        DistrictsTable districtsTable = new DistrictsTable(db, HttpModule.getGson());
        EventsTable table = new EventsTable(db, HttpModule.getGson(), districtsTable);
        when(database.getEventsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static NotificationsTable mockNotificationsTable(Database database) {
        NotificationsTable table = new NotificationsTable(mock(SQLiteDatabase.class));
        when(database.getNotificationsTable()).thenReturn(table);
        return table;
    }

    public static AwardsTable mockAwardsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        AwardsTable table = new AwardsTable(db, HttpModule.getGson());
        when(database.getAwardsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static DistrictsTable mockDistrictsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        DistrictsTable table = new DistrictsTable(db, HttpModule.getGson());
        when(database.getDistrictsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static DistrictTeamsTable mockDistrictTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        DistrictTeamsTable table = new DistrictTeamsTable(db, HttpModule.getGson());
        when(database.getDistrictTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static EventTeamsTable mockEventTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        EventTeamsTable table = new EventTeamsTable(db, HttpModule.getGson());
        when(database.getEventTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static MatchesTable mockMatchesTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        MatchesTable table = new MatchesTable(db, HttpModule.getGson());
        when(database.getMatchesTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static MediasTable mockMediasTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        MediasTable table = new MediasTable(db, HttpModule.getGson());
        when(database.getMediasTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

}
