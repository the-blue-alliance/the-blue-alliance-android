package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.adapters.ExpandableListViewAdapter;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import java.util.List;

import javax.inject.Inject;

public class MatchListBinder extends ExpandableListViewBinder {

    private String mSelectedTeam;

    @Inject
    public MatchListBinder(ModelRendererSupplier supplier) {
        super(supplier);
    }

    public void setSelectedTeam(String selectedTeam) {
        mSelectedTeam = selectedTeam;
    }

    @Override
    protected ExpandableListViewAdapter newAdapter(List<ListGroup> data) {
        return new MatchListAdapter(mActivity, mRendererSupplier, data, mSelectedTeam);
    }
}
