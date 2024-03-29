package com.thebluealliance.androidclient.datafeed;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

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
    @UiThread
    void updateData(@Nullable T data);

    /**
     * A hook for binders to clean up their views
     * @param unbindViews
     */
    @UiThread
    void unbind(boolean unbindViews);

    /**
     * Called when loading completes
     */
    @UiThread
    void onComplete();

    /**
     * Called for view binding
     */
    @UiThread
    void bindViews();

    /**
     * Called when we can't fetch data :(
     */
    @UiThread
    void onError(Throwable throwable);
}
