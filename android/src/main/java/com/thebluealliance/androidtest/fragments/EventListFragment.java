package com.thebluealliance.androidtest.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.background.PopulateEventList;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment implements AdapterView.OnItemSelectedListener, ActionBar.TabListener {

    private Activity activity;
    private ActionBar bar;
    private View eventList;
    private String year, dropdownItems[];

    public EventListFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bar = activity.getActionBar();
        if(bar != null){
            //configure action bar title
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(activity.getString(R.string.app_name_short));
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            //inflate custom action bar layout.
            //This has to be done, because the regular layouts don't support both spinner and tab nagigation
            bar.setDisplayShowCustomEnabled(true);
            View actionBarView = inflateActionBarLayout();
            bar.setCustomView(actionBarView);

            //create appropriate number of action bar tabs
            bar.removeAllTabs();
            //TODO get the number of weeks this season spans
            /* for now, use three weeks */
            bar.addTab(bar.newTab().setText(activity.getString(R.string.week_selector)+" "+1).setTag("week1").setTabListener(this));
            bar.addTab(bar.newTab().setText(activity.getString(R.string.week_selector)+" "+2).setTag("week2").setTabListener(this));
            bar.addTab(bar.newTab().setText(activity.getString(R.string.week_selector)+" "+3).setTag("week3").setTabListener(this));
            //TODO again, select the proper tab based on the SavedInstanceState
        }
    }

    private View inflateActionBarLayout(){
        dropdownItems = new String[]{"2014","2013","2012"};
        View out = activity.getLayoutInflater().inflate(R.layout.actionbar_spinner_layout,null);
        TextView title = (TextView)out.findViewById(R.id.title);
        title.setText(R.string.tab_events);
        Spinner subtitle = (Spinner)out.findViewById(R.id.subtitle);
        ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(activity,R.layout.actionbar_spinner,dropdownItems);
        actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
        subtitle.setAdapter(actionBarAdapter);
        subtitle.setOnItemSelectedListener(this);
        subtitle.setSelection(0); //TODO this should be taken from a SavedInstanceState, if available
        return out;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(eventList == null)
            eventList = inflater.inflate(R.layout.fragment_events,null);
        return eventList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        year = dropdownItems[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        String week;
        if(bar == null || bar.getSelectedTab()==null)
            week = "week1";
        else
            week = bar.getSelectedTab().getTag().toString();

        if(eventList == null)
            eventList = activity.getLayoutInflater().inflate(R.layout.fragment_events,null);
        new PopulateEventList(getActivity(),eventList).execute(year,week);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
