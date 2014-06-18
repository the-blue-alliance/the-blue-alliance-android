package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class StatsListElement extends ListElement {

    private int mTeamNumber;
    private String mTeamName;
    private String mTeamStat;
    private String mTeamLocation;

    public StatsListElement(String key, int number, String name, String location, String stat) {
        super(key);
        mTeamNumber = number;
        mTeamName = name;
        mTeamStat = stat;
        mTeamLocation = location;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_stats, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView team = (TextView) view.findViewById(R.id.team_number);
            team.setText("" + mTeamNumber);

            TextView name = (TextView) view.findViewById(R.id.team_name);
            if (!mTeamName.isEmpty()) {
                name.setText(mTeamName);
            } else {
                name.setVisibility(View.GONE);
            }

            TextView location = (TextView) view.findViewById(R.id.team_location);
            if (!mTeamLocation.isEmpty()) {
                location.setText(mTeamLocation);
            } else {
                location.setVisibility(View.GONE);
            }

            TextView stat = (TextView) view.findViewById(R.id.team_stat);
            stat.setText(mTeamStat);
        }
        return view;
    }

}
