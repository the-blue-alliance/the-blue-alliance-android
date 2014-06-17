package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * File created by phil on 4/20/14.
 */
public interface ListItem {
    public int getViewType();

    public View getView(Context c, LayoutInflater inflater, View convertView);

    public void setSelected(boolean selected);
}