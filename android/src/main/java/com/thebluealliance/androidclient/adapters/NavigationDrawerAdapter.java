package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.NavDrawerItem;

import android.content.Context;

import java.util.List;

public class NavigationDrawerAdapter extends ListViewAdapter {

    public NavigationDrawerAdapter(Context context, List<ListItem> values) {
        super(context, values);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        // Prevents spacers/dividers from being selected
        return getItem(position) instanceof NavDrawerItem;

    }

    /**
     * Get the position for a given navigation item
     *
     * @param id The id of the item to find
     * @return The position of the item in the adapter, or -1 if it is not found.
     */
    public int getPositionForId(int id) {
        for (int i = 0; i < getCount(); i++) {
            ListItem item = getItem(i);
            if (item instanceof NavDrawerItem) {
                if (((NavDrawerItem) item).getId() == id) {
                    return i;
                }
            }
        }

        return -1;
    }
}
