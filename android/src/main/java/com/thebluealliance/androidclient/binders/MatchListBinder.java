package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.adapters.ExpandableListViewAdapter;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.listitems.ListGroup;

import java.util.List;

public class MatchListBinder extends ExpandableListViewBinder {

    private String mSelectedTeam;

    public void setSelectedTeam(String selectedTeam) {
        mSelectedTeam = selectedTeam;
    }

    @Override
    protected ExpandableListViewAdapter newAdapter(List<ListGroup> data) {
        return new MatchListAdapter(mActivity, data, mSelectedTeam);
    }
}
