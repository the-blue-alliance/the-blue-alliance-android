package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * Created by phil on 7/16/14.
 */
public class LabelValueListItem extends ListElement {

    String label, value, intent;
    ListItem listItem;

    public LabelValueListItem(String label, String value, String intent) {
        this.label = label;
        this.value = value;
        this.listItem = null;
        this.intent = intent;
    }
    
    public LabelValueListItem(String label, String value) {
        this.label = label;
        this.value = value;
        this.listItem = null;
        this.intent = "";
    }

    public LabelValueListItem(String label, ListItem value) {
        this.label = label;
        this.listItem = value;
        this.value = null;
        this.intent = "";
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;

        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_summary, null);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.container = (LinearLayout) convertView.findViewById(R.id.summary_container);
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

        return convertView;
    }

    private class ViewHolder {
        TextView label;
        TextView value;
        LinearLayout container;
    }
    
    public String getIntent(){
        return intent;
    }
}
