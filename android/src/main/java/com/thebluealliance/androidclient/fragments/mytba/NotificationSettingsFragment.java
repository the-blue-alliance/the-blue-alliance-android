package com.thebluealliance.androidclient.fragments.mytba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AddRemoveUserFavorite;
import com.thebluealliance.androidclient.background.mytba.UpdateFABIcon;
import com.thebluealliance.androidclient.views.FloatingActionButton;

/**
 * File created by phil on 8/18/14.
 */
public class NotificationSettingsFragment extends Fragment {

    public static final String MODEL_KEY = "model_key";
    private String modelKey;

    public static NotificationSettingsFragment newInstance(String modelKey){
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
        Bundle args = new Bundle();
        args.putString(MODEL_KEY, modelKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() == null || !getArguments().containsKey(MODEL_KEY)){
            throw new IllegalArgumentException("NotificationSettingsFragment must be constructed with a model key");
        }
        modelKey = getArguments().getString(MODEL_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.notification_panel, container, false);
        final FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.favorite_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddRemoveUserFavorite(getActivity(), fab).execute(modelKey);
            }
        });
        new UpdateFABIcon(getActivity(), fab).execute(modelKey);
        return v;
    }
}
