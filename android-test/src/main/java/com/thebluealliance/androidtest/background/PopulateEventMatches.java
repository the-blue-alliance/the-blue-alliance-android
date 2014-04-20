package com.thebluealliance.androidtest.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidtest.R;
import com.thebluealliance.androidtest.adapters.ListViewAdapter;
import com.thebluealliance.androidtest.datatypes.ListElement;
import com.thebluealliance.androidtest.datatypes.ListItem;
import com.thebluealliance.androidtest.datatypes.MatchListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of TBA Test.
 * TBA Test is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * TBA Test is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with TBA Test. If not, see http://www.gnu.org/licenses/.
 */
public class PopulateEventMatches extends AsyncTask<String,String,String> {

    private Activity activity;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventMatches(Activity activity){
        this.activity = activity;

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
    }

    @Override
    protected String doInBackground(String... params) {
        //some more temp data
        teamKeys.add("2014ctgro_qm1");      teams.add(new MatchListElement(true,"Quals 1",new String[]{"3182","3634","2168"},new String[]{"181","4055","237"},23,120,"2014ctgro_qm1"));
        teamKeys.add("2014ctgro_qm2");      teams.add(new MatchListElement(true,"Quals 2",new String[]{"3718","230","5112"},new String[]{"175","4557","125"},60,121,"2014ctgro_qm2"));

        adapter = new ListViewAdapter(activity,teams,teamKeys);

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView eventList = (ListView)activity.findViewById(R.id.match_list);
        eventList.setAdapter(adapter);
    }

}
