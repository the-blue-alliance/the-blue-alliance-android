package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.thebluealliance.androidclient.R;

public class DividerListItem implements ListItem {
    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        View v = inflater.inflate(R.layout.list_item_divider, null);
        // Prevent clicks
        v.setEnabled(false);
        return v;
    }
}
