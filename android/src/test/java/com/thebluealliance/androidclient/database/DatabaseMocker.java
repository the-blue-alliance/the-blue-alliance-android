package com.thebluealliance.androidclient.database;

import android.database.sqlite.SQLiteDatabase;

import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class that mocks the classes surrounding database tables
 */
public class DatabaseMocker {

    public static TeamsTable mockTeamsTable(Database database) {
        TeamsTable table = new TeamsTable(mock(SQLiteDatabase.class));
        when(database.getTeamsTable()).thenReturn(table);
        return table;
    }

    public static EventsTable mockEventsTable(Database database) {
        EventsTable table = new EventsTable(mock(SQLiteDatabase.class));
        when(database.getEventsTable()).thenReturn(table);
        return table;
    }

}
