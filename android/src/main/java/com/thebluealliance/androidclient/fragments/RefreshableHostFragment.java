package com.thebluealliance.androidclient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.interfaces.RefreshableHost;

import java.util.ArrayList;

/**
 * File created by phil on 7/20/14.
 */
public abstract class RefreshableHostFragment extends Fragment implements RefreshListener, RefreshableHost {

    private ArrayList<RefreshListener> mRefreshListeners = new ArrayList<>();
    private ArrayList<RefreshListener> mCompletedRefreshListeners = new ArrayList<>();
    private boolean mRefreshInProgress;
    private Activity parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LegacyRefreshableHostActivity) parent).unregisterRefreshListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRefresh();
    }

    public synchronized void registerRefreshListener(RefreshListener listener) {
        if (listener != null && !mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
    }

    public synchronized void unregisterRefreshListener(RefreshListener listener) {
        if (listener != null && mRefreshListeners.contains(listener)) {
            mRefreshListeners.remove(listener);
        }
        if (listener != null && mCompletedRefreshListeners.contains(listener)) {
            mCompletedRefreshListeners.remove(listener);
        }
    }

    public synchronized void notifyRefreshComplete(RefreshListener completedListener) {
        if (completedListener == null) {
            return;
        }
        if (!mRefreshListeners.contains(completedListener) && parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).notifyRefreshComplete(completedListener);
        }
        if (!mCompletedRefreshListeners.contains(completedListener)) {
            mCompletedRefreshListeners.add(completedListener);
        }
        if (mCompletedRefreshListeners.size() >= mRefreshListeners.size()) {
            onRefreshComplete();
            mCompletedRefreshListeners.clear();
        }
    }

    protected void onRefreshComplete() {
        mRefreshInProgress = false;
        ((LegacyRefreshableHostActivity) parent).notifyRefreshComplete(this);
    }

    public void startRefresh(boolean actionIconPressed) {
        if (mRefreshInProgress) {
            //if a refresh is already happening, don't start another
            return;
        }
        mRefreshInProgress = true;
        if (mRefreshListeners.isEmpty()) {
            return;
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart(actionIconPressed);
        }
    }

    public void cancelRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        mRefreshInProgress = false;
    }

    public void restartRefresh(boolean actionIconPressed) {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart(actionIconPressed);
        }
        mRefreshInProgress = true;
    }

    public void startRefresh(RefreshListener listener) {
        if (!mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
        listener.onRefreshStart(false);
    }
}
