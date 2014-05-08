package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.PopulateEventTeams;

/**
 * File created by phil on 4/22/14.
 */
public class EventTeamsFragment extends Fragment {

    private String eventKey;
    private static final String KEY = "eventKey";

    public static EventTeamsFragment getInstance(String eventKey){
        EventTeamsFragment f = new EventTeamsFragment();
        Bundle data = new Bundle();
        data.putString(KEY,eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            eventKey = getArguments().getString(KEY,"");
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(KEY)){
            eventKey = savedInstanceState.getString(KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(KEY)){
            eventKey = savedInstanceState.getString(KEY);
        }
        View out = inflater.inflate(R.layout.fragment_event_teams, null);
        new PopulateEventTeams(getActivity(), out).execute(eventKey);
        return out;
    }
}
