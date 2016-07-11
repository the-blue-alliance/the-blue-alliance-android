package com.thebluealliance.androidclient.listitems;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.EventClickListener;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class EventListElement extends ListElement implements Serializable {

    public final String eventName;
    public final String eventDates;
    public final String eventLocation;
    public final String eventKey;
    public final int eventYear;
    public final boolean showMyTba;

    public EventListElement(Event event) throws BasicModel.FieldNotDefinedException {
        super(event.getKey());
        eventKey = "";
        eventName = event.getName();
        eventDates = event.getDateString();
        eventLocation = event.getLocation();
        eventYear = event.getYear();
        this.showMyTba = false;
    }

    public EventListElement(String key, int year, String name, String dates, String location, boolean showMyTba) {
        super(key);
        eventKey = key;
        eventName = name;
        eventDates = dates;
        eventLocation = location;
        eventYear = year;
        this.showMyTba = showMyTba;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventListElement)) {
            return false;
        }
        EventListElement element = ((EventListElement) o);
        return eventName.equals(element.eventName)
          && eventDates.equals(element.eventDates)
          && eventLocation.equals(element.eventLocation)
          && eventKey.equals(element.eventKey);
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
            holder.modelSettings = (ImageView) convertView.findViewById(R.id.model_settings);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /* When rendering in mytba list, show year with event name */
        if (showMyTba) {
            holder.name.setText(String.format("%1$d %2$s", eventYear, eventName));
        } else {
            holder.name.setText(eventName);
        }
        holder.dates.setText(eventDates);
        holder.location.setText(eventLocation);

        if (showMyTba) {
            // When rendered in MyTba, add a specific click listener because we can't add
            // one to the parent ListView
            convertView.setOnClickListener(new EventClickListener(context, eventKey));
            holder.modelSettings.setOnClickListener(new ModelSettingsClickListener(context, eventKey, ModelType.EVENT));
        }
        holder.dates.setVisibility(showMyTba ? View.GONE : View.VISIBLE);
        holder.modelSettings.setVisibility(showMyTba ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView dates;
        TextView location;
        ImageView modelSettings;
    }
}
