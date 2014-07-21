package com.thebluealliance.androidclient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
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
    public void onResume() {
        super.onResume();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) parent).deregisterRefreshableActivityListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRefresh();
    }

    public synchronized void registerRefreshableActivityListener(RefreshListener listener) {
        if (listener != null && !mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
    }

    public synchronized void deregisterRefreshableActivityListener(RefreshListener listener) {
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
        if(!mRefreshListeners.contains(completedListener) && parent instanceof RefreshableHostActivity){
            ((RefreshableHostActivity) parent).notifyRefreshComplete(completedListener);
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
        ((RefreshableHostActivity) parent).notifyRefreshComplete(this);
    }

    public void startRefresh() {
        if (mRefreshInProgress) {
            //if a refresh is already happening, don't start another
            return;
        }
        mRefreshInProgress = true;
        if (mRefreshListeners.isEmpty()) {
            return;
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart();
        }
    }

    public void cancelRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        mRefreshInProgress = false;
    }

    public void restartRefresh() {
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStop();
        }
        for (RefreshListener listener : mRefreshListeners) {
            listener.onRefreshStart();
        }
        mRefreshInProgress = true;
    }

    public void startRefresh(RefreshListener listener){
        if (!mRefreshListeners.contains(listener)) {
            mRefreshListeners.add(listener);
        }
        listener.onRefreshStart();
    }
}
