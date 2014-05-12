package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
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

    private Context context;
    private View view;
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ArrayList<String> keys;
    private ListViewAdapter adapter;

    public PopulateEventAwards(Context c, View view) {
        this.context = c;
        this.view = view;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<ListItem>();
        keys = new ArrayList<String>();

        //add some temp data
        keys.add("frc1311");
        awards.add(new AwardListElement("frc1311", "Regional Chairman's Award", "1311"));
        keys.add("frc2974");
        awards.add(new AwardListElement("frc2974", "Engineering Inspiration Award", "2974"));
        keys.add("frc4965");
        awards.add(new AwardListElement("frc4965", "Rookie All Star", "4965"));
        keys.add("frc4551");
        awards.add(new AwardListElement("frc4551", "Woodie Flowers Finalist Award", "James Bryan\n(4551)"));

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (view != null && context != null) {
            adapter = new ListViewAdapter(context, awards, keys);
            ListView rankings = (ListView) view.findViewById(R.id.event_awards);
            rankings.setAdapter(adapter);
        }
    }

}
