package com.thebluealliance.androidclient.fragments.event;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.PopulateEventTeams;

/**
 * File created by phil on 4/22/14.
 */
public class EventTeamsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View out = inflater.inflate(R.layout.fragment_event_teams,null);
        new PopulateEventTeams(getActivity(),out).execute("");
        return out;
    }
}
