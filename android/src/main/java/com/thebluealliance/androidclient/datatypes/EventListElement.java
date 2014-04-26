package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class EventListElement extends ListElement {

    public EventListElement(String key, String... texts) {
        super(key, texts);
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if (texts.length < 3) return super.getView(inflater, convertView);

        if (view == null) {
            view = inflater.inflate(R.layout.event_list_item, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView title = (TextView) view.findViewById(R.id.event_title);
            title.setText(texts[0]);

            TextView dates = (TextView) view.findViewById(R.id.event_dates);
            dates.setText(texts[1]);

            TextView location = (TextView) view.findViewById(R.id.event_location);
            location.setText(texts[2]);

            if (view.isSelected()) {
                view.setBackgroundColor(android.R.color.holo_blue_light);
            } else {
                view.setBackgroundColor(android.R.color.transparent);
            }
        }
        return view;
    }
}
