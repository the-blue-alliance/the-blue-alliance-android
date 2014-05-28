package com.thebluealliance.androidclient.datatypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class TeamListElement extends ListElement {

    private int mTeamNumber;
    private String mTeamName;
    private String mTeamLocation;

    public TeamListElement(String key, int number, String name, String location) {
        super(key);
        mTeamNumber = number;
        mTeamName = name;
        mTeamLocation = location;
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_team, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView title = (TextView) view.findViewById(R.id.team_number);
            title.setText("" + mTeamNumber);

            TextView dates = (TextView) view.findViewById(R.id.team_name);
            if (mTeamName.equals(""))
            {
                dates.setText("Team " + mTeamNumber);
            }
            else {
                dates.setText(mTeamName);
            }

            TextView location = (TextView) view.findViewById(R.id.team_location);
            location.setText(mTeamLocation);
        }
        return view;
    }
}
