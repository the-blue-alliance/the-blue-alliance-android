package com.thebluealliance.androidtest.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidtest.R;
import com.thebluealliance.androidtest.activities.ViewEvent;
import com.thebluealliance.androidtest.adapters.ListViewAdapter;
import com.thebluealliance.androidtest.datatypes.ListElement;
import com.thebluealliance.androidtest.datatypes.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of TBA Test.
 * TBA Test is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * TBA Test is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with TBA Test. If not, see http://www.gnu.org/licenses/.
 */
public class PopulateTeamList extends AsyncTask<String,String,String> {

    private Activity activity;
    private View view;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateTeamList(Activity activity, View view){
        this.activity = activity;
        this.view = view;

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
    }

    @Override
    protected String doInBackground(String... params) {
        //some more temp data
        teamKeys.add("frc1124");    teams.add(new ListElement("1124","frc1124"));
        teamKeys.add("frc177");     teams.add(new ListElement("177","frc177"));
        teamKeys.add("frc1114");    teams.add(new ListElement("1114","frc1114"));
        teamKeys.add("frc 254");    teams.add(new ListElement("254","frc254"));
        teamKeys.add("frc2056");    teams.add(new ListElement("2056","frc2056"));

        adapter = new ListViewAdapter(activity,teams,teamKeys);

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView eventList = (ListView)view.findViewById(R.id.team_list);
        eventList.setAdapter(adapter);
    }
}
