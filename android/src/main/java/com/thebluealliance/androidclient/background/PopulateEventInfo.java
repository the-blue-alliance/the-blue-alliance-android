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
    View last, next;
    LinearLayout nextLayout,lastLayout,topTeams,topOpr;
    TextView eventDate,eventLoc;

    public PopulateEventInfo(Activity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(View... params) {
        View info = params[0];

        eventDate = (TextView)info.findViewById(R.id.event_date);
        eventLoc  = (TextView)info.findViewById(R.id.event_location);
        nextLayout = (LinearLayout)info.findViewById(R.id.next_match_container);
        lastLayout = (LinearLayout)info.findViewById(R.id.last_match_container);
        topTeams   = (LinearLayout)info.findViewById(R.id.top_teams_container);
        topOpr     = (LinearLayout)info.findViewById(R.id.top_opr_container);

        //set all the event info here, for now, just use temp data
        last = new MatchListElement(true,"Quals 1",new String[]{"3182","3634","2168"},new String[]{"181","4055","237"},23,120,"2014ctgro_qm1").getView(activity.getLayoutInflater(),null);
        next = new MatchListElement(true, "Quals 2", new String[]{"3718", "230", "5112"}, new String[]{"175", "4557", "125"}, 60, 121, "2014ctgro_qm2").getView(activity.getLayoutInflater(), null);


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        eventDate.setText("Feb 27th to March 1st, 2014");
        eventLoc.setText("In Myrtle Beach, SC");
        nextLayout.addView(next);
        lastLayout.addView(last);
        TextView teams = new TextView(activity);
        teams.setText("1. 3824 (9-0-0)\n" +
                      "2. 1876 (9-0-0)\n" +
                      "3. 2655 (8-1-0)\n" +
                      "4. 1251 (7-2-0)");
        topTeams.addView(teams);

        TextView oprs = new TextView(activity);
        oprs.setText("1. 1261 (88.88)\n" +
                     "2. 1772 (83.84)\n" +
                     "3. 3824 (71.54)\n" +
                     "4. 1024 (63.76)");
        topOpr.addView(oprs);
    }
}
