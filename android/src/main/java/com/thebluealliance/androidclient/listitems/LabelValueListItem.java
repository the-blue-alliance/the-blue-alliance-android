package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.teamAtEvent.PopulateTeamAtEventSummary;

/**
* Created by phil on 7/16/14.
*/
public class LabelValueListItem extends ListElement {

    String label, value;

    public LabelValueListItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;

        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_summary, null);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setText(label);
        holder.value.setText(value);

        return convertView;
    }

    private class ViewHolder {
        TextView label;
        TextView value;
    }
}
