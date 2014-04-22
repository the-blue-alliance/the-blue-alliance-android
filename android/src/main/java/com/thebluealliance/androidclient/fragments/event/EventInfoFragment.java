package com.thebluealliance.androidclient.fragments.event;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.PopulateEventInfo;

/**
 * File created by phil on 4/22/14.
 */
public class EventInfoFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View info = inflater.inflate(R.layout.fragment_event_info,null);
        new PopulateEventInfo(getActivity()).execute(info);
        return info;
    }
}
