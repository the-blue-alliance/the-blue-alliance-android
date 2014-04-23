package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class NavDrawerItem implements ListItem {

    private String title;
    private int icon = -1;

    public NavDrawerItem(String title){
        this.title = title;
    }

    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.nav_drawer_item, null);
        }
        if(icon != -1) {
            ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(icon);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(title);
        return convertView;
    }

    @Override
    public void setSelected(boolean selected) {

    }
}
