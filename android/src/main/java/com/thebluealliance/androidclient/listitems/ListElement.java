package com.thebluealliance.androidclient.listitems;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;

public abstract class ListElement implements ListItem {
    protected final String key;

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

    public String getKey() {
        return key;
    }
}
