package com.thebluealliance.androidclient.datafeed;

import android.support.annotation.Nullable;

/**
 * An interface implemented by a fragment to provide the right callbacks
 * @param <T> Datatype expected to be bound to views
 */
public interface DataConsumer<T> {

    /**
     * Tells the consumer to re-bind new data to the views
     * TO BE RUN ON THE UI THREAD - keep it light
     * @param data content to update
     */
    void updateData(@Nullable T data);

    /**
     * Called when we can't fetch data :(
     */
    void onError(Throwable throwable);
}
