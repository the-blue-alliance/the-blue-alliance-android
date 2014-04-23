package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class AwardListElement extends ListElement {

    public AwardListElement(String key, String... texts){
        super(key,texts);
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if(texts.length < 2) return super.getView(inflater, convertView);

        if(view == null){
            view = inflater.inflate(R.layout.award_list_item, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView title = (TextView) view.findViewById(R.id.award_name);
            title.setText(texts[0]);

            TextView winner = (TextView)view.findViewById(R.id.award_winner);
            winner.setText(texts[1]);

            if(view.isSelected()){
                view.setBackgroundResource(android.R.color.holo_blue_light);
            }else{
                view.setBackgroundResource(android.R.color.transparent);
            }
        }
        return view;
    }

}
