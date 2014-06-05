package com.thebluealliance.androidclient.datatypes;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
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

        ((TextView)convertView.findViewById(R.id.alliance_name)).setText(String.format(c.getString(R.string.alliance_title), number));

        ((TextView)convertView.findViewById(R.id.member_one)).setText(teams.get(0).getAsString());
        ((TextView)convertView.findViewById(R.id.member_two)).setText(teams.get(1).getAsString());

        if(teams.size() >= 3){
            View team3 = convertView.findViewById(R.id.member_three);
            ((TextView)team3).setText(teams.get(2).getAsString());
            team3.setVisibility(View.VISIBLE);
        }

        if(teams.size() >= 4){
            View team3 = convertView.findViewById(R.id.member_four);
            ((TextView)team3).setText(teams.get(3).getAsString());
            team3.setVisibility(View.VISIBLE);
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
