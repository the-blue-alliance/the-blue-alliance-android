package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.Utilities;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * A singleton class that manages observable references
 * This lets us avoid creating them needlessly during the fragments' lifecycle
 */
@Singleton
public class ObservableCache {

    private Map<String, Observable> mCache;

    @Inject
    public ObservableCache() {
        mCache = Utilities.getMapForPlatform(String.class, Observable.class);
    }

    /**
     * Adds an Observable to the cache, overwriting previous data for this tag if necessary
     * @param tag A string uniquely identifying this Observable (e.g. the Fragment's model keys)
     * @param observable The data to stores
     * @return {@code true} if the data was newly entered, {@code false} if updated
     */
    public boolean addToCache(String tag, Observable observable) {
        return mCache.put(tag, observable) == null;
    }

    /**
     * Removes the {@link Observable} linked to this tag
     * @return {@code true} if the data existed for the tag and was removed, {@code false}  if there was no data
     */
    public boolean clear(String tag) {
        return mCache.remove(tag) != null;
    }

    /**
     * Is there data associated with the given key stored in the cache?
     */
    public boolean contains(String tag) {
        return mCache.containsKey(tag);
    }

    /**
     * @return Data associated with the given tag, or {@code null} if none found
     */
    public Observable get(String tag) {
        return mCache.get(tag);
    }

}
