package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.MatchListElement;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventInfo extends AsyncTask<View,String,String> {

    private Activity activity;

    public PopulateEventInfo(Activity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(View... params) {
        View info = params[0];

        TextView eventDate = (TextView)info.findViewById(R.id.event_date),
                 eventLoc  = (TextView)info.findViewById(R.id.event_location);
        LinearLayout nextMatchLayout = (LinearLayout)info.findViewById(R.id.next_match_container),
                     lastMatchLayout = (LinearLayout)info.findViewById(R.id.last_match_container);

        //set all the event info here, for now, just use temp data
        eventDate.setText("Feb 27th to March 1st, 2014");
        eventLoc.setText("In Myrtle Beach, SC");

        nextMatchLayout.addView(new MatchListElement(true,"Quals 1",new String[]{"3182","3634","2168"},new String[]{"181","4055","237"},23,120,"2014ctgro_qm1").getView(activity.getLayoutInflater(),null));
        lastMatchLayout.addView(new MatchListElement(true,"Quals 2",new String[]{"3718","230","5112"},new String[]{"175","4557","125"},60,121,"2014ctgro_qm2").getView(activity.getLayoutInflater(),null));


        return null;
    }

}
