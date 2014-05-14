package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.AwardListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventAwards extends AsyncTask<String, Void, Void> {

    private Fragment mFragment;
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ArrayList<String> keys;
    private ListViewAdapter adapter;

    public PopulateEventAwards(Fragment f) {
        mFragment = f;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<>();
        keys = new ArrayList<>();

        ArrayList<Award> awardList;
        try {
            awardList = DataManager.getEventAwards(mFragment.getActivity(), eventKey);
            for(Award a:awardList){
                ArrayList<AwardListElement> allWinners = a.renderAll();
                awards.addAll(allWinners);
                for (AwardListElement allWinner : allWinners) {
                    keys.add(a.getEventKey() + "_" + a.getName());
                }
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }


        adapter = new ListViewAdapter(mFragment.getActivity(), awards, keys);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        View view = mFragment.getView();
        if (view != null) {
            ListView rankings = (ListView) view.findViewById(R.id.event_awards);
            rankings.setAdapter(adapter);
        }
    }
}
