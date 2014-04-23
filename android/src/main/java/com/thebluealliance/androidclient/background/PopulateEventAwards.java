package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.AwardListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventAwards extends AsyncTask<String,Void,Void> {

    private Activity activity;
    private View view;
    private String eventKey;
    private ArrayList<AwardListElement> awards;

    public PopulateEventAwards(Activity activity, View view){
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<AwardListElement>();

        //add some temp data
        awards.add(new AwardListElement("frc1311","Regional Chairman's Award","1311"));
        awards.add(new AwardListElement("frc2974", "Engineering Inspiration Award", "2974"));
        awards.add(new AwardListElement("frc4965", "Rookie All Star", "4965"));
        awards.add(new AwardListElement("frc4551", "Woodie Flowers Finalist Award", "James Bryan\n(4551)"));

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TableLayout rankings = (TableLayout)view.findViewById(R.id.event_awards);
        LayoutInflater inflater = activity.getLayoutInflater();
        for(AwardListElement award: awards){
            rankings.addView(award.getView(inflater,null));
        }

    }

}
