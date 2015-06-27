package com.thebluealliance.androidclient.subscribers;

import android.content.Context;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamListSubscriber extends BaseAPISubscriber<List<Team>, ListViewAdapter> {

    public ListView mListView;
    public ProgressBar mProgressBar;

    public TeamListSubscriber(Context context) {
        super();
        mDataToBind = new ListViewAdapter(context, new ArrayList<>());
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.values.clear();
        for (int i=0; i < mAPIData.size(); i++) {
            Team team = mAPIData.get(i);
            if (team == null) {
                continue;
            }
            ListItem item = team.render();
            if (item == null) {
                continue;
            }
            mDataToBind.values.add(item);
        }
    }
}
