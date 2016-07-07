package com.thebluealliance.androidclient.database;

import com.squareup.sqlbrite.BriteDatabase;

import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.TimeUnit;


public final class BriteDatabaseMocker {

    private BriteDatabaseMocker() {
        // unused
    }

    public static BriteDatabase mockDatabase() {
        BriteDatabase briteDb = PowerMockito.mock(BriteDatabase.class);
        PowerMockito.when(briteDb.newTransaction()).thenReturn(new BriteDatabase.Transaction() {
            @Override public void end() {

            }

            @Override public void markSuccessful() {

            }

            @Override public boolean yieldIfContendedSafely() {
                return false;
            }

            @Override public boolean yieldIfContendedSafely(long sleepAmount, TimeUnit sleepUnit) {
                return false;
            }

            @Override public void close() {

            }
        });
        return briteDb;
    }
}
