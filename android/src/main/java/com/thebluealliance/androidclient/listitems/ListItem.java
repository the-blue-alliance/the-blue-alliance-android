package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public interface ListItem {
    public int getViewType();

    public View getView(Context c, LayoutInflater inflater, View convertView);
}