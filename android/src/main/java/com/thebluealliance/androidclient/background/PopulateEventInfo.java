package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.MatchListElement;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventInfo extends AsyncTask<View, String, String> {

    private Context context;
    View last, next;
    LinearLayout nextLayout, lastLayout, topTeams, topOpr;
    TextView eventName, eventDate, eventLoc;

    public PopulateEventInfo(Context c) {
        this.context = c;
    }

    @Override
    protected String doInBackground(View... params) {
        View info = params[0];

        eventName = (TextView) info.findViewById(R.id.event_name);
        eventDate = (TextView) info.findViewById(R.id.event_date);
        eventLoc = (TextView) info.findViewById(R.id.event_location);
        nextLayout = (LinearLayout) info.findViewById(R.id.event_next_match_container);
        lastLayout = (LinearLayout) info.findViewById(R.id.event_last_match_container);
        topTeams = (LinearLayout) info.findViewById(R.id.event_top_teams_container);
        topOpr = (LinearLayout) info.findViewById(R.id.top_opr_container);

        //set all the event info here, for now, just use temp data
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        last = new MatchListElement(true, "Quals 1", new String[]{"3182", "3634", "2168"}, new String[]{"181", "4055", "237"}, 23, 120, "2014ctgro_qm1").getView(context, inflater, null);
        next = new MatchListElement(true, "Quals 2", new String[]{"3718", "230", "5112"}, new String[]{"175", "4557", "125"}, 60, 121, "2014ctgro_qm2").getView(context, inflater, null);


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        eventDate.setText("Feb 27th to March 1st, 2014");
        eventLoc.setText("Myrtle Beach, SC");
        nextLayout.addView(next);
        lastLayout.addView(last);
        TextView teams = new TextView(context);
        teams.setText("1. 3824 (9-0-0)\n" +
                "2. 1876 (9-0-0)\n" +
                "3. 2655 (8-1-0)\n" +
                "4. 1251 (7-2-0)");
        topTeams.addView(teams);

        TextView oprs = new TextView(context);
        oprs.setText("1. 1261 (88.88)\n" +
                "2. 1772 (83.84)\n" +
                "3. 3824 (71.54)\n" +
                "4. 1024 (63.76)");
        topOpr.addView(oprs);
    }
}
