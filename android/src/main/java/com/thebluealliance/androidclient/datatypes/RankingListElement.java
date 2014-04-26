package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class RankingListElement extends ListElement {

    public RankingListElement(String key, String... texts) {
        super(key, texts);
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if (texts.length < 4) return super.getView(inflater, convertView);

        if (view == null) {
            view = inflater.inflate(R.layout.ranking_list_item, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView team = (TextView) view.findViewById(R.id.team_number);
            team.setText(texts[0]);

            TextView rank = (TextView) view.findViewById(R.id.team_ranking); /* formatted as #<rank> (<record>)*/
            if (texts[1].isEmpty()) {
                rank.setVisibility(View.GONE);
            } else {
                rank.setVisibility(View.VISIBLE);
                rank.setText(texts[1]);
            }


            TextView name = (TextView) view.findViewById(R.id.team_name);
            name.setText(texts[2]);

            TextView breakdown = (TextView) view.findViewById(R.id.ranking_breakdown);
            breakdown.setText(texts[3]);

            if (view.isSelected()) {
                view.setBackgroundColor(android.R.color.holo_blue_light);
            } else {
                view.setBackgroundColor(android.R.color.transparent);
            }
        }
        return view;
    }

}
