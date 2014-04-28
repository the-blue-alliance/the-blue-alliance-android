package com.thebluealliance.androidclient.datatypes;

import android.view.View;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;

/**
 * File created by phil on 4/20/14.
 */
public abstract class ListElement implements ListItem {
    protected final String key;
    protected View view;
    protected boolean selected = false;

    public ListElement() {
        key = "";
    }

    public ListElement(String key) {
        this.key = key;
    }

    @Override
    public int getViewType() {
        return ListViewAdapter.ItemType.LIST_ITEM.ordinal();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }
}
