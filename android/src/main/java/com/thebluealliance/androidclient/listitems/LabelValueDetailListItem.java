package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;

/**
 * File created by phil on 7/26/14.
 */
public class LabelValueDetailListItem extends ListElement {

    String label, value, key;
    ListItem listItem;

    public LabelValueDetailListItem(String label, String value, String key) {
        this.label = label;
        this.value = value;
        this.key = key;
        this.listItem = null;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;

        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_summary_detail, null);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.container = (LinearLayout) convertView.findViewById(R.id.summary_container);
            holder.detail = (ImageView) convertView.findViewById(R.id.item_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setText(label);
        if (holder.container.getChildCount() > 2) {
            holder.container.removeViewAt(2);
        }
        if (value != null) {
            holder.value.setVisibility(View.VISIBLE);
            holder.value.setText(value);
        } else {
            holder.value.setVisibility(View.GONE);
            holder.container.addView(listItem.getView(c, inflater, null), 2);
        }

        holder.detail.setOnClickListener(new TeamAtEventClickListener(c, key));

        return convertView;
    }

    private class ViewHolder {
        TextView label;
        TextView value;
        ImageView detail;
        LinearLayout container;
    }
}
