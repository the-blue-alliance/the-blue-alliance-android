package com.thebluealliance.androidtest.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class EventWeekHeader extends ListHeader {

    public EventWeekHeader(String title){
        super(title);
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        view = inflater.inflate(R.layout.event_week_header, null);

        TextView text = (TextView) view.findViewById(R.id.event_week_separator);
        text.setText(getText());

        return view;
    }
}
