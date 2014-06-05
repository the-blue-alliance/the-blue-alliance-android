package com.thebluealliance.androidclient.datatypes;

import android.content.ContentValues;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
import com.thebluealliance.androidclient.models.BasicModel;

/**
 * File created by phil on 6/4/14.
 */
public class AllianceListElement extends ListElement implements BasicModel{

    private int number;
    private JsonArray teams;

    public AllianceListElement(int number, JsonArray teams){
        if(teams.size() < 2) throw new IllegalArgumentException("Alliances have >= 2 members");
        this.number = number;
        this.teams = teams;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_alliance, null, false);
        }

        if(convertView != null) {
            ((TextView) convertView.findViewById(R.id.alliance_name)).setText(String.format(c.getString(R.string.alliance_title), number));

            TextView team1 = ((TextView) convertView.findViewById(R.id.member_one));
            String team1Key = teams.get(0).getAsString();
            SpannableString underLine = new SpannableString(team1Key.substring(3));
            underLine.setSpan(new UnderlineSpan(), 0, underLine.length(), 0);
            team1.setText(underLine);
            team1.setTag(team1Key);
            team1.setOnClickListener(new TeamClickListener(c));

            TextView team2 = ((TextView) convertView.findViewById(R.id.member_two));
            String team2Key = teams.get(1).getAsString();
            team2.setText(team2Key.substring(3));
            team2.setTag(team2Key);
            team2.setOnClickListener(new TeamClickListener(c));

            if (teams.size() >= 3) {
                TextView team3 = ((TextView) convertView.findViewById(R.id.member_three));
                String team3Key = teams.get(2).getAsString();
                team3.setText(team3Key.substring(3));
                team3.setTag(team3Key);
                team3.setVisibility(View.VISIBLE);
                team3.setOnClickListener(new TeamClickListener(c));
            }

            if (teams.size() >= 4) {
                TextView team4 = ((TextView) convertView.findViewById(R.id.member_four));
                String team4Key = teams.get(3).getAsString();
                team4.setText(team4Key.substring(3));
                team4.setTag(team4Key);
                team4.setVisibility(View.VISIBLE);
                team4.setOnClickListener(new TeamClickListener(c));
            }
        }
        return convertView;
    }

    @Override
    public ListElement render() {
        return this;
    }

    @Override
    public ContentValues getParams() {
        return null;
    }
}
