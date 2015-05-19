package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.views.SelectableImage;

/**
 * File created by phil on 4/20/14.
 */
public class NavDrawerItem implements ListItem {

    private int id;
    private String title;
    private int titleId = -1;
    private int icon = -1;
    private int layout;

    public NavDrawerItem(String title) {
        this.title = title;
    }

    public NavDrawerItem(int id, String title, int icon, int layout) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.layout = layout;
    }

    public NavDrawerItem(int id, int titleId, int icon, int layout) {
        this.id = id;
        this.titleId = titleId;
        this.icon = icon;
        this.layout = layout;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, null);
        }

        if (icon != -1) {
            ((SelectableImage) convertView.findViewById(R.id.icon)).setImageResource(icon);
        }

        if (titleId != -1) {
            ((TextView) convertView.findViewById(R.id.title)).setText(titleId);
        } else {
            ((TextView) convertView.findViewById(R.id.title)).setText(title != null ? title : "");

        }

        return convertView;
    }

    public int getId() {
        return id;
    }
}
