package com.thebluealliance.androidclient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 4/20/14.
 */
public class InsightsFragment extends Fragment implements RefreshListener {

    private Activity parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = getActivity();
        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart(boolean actionItemPressed) {

    }

    @Override
    public void onRefreshStop() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LegacyRefreshableHostActivity) parent).unregisterRefreshListener(this);
    }
}
