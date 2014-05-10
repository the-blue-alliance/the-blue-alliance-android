package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.AwardListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;

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

        //add some temp data
        keys.add("frc1311");
        awards.add(new AwardListElement("frc1311", "Regional Chairman's Award", "1311"));
        keys.add("frc2974");
        awards.add(new AwardListElement("frc2974", "Engineering Inspiration Award", "2974"));
        keys.add("frc4965");
        awards.add(new AwardListElement("frc4965", "Rookie All Star", "4965"));
        keys.add("frc4551");
        awards.add(new AwardListElement("frc4551", "Woodie Flowers Finalist Award", "James Bryan\n(4551)"));

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
