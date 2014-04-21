package com.thebluealliance.androidtest.datatypes;

import android.view.LayoutInflater;
import android.view.View;

/**
 * File created by phil on 4/20/14.
 */
public interface ListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
    public void setSelected(boolean selected);
}