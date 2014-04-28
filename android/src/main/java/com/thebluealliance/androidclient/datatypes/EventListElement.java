package com.thebluealliance.androidclient.datatypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/23/14.
 */
public class EventListElement extends ListElement {

    private String mEventName;
    private String mEventDates;
    private String mEventLocation;

    public EventListElement(String key, String name, String dates, String location) {
        super(key);
        mEventName = name;
        mEventDates = dates;
        mEventLocation = location;
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_event, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView title = (TextView) view.findViewById(R.id.event_name);
            title.setText(mEventName);

            TextView dates = (TextView) view.findViewById(R.id.event_dates);
            dates.setText(mEventDates);

            TextView location = (TextView) view.findViewById(R.id.event_location);
            location.setText(mEventLocation);

            if (view.isSelected()) {
                view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            } else {
                view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }
        }
        return view;
    }
}
