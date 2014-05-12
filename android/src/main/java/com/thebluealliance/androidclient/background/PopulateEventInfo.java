package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.MatchListElement;
import com.thebluealliance.androidclient.models.Event;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventInfo extends AsyncTask<String, String, APIResponse.CODE> {

    private Fragment mFragment;
    private BaseActivity activity;
    View last, next;
    LinearLayout nextLayout, lastLayout, topTeams, topOpr;
    TextView eventName, eventDate, eventLoc;
    String eventKey;
    Event event;

    public PopulateEventInfo(Fragment f) {
        mFragment = f;
        activity = (BaseActivity)mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        View view = mFragment.getView();
        if (view != null && mFragment.getActivity() != null) {
            eventName = (TextView) view.findViewById(R.id.event_name);
            eventDate = (TextView) view.findViewById(R.id.event_date);
            eventLoc = (TextView) view.findViewById(R.id.event_location);
            nextLayout = (LinearLayout) view.findViewById(R.id.event_next_match_container);
            lastLayout = (LinearLayout) view.findViewById(R.id.event_last_match_container);
            topTeams = (LinearLayout) view.findViewById(R.id.event_top_teams_container);
            topOpr = (LinearLayout) view.findViewById(R.id.top_opr_container);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            APIResponse<Event> response = DataManager.getEvent(activity,eventKey);
            event = response.getData();
            last = new MatchListElement(true, "Quals 1", new String[]{"3182", "3634", "2168"}, new String[]{"181", "4055", "237"}, 23, 120, "2014ctgro_qm1").getView(activity, inflater, null);
            next = new MatchListElement(true, "Quals 2", new String[]{"3718", "230", "5112"}, new String[]{"175", "4557", "125"}, 60, 121, "2014ctgro_qm2").getView(activity, inflater, null);
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }

        /* TODO finish basic event bits as the rest of the API queries get implemented
         * this includes next/last match, if event is currently active
         * Top teams in rankings
         * Top teams in stats
         *
         */
        }
        return APIResponse.CODE.NODATA;
    }

    @Override
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);
        if (event != null && mFragment.getActivity() != null) {
            eventName.setText(event.getEventName());
            eventDate.setText(event.getDateString());
            eventLoc.setText(event.getLocation());
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

            if(c == APIResponse.CODE.OFFLINECACHE /* && event is current */){
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }
    }
}
