package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;

/**
 * File created by phil on 4/22/14.
 */
public class MatchListAdapter extends ExpandableListAdapter {

    private String teamKey;

    public MatchListAdapter(Activity a, ArrayList<ListGroup> groups, String selectedTeam) {
        super(a, groups);
        teamKey = selectedTeam;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        BasicModel child = groups.get(groupPosition).children.get(childPosition);
        if(child instanceof Match) {
            ((Match)child).setSelectedTeam(teamKey);
        }
        return child.render().getView(activity, inflater, convertView);
    }
}
