package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData;

public class LoadTBADataTaskFragment extends Fragment implements LoadTBAData.LoadTBADataCallbacks {

    LoadTBAData.LoadTBADataCallbacks callback;
    private LoadTBAData task;
    private Short[] dataToLoad;
    private LoadTBAData.LoadProgressInfo lastUpdate;
    private boolean lastUpdateDelivered = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof LoadTBAData.LoadTBADataCallbacks) {
            callback = (LoadTBAData.LoadTBADataCallbacks) activity;
        } else {
            throw new IllegalStateException("LoadTBADataTaskFragment must be hosted by an activity that implements LoadTBADataCallbacks");
        }

        Bundle args = getArguments();
        if (args != null && args.containsKey(LoadTBAData.DATA_TO_LOAD)) {
            short[] inData = args.getShortArray(LoadTBAData.DATA_TO_LOAD);
            if (inData != null) {
                dataToLoad = new Short[inData.length];
                for (int i = 0; i < dataToLoad.length; i++) {
                    dataToLoad[i] = inData[i];
                }
            }
        } else if (args != null) {
            // Don't load any data
        } else {
            // Load all data
            dataToLoad = new Short[]{LoadTBAData.LOAD_TEAMS, LoadTBAData.LOAD_EVENTS, LoadTBAData.LOAD_DISTRICTS};
        }

        if (task == null) {
            task = new LoadTBAData(this, getActivity());
            task.execute(dataToLoad);
        }
    }

    public void cancelTask() {
        task.cancel(false);
    }

    @Override
    public void onProgressUpdate(LoadTBAData.LoadProgressInfo info) {
        lastUpdate = info;
        if (callback != null) {
            callback.onProgressUpdate(info);
            lastUpdateDelivered = true;
        }
    }

    public LoadTBAData.LoadProgressInfo getLastProgressUpdate() {
        return lastUpdate;
    }

    public boolean wasLastUpdateDelivered() {
        return lastUpdateDelivered;
    }
}
