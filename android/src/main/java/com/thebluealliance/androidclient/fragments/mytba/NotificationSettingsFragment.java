package com.thebluealliance.androidclient.fragments.mytba;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.accounts.AddUpdateUserSubscription;
import com.thebluealliance.androidclient.accounts.RemoveUserSubscription;
import com.thebluealliance.androidclient.background.mytba.CreateSubscriptionPanel;
import com.thebluealliance.androidclient.helpers.ModelHelper;

import java.util.ArrayList;

/**
 * File created by phil on 8/18/14.
 */
public class NotificationSettingsFragment extends PreferenceFragment {

    public static final String MODEL_KEY = "model_key";
    private String modelKey;
    private ModelHelper.MODELS modelType;

    public static NotificationSettingsFragment newInstance(String modelKey) {
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
        Bundle args = new Bundle();
        args.putString(MODEL_KEY, modelKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(MODEL_KEY)) {
            throw new IllegalArgumentException("NotificationSettingsFragment must be constructed with a model key");
        }
        modelKey = getArguments().getString(MODEL_KEY);
        modelType = ModelHelper.getModelFromKey(modelKey);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the preference screen that will hold all the preferences
        PreferenceScreen p = getPreferenceManager().createPreferenceScreen(getActivity());
        this.setPreferenceScreen(p);

        // Create the list of preferences
        new CreateSubscriptionPanel(getActivity(), this).execute(modelKey);
    }

    public void saveSettings() {
        ArrayList<String> subscribed = new ArrayList<>();
        subscribed.add(modelKey);

        PreferenceScreen preferences = getPreferenceScreen();
        // Use recursion to make sure we catch any preferences nested in groups
        writeSettingsFromPreferenceGroupToStringArray(preferences, subscribed);
        Log.d(Constants.LOG_TAG, "notifications: " + subscribed);

        if (subscribed.size() == 1) {
            // The user has unsubscribed from everything
            new RemoveUserSubscription(getActivity()).execute(modelKey);
        } else {
            // At least one subscription exists or has been created or updated.
            new AddUpdateUserSubscription(getActivity()).execute(subscribed.toArray(new String[subscribed.size()]));
        }
    }

    private void writeSettingsFromPreferenceGroupToStringArray(PreferenceGroup pg, ArrayList<String> strings) {
        for (int i = 0; i < pg.getPreferenceCount(); i++) {
            Preference currentPreference = pg.getPreference(i);
            if (currentPreference instanceof CheckBoxPreference) {
                if (((CheckBoxPreference) currentPreference).isChecked()) {
                    strings.add(currentPreference.getKey());
                }
            } else if (currentPreference instanceof PreferenceGroup) {
                writeSettingsFromPreferenceGroupToStringArray((PreferenceGroup) currentPreference, strings);
            }
        }
    }
}
