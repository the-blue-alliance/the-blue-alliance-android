package com.thebluealliance.androidtest.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.background.PopulateEventList;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment {

    public EventListFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View eventList = inflater.inflate(R.layout.fragment_events,null);
        new PopulateEventList(getActivity(),eventList).execute("");
        return eventList;
    }
}
