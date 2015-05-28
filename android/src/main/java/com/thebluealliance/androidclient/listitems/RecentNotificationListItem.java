package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;

/**
 * Created by phil on 7/16/14.
 */
public class RecentNotificationListItem extends ListElement {

    String label, value, intent;

    public RecentNotificationListItem(String label, String value, String intent) {
        this.label = label;
        this.value = value;
        this.intent = intent;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;

        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_recent_notification, null);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.container = (LinearLayout) convertView.findViewById(R.id.summary_container);
            holder.card = convertView.findViewById(R.id.card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setText(label);
        holder.value.setText(value);

        holder.card.setOnClickListener(v -> {
            if (!intent.isEmpty()) {
                c.startActivity(MyTBAHelper.deserializeIntent(intent));
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView label;
        TextView value;
        LinearLayout container;
        View card;
    }

    public String getIntent() {
        return intent;
    }
}
