package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.listitems.ListGroup;

import java.util.List;

public class MatchListBinder extends ExpandableListBinder {

    private String mSelectedTeam;

    public void setSelectedTeam(String selectedTeam) {
        mSelectedTeam = selectedTeam;
    }

    @Override
    protected ExpandableListAdapter newAdapter(List<ListGroup> data) {
        return new MatchListAdapter(mActivity, data, mSelectedTeam);
    }
}
