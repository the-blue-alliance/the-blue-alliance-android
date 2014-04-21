package com.thebluealliance.androidtest.datatypes;

import android.view.LayoutInflater;
import android.view.View;

/**
 * File created by phil on 4/20/14.
 */
public class NavDrawerItem implements ListItem {

    private String title;
    private int icon;

    public NavDrawerItem(){

    }

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
        return null;
    }

    @Override
    public void setSelected(boolean selected) {

    }
}
