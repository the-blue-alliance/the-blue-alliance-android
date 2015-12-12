package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import java.util.List;

/**
 * File created by phil on 4/22/14.
 */
public class MatchListAdapter extends ExpandableListViewAdapter {

    private String mTeamKey;

    public MatchListAdapter(
            Activity a,
            ModelRendererSupplier supplier,
            List<ListGroup> groups,
            String selectedTeam) {
        super(a, supplier, groups);
        mTeamKey = selectedTeam;
    }

    public void setTeamKey(String teamKey) {
        if (teamKey != null) {
            mTeamKey = teamKey;
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RenderableModel child = groups.get(groupPosition).children.get(childPosition);
        if (child instanceof Match) {
            ((Match) child).setSelectedTeam(mTeamKey);
        }
        ListItem renderedChild = child.render(mRendererSupplier);
        if (renderedChild != null) {
            return renderedChild.getView(mActivity, inflater, convertView);
        } else {
            return new LabelValueListItem("Match", "Unable to render").getView(mActivity, inflater, convertView);
        }
    }
}
