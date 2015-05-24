package com.thebluealliance.androidclient.fragments.tasks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.accounts.UpdateUserModelSettings;
import com.thebluealliance.androidclient.helpers.ModelNotificationFavoriteSettings;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;

/**
 * Created by Nathan on 11/7/2014.
 */
public class UpdateUserModelSettingsTaskFragment extends Fragment {

    private UpdateUserModelSettings task;
    private ModelSettingsCallbacks callbacks;
    private ModelNotificationFavoriteSettings settings;

    public UpdateUserModelSettingsTaskFragment() {
    }

    public UpdateUserModelSettingsTaskFragment(ModelNotificationFavoriteSettings settings) {
        this.settings = settings;

        // Stash the settings so they'll be retained across Fragment destroy and creation.
        Bundle bundle = new Bundle();
        settings.writeToBundle(bundle);
        setArguments(bundle);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (settings == null) {
            Bundle arguments = getArguments();
            settings = ModelNotificationFavoriteSettings.readFromBundle(arguments);
        }

        callbacks = (ModelSettingsCallbacks) activity;
        this.setRetainInstance(true);
        // If the task does not exist, create it
        if (task != null) {
            task.setCallbacks(callbacks);
        } else {
            task = new UpdateUserModelSettings(activity, settings);
            task.setCallbacks(callbacks);
            task.execute();
        }
    }
}
