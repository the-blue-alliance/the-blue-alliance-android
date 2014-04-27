package com.thebluealliance.androidclient.datatypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class RankingListElement extends ListElement {

    private int mTeamNumber;
    private String mTeamName;
    private int mTeamRank;
    private String mTeamRecord;
    private String mTeamBreakdown;

    public RankingListElement(String key, int number, String name, int ranking, String record, String breakdown) {
        super(key);
        mTeamNumber = number;
        mTeamName = name;
        mTeamRank = ranking;
        mTeamRecord = record;
        mTeamBreakdown = breakdown;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_ranking, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView team = (TextView) view.findViewById(R.id.team_number);
            team.setText("" + mTeamNumber);

            TextView name = (TextView) view.findViewById(R.id.team_name);
            name.setText(mTeamName);

            TextView rank = (TextView) view.findViewById(R.id.team_rank);
            rank.setText("" + mTeamRank);

            TextView record = (TextView) view.findViewById(R.id.team_record); /* formatted as (W, L, T) */
            record.setText(mTeamRecord);

            TextView breakdown = (TextView) view.findViewById(R.id.ranking_breakdown);
            breakdown.setText(mTeamBreakdown);

            if (view.isSelected()) {
                view.setBackgroundColor(c.getResources().getColor(android.R.color.holo_blue_light));
            } else {
                view.setBackgroundColor(c.getResources().getColor(android.R.color.transparent));
            }
        }
        return view;
    }

}
