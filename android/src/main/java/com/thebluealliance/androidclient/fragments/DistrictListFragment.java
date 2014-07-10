package com.thebluealliance.androidclient.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.background.PopulateDistrictList;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * Created by phil on 7/10/14.
 */
public class DistrictListFragment extends Fragment implements RefreshListener {

    public static final String YEAR = "year";

    private int year;
    private PopulateDistrictList task;

    public static DistrictListFragment newInstance(int year){
        DistrictListFragment f = new DistrictListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, "Loading all districts for "+year);
        task = new PopulateDistrictList(this, true);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, year);
    }

    @Override
    public void onRefreshStop() {
        if(task != null){
            task.cancel(false);
        }
    }
}
