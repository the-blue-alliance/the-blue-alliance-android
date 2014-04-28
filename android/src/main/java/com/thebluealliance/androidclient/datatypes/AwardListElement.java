package com.thebluealliance.androidclient.datatypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class AwardListElement extends ListElement {

    private String mAwardName;
    private String mAwardWinner;

    public AwardListElement(String key, String name, String winner) {
        super(key);
        mAwardName = name;
        mAwardWinner = winner;
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {

        if (view == null) {
            view = inflater.inflate(R.layout.list_item_award, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView title = (TextView) view.findViewById(R.id.award_name);
            title.setText(mAwardName);

            TextView winner = (TextView) view.findViewById(R.id.award_winner);
            winner.setText(mAwardWinner);

            if (view.isSelected()) {
                view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            } else {
                view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }
        }
        return view;
    }

}