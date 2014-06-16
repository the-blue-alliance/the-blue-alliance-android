package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class NavDrawerItem implements ListItem {

    private int id;
    private String title;
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
            ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(icon);
        }
        TextView textView = ((TextView) convertView.findViewById(R.id.title));
        textView.setText(title);
        /*if(convertView.isActivated()){
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
        }*/
        return convertView;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setSelected(boolean selected) {

    }
}
