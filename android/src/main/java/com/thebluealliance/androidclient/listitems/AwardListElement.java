package com.thebluealliance.androidclient.listitems;

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
    private String mAwardTeam;

    public AwardListElement(String key, String name, String winnerString, String team) {
        super(key);
        mAwardName = name;
        mAwardWinner = winnerString;
        mAwardTeam = team;
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {

        if (view == null) {
            view = inflater.inflate(R.layout.list_item_award, null);
            if(!mAwardTeam.contains(",")) {
                view.setTag("frc" + mAwardTeam);
            }

            TextView title = (TextView) view.findViewById(R.id.award_name);
            title.setText(mAwardName);

            TextView winner = (TextView) view.findViewById(R.id.award_winner);
            winner.setText(mAwardWinner);
        }
        return view;
    }

}