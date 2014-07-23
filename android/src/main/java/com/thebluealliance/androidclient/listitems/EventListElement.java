package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.io.Serializable;

/**
 * File created by phil on 4/23/14.
 */
public class EventListElement extends ListElement implements Serializable{

    private String mEventName;
    private String mEventDates;
    private String mEventLocation;
    private String mEventKey;

    public EventListElement(Event event) throws BasicModel.FieldNotDefinedException {
        super(event.getEventKey());
        mEventName = event.getEventName();
        mEventDates = event.getDateString();
        mEventLocation = event.getLocation();
    }

    public EventListElement(String key, String name, String dates, String location) {
        super(key);
        mEventKey = key;
        mEventName = name;
        mEventDates = dates;
        mEventLocation = location;
    }

    public String getEventKey(){
        return mEventKey;
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

        holder.name.setText(mEventName);
        holder.dates.setText(mEventDates);
        holder.location.setText(mEventLocation);

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView dates;
        TextView location;
    }
}
