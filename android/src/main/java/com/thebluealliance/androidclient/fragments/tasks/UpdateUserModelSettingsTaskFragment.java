package com.thebluealliance.androidclient.fragments.tasks;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.accounts.UpdateUserModelSettings;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.helpers.ModelNotificationFavoriteSettings;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

public class UpdateUserModelSettingsTaskFragment extends Fragment {

    private UpdateUserModelSettings task;
    private ModelNotificationFavoriteSettings settings;

    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public static UpdateUserModelSettingsTaskFragment newInstance(
            ModelNotificationFavoriteSettings settings) {
        UpdateUserModelSettingsTaskFragment fragment = new UpdateUserModelSettingsTaskFragment();
        Bundle bundle = new Bundle();
        settings.writeToBundle(bundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TBAAndroid application = (TBAAndroid) getActivity().getApplication();
        DaggerMyTbaComponent.builder()
                            .tBAAndroidModule(application.getModule())
                            .accountModule(application.getAccountModule())
                            .authModule(application.getAuthModule())
                            .applicationComponent(application.getComponent())
                            .gceModule(application.getGceModule())
                            .build()
                            .inject(this);

        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("UpdateUserModelSettingsTaskFragment needs "
                                               + "settings");
        }
        settings = ModelNotificationFavoriteSettings.readFromBundle(arguments);

        Activity activity = getActivity();
        ModelSettingsCallbacks callbacks = (ModelSettingsCallbacks) activity;
        this.setRetainInstance(true);
        // If the task does not exist, create it
        if (task != null) {
            task.setCallbacks(callbacks);
        } else {
            task = new UpdateUserModelSettings(activity, mMyTbaDatafeed, settings);
            task.setCallbacks(callbacks);
            task.execute();
        }
    }

}
