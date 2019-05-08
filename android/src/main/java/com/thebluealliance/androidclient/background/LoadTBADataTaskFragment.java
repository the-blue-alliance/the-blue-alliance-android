package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.thebluealliance.androidclient.api.call.TbaApiV3;
import com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
import com.thebluealliance.androidclient.database.writers.TeamListWriter;
import com.thebluealliance.androidclient.di.components.DatafeedComponent;
import com.thebluealliance.androidclient.di.components.HasDatafeedComponent;

import javax.inject.Inject;
import javax.inject.Named;

public class LoadTBADataTaskFragment extends Fragment implements LoadTBAData.LoadTBADataCallbacks {

    DatafeedComponent mComponent;
    @Inject @Named("tba_apiv3_call") TbaApiV3 mDatafeed;
    @Inject AppConfig mConfig;
    @Inject Database mDb;
    @Inject TeamListWriter mTeamWriter;
    @Inject EventListWriter mEventWriter;
    @Inject DistrictListWriter mDistrictWriter;

    LoadTBAData.LoadTBADataCallbacks callback;
    private LoadTBAData task;
    private Short[] dataToLoad;
    private LoadTBAData.LoadProgressInfo lastUpdate;
    private boolean lastUpdateDelivered = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof HasDatafeedComponent) {
            mComponent = ((HasDatafeedComponent) getActivity()).getComponent();
        }
        mComponent.inject(this);

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
            task = new LoadTBAData(mDatafeed, mConfig, this, getActivity(), mDb, mTeamWriter, mEventWriter, mDistrictWriter);
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
