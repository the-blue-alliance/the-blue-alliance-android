package com.thebluealliance.androidclient.database;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;

import android.database.sqlite.SQLiteDatabase;

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
        TeamsTable table = new TeamsTable(db, mock(Gson.class));
        when(database.getTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static EventsTable mockEventsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        DistrictsTable districtsTable = new DistrictsTable(db, mock(Gson.class));
        EventsTable table = new EventsTable(db, mock(Gson.class), districtsTable);
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
        AwardsTable table = new AwardsTable(db, mock(Gson.class));
        when(database.getAwardsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static DistrictsTable mockDistrictsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        DistrictsTable table = new DistrictsTable(db, mock(Gson.class));
        when(database.getDistrictsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static DistrictTeamsTable mockDistrictTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        DistrictTeamsTable table = new DistrictTeamsTable(db, mock(Gson.class));
        when(database.getDistrictTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static EventTeamsTable mockEventTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        EventTeamsTable table = new EventTeamsTable(db, mock(Gson.class));
        when(database.getEventTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static MatchesTable mockMatchesTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        MatchesTable table = new MatchesTable(db, mock(Gson.class));
        when(database.getMatchesTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static MediasTable mockMediasTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        MediasTable table = new MediasTable(db, mock(Gson.class));
        when(database.getMediasTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

}
