package com.thebluealliance.androidclient.listitems;

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
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_ranking, null);

            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            holder.teamName = (TextView) convertView.findViewById(R.id.team_name);
            holder.rank = (TextView) convertView.findViewById(R.id.team_rank);
            holder.record = (TextView) convertView.findViewById(R.id.team_record);
            holder.breakdown = (TextView) convertView.findViewById(R.id.ranking_breakdown);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNumber.setText("" + mTeamNumber);

        if (mTeamName.equals("")) {
            holder.teamName.setVisibility(View.INVISIBLE);
        } else {
            holder.teamName.setText(mTeamName);
        }

        holder.rank.setText(String.format(c.getString(R.string.team_rank), mTeamRank));

        if (mTeamRecord.isEmpty()) {
            holder.record.setVisibility(View.GONE);
        } else {
            holder.record.setText(mTeamRecord);
        }

        holder.breakdown.setText(mTeamBreakdown);

        return convertView;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamName;
        TextView rank;
        TextView record;
        TextView breakdown;
    }

}
