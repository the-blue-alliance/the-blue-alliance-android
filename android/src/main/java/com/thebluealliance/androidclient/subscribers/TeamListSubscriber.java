package com.thebluealliance.androidclient.subscribers;

import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamListSubscriber extends BaseAPISubscriber<List<Team>, List<ListItem>> {

    public ListView mListView;
    public ProgressBar mProgressBar;

    public TeamListSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        for (int i=0; i < mAPIData.size(); i++) {
            Team team = mAPIData.get(i);
            if (team == null) {
                continue;
            }
            ListItem item = team.render();
            if (item == null) {
                continue;
            }
            mDataToBind.add(item);
        }
    }
}
