package com.thebluealliance.androidclient.database;

import com.squareup.sqlbrite.BriteDatabase;
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
        BriteDatabase briteDb = mock(BriteDatabase.class);
        TeamsTable table = new TeamsTable(db, briteDb);
        when(database.getTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static EventsTable mockEventsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        EventsTable table = new EventsTable(db, briteDb);
        when(database.getEventsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static NotificationsTable mockNotificationsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        NotificationsTable table = new NotificationsTable(db, briteDb);
        when(database.getNotificationsTable()).thenReturn(table);
        return table;
    }

    public static AwardsTable mockAwardsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        AwardsTable table = new AwardsTable(db, briteDb);
        when(database.getAwardsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static DistrictsTable mockDistrictsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        DistrictsTable table = new DistrictsTable(db, briteDb);
        when(database.getDistrictsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static DistrictTeamsTable mockDistrictTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        DistrictTeamsTable table = new DistrictTeamsTable(db, briteDb);
        when(database.getDistrictTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static EventTeamsTable mockEventTeamsTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        EventTeamsTable table = new EventTeamsTable(db, briteDb);
        when(database.getEventTeamsTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static MatchesTable mockMatchesTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        MatchesTable table = new MatchesTable(db, briteDb);
        when(database.getMatchesTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

    public static MediasTable mockMediasTable(Database database) {
        SQLiteDatabase db = mock(SQLiteDatabase.class);
        BriteDatabase briteDb = mock(BriteDatabase.class);
        MediasTable table = new MediasTable(db, briteDb);
        when(database.getMediasTable()).thenReturn(table);
        when(database.getWritableDatabase()).thenReturn(db);
        return table;
    }

}
