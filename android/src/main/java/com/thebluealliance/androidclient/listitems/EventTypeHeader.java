package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class EventTypeHeader extends ListHeader {

    public EventTypeHeader(String title) {
        super(title);
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_event_type_header, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.event_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getText());

        return convertView;
    }

    private static class ViewHolder {
        TextView text;
    }
}
