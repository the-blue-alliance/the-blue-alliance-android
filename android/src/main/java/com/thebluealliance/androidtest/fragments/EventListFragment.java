package com.thebluealliance.androidtest.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.adapters.ActionBarSpinnerAdapter;
import com.thebluealliance.androidtest.background.PopulateEventList;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment {

    private Activity activity;

    public EventListFragment(){
        super();
        activity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        ActionBar bar = activity.getActionBar();
        if(bar != null){
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle("");
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            String[] dropdownItems = new String[]{"2014","2013","2012"};
            bar.setListNavigationCallbacks(new ActionBarSpinnerAdapter(activity, R.layout.actionbar_spinner, dropdownItems), new ActionBar.OnNavigationListener() {

                @Override
                public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                    //TODO change the year of events here. Re-run the asynctask
                    return false;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View eventList = inflater.inflate(R.layout.fragment_events,null);
        new PopulateEventList(getActivity(),eventList).execute("");
        return eventList;
    }
}
