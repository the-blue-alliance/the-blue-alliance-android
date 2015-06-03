package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.listitems.ListItem;
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
        RenderableModel child = groups.get(groupPosition).children.get(childPosition);
        if (child instanceof Match) {
            ((Match) child).setSelectedTeam(teamKey);
        }
        ListItem renderedChild = child.render();
        if (renderedChild != null) {
            return renderedChild.getView(activity, inflater, convertView);
        } else {
            return new LabelValueListItem("Match", "Unable to render").getView(activity, inflater, convertView);
        }
    }
}
