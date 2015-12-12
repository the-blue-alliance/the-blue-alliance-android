package com.thebluealliance.androidclient.datafeed.refresh;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * A class that wraps a {@link Refreshable} and and tracks its refreshing state
 */
public class RefreshWrapper {

    private final WeakReference<Refreshable> mRefreshable;
    private boolean mRefreshState;

    public RefreshWrapper(Refreshable refreshable, boolean refreshState) {
        mRefreshable = new WeakReference<>(refreshable);
        mRefreshState = refreshState;
    }

    public
    @Nullable
    Refreshable getRefreshable() {
        return mRefreshable.get();
    }

    public boolean getRefreshState() {
        return mRefreshState;
    }

    public void setRefreshState(boolean refreshState) {
        mRefreshState = refreshState;
    }

    @Override
    public int hashCode() {
        Refreshable refreshable = mRefreshable.get();
        if (refreshable != null) {
            return refreshable.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        Refreshable refreshable = mRefreshable.get();
        if (refreshable != null) {
            return refreshable.equals(o);
        }
        return super.equals(o);
    }
}
