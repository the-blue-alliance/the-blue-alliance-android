package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;

public abstract class ListHeader implements ListItem {
    private final String name;

    public ListHeader() {
        name = "";
    }

    public ListHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return ListViewAdapter.ItemType.HEADER_ITEM.ordinal();
    }

    public String getText() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ListHeader) &&
                ((ListHeader) o).getText().equals(name);
    }

    @Override
    public abstract View getView(Context c, LayoutInflater inflater, View convertView);
}
