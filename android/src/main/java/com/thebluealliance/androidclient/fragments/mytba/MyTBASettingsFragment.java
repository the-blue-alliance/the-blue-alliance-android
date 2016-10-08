package com.thebluealliance.androidclient.fragments.mytba;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.background.mytba.CreateSubscriptionPanel;
import com.thebluealliance.androidclient.di.components.DaggerMyTbaComponent;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.helpers.ModelNotificationFavoriteSettings;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.interfaces.LoadModelSettingsCallback;
import com.thebluealliance.androidclient.types.ModelType;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

public class MyTBASettingsFragment extends PreferenceFragment {

    public static final String MODEL_KEY = "model_key";
    public static final String MODEL_TYPE = "model_type";
    public static final String SAVED_STATE_BUNDLE = "saved_state_bundle";
    private Bundle savedStateBundle;
    private Bundle initialStateBundle;
    private String modelKey;
    private ModelType modelType;
    private LoadModelSettingsCallback loadCallback;

    private boolean preferencesLoaded = false;

    @Inject AccountController mAccountController;

    public static MyTBASettingsFragment newInstance(String modelKey, ModelType modelType, Bundle savedStateBundle) {
        MyTBASettingsFragment fragment = new MyTBASettingsFragment();
        Bundle args = new Bundle();
        args.putString(MODEL_KEY, modelKey);
        args.putInt(MODEL_TYPE, modelType.getEnum());
        args.putBundle(SAVED_STATE_BUNDLE, savedStateBundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoadModelSettingsCallback) {
            loadCallback = (LoadModelSettingsCallback) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(MODEL_KEY)) {
            throw new IllegalArgumentException("MyTBASettingsFragment must be constructed with a model key");
        }
        TBAAndroid application = (TBAAndroid) getActivity().getApplication();
        DaggerMyTbaComponent.builder()
                .tBAAndroidModule(application.getModule())
                .accountModule(application.getAccountModule())
                .authModule(application.getAuthModule())
                .applicationComponent(application.getComponent())
                .build()
                .inject(this);
        modelKey = getArguments().getString(MODEL_KEY);
        modelType = ModelHelper.getModelFromEnum(getArguments().getInt(MODEL_TYPE));
        savedStateBundle = getArguments().getBundle(SAVED_STATE_BUNDLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the preference screen that will hold all the preferences
        PreferenceScreen p = getPreferenceManager().createPreferenceScreen(getActivity());
        this.setPreferenceScreen(p);

        // Create the list of preferences
        new CreateSubscriptionPanel(getActivity(), mAccountController, this, savedStateBundle, modelType).execute(modelKey);

        // Setup padding on the view. Padding is needed at the bottom to account for the FAB.
        if (getView() != null) {
            ListView listView = (ListView) getView().findViewById(android.R.id.list);
            if (listView != null) {
                listView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.fab_list_padding));
                listView.setClipToPadding(false);
                // Scrollbar gets janky with padding in a listview. Just hide it.
                listView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    public ModelNotificationFavoriteSettings getSettings() {
        ArrayList<String> subscribed = new ArrayList<>();

        PreferenceScreen preferences = getPreferenceScreen();
        // Use recursion to make sure we catch any preferences nested in groups
        writeSettingsFromPreferenceGroupToStringArray(preferences, subscribed);

        // Don't pass the favorite preference to the updater.
        subscribed.remove(MyTBAHelper.getFavoritePreferenceKey());
        TbaLogger.d("notifications: " + subscribed);

        ModelNotificationFavoriteSettings settings = new ModelNotificationFavoriteSettings();
        Preference fav = findPreference(MyTBAHelper.getFavoritePreferenceKey());
        settings.isFavorite = (fav != null && ((CheckBoxPreference) fav).isChecked());
        settings.enabledNotifications = subscribed;
        settings.modelKey = modelKey;
        settings.modelType = modelType;

        return settings;
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

    public void writeStateToBundle(Bundle b) {
        PreferenceGroup pg = getPreferenceScreen();
        for (int i = 0; i < pg.getPreferenceCount(); i++) {
            Preference currentPreference = pg.getPreference(i);
            if (currentPreference instanceof CheckBoxPreference) {
                b.putBoolean(currentPreference.getKey(), ((CheckBoxPreference) currentPreference).isChecked());
            } else if (currentPreference instanceof PreferenceGroup) {
                writeStateToBundle(b, (PreferenceGroup) currentPreference);
            }
        }
    }

    private void writeStateToBundle(Bundle b, PreferenceGroup pg) {
        for (int i = 0; i < pg.getPreferenceCount(); i++) {
            Preference currentPreference = pg.getPreference(i);
            if (currentPreference instanceof CheckBoxPreference) {
                b.putBoolean(currentPreference.getKey(), ((CheckBoxPreference) currentPreference).isChecked());
            } else if (currentPreference instanceof PreferenceGroup) {
                writeStateToBundle(b, (PreferenceGroup) currentPreference);
            }
        }
    }

    // Call when preferences have been loaded into the fragment
    public void setPreferencesLoaded() {
        preferencesLoaded = true;
        if (loadCallback != null) {
            loadCallback.onSettingsLoaded();
        }
    }

    public boolean arePreferencesLoaded() {
        return preferencesLoaded;
    }

    public void setInitialStateBundle(Bundle b) {
        initialStateBundle = b;
    }

    // Call to restore the preference fragment to its initial state, before the user unchecked or checked anything.
    public void restoreInitialState() {
        if (initialStateBundle == null) {
            return;
        }

        Set<String> keys = initialStateBundle.keySet();
        for (String key : keys) {
            ((CheckBoxPreference) findPreference(key)).setChecked(initialStateBundle.getBoolean(key));
        }
    }

    public void refreshSettingsFromDatabase() {
        new CreateSubscriptionPanel(getActivity(), mAccountController, this, savedStateBundle,
                                    modelType).execute(modelKey);
    }
}
