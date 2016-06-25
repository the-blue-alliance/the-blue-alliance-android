package com.thebluealliance.androidclient.listitems;

import com.thebluealliance.androidclient.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;

public class NotificationTypeListElement extends ListElement {

    private String notificationTitle;
    private boolean enabled;
    private int position;

    public NotificationTypeListElement(String notificationTitle, boolean enabled, int position) {
        this.notificationTitle = notificationTitle;
        this.enabled = enabled;
        this.position = position;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        final ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_notification, null);

            holder = new ViewHolder();
            holder.item = (CheckedTextView) convertView.findViewById(R.id.notification_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item.setText(notificationTitle);
        holder.item.setChecked(enabled);
        holder.item.setOnClickListener(v -> {
            enabled = !enabled;
            holder.item.setChecked(enabled);
        });

        return convertView;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getPosition() {
        return position;
    }

    private static class ViewHolder {
        CheckedTextView item;
    }
}
