package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.io.Serializable;

public class EventListElement extends ListElement implements Serializable {

    public final String eventName;
    public final String eventDates;
    public final String eventLocation;
    public final String eventKey;

    public EventListElement(Event event) throws BasicModel.FieldNotDefinedException {
        super(event.getKey());
        eventKey = "";
        eventName = event.getEventName();
        eventDates = event.getDateString();
        eventLocation = event.getLocation();
    }

    public EventListElement(String key, String name, String dates, String location) {
        super(key);
        eventKey = key;
        eventName = name;
        eventDates = dates;
        eventLocation = location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventListElement)) {
            return false;
        }
        EventListElement element = ((EventListElement) o);
        return eventName.equals(element.eventName) &&
          eventDates.equals(element.eventDates) &&
          eventLocation.equals(element.eventLocation) &&
          eventKey.equals(element.eventKey);
    }

    public String getEventKey() {
        return eventKey;
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_event, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.event_name);
            holder.dates = (TextView) convertView.findViewById(R.id.event_dates);
            holder.location = (TextView) convertView.findViewById(R.id.event_location);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(eventName);
        holder.dates.setText(eventDates);
        holder.location.setText(eventLocation);

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView dates;
        TextView location;
    }
}
