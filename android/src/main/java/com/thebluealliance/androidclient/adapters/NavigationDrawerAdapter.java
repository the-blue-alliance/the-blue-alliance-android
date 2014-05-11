package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.NavDrawerItem;

import java.util.List;

/**
 * Adapter for NavDrawerItems
 *
 * TODO: Update to only take NavDrawerItems instead of ListItems.
 *
 * Created by Nathan on 5/8/2014.
 */
public class NavigationDrawerAdapter extends ListViewAdapter {

    private int mSelectedItemPosition = -1;

    public NavigationDrawerAdapter(Context context, List<ListItem> values) {
        super(context, values, null);
    }

    public void setItemSelected(int position) {
        this.mSelectedItemPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        boolean isSelectedItem = (position == mSelectedItemPosition);
        TextView text = (TextView) v.findViewById(R.id.title);
        if (text != null) {
            text.setTypeface(null, isSelectedItem ? Typeface.BOLD : Typeface.NORMAL);
        }
        return v;
    }

    @Override
    public NavDrawerItem getItem(int position) {
        return (NavDrawerItem) super.getItem(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Get the position for a given navigation item
     *
     * @param id The id of the item to find
     * @return The position of the item in the adapter, or -1 if it is not found.
     */
    public int getPostitionForId(int id) {
        for (int i=0; i<getCount(); i++) {
            if (getItem(i).getId() == id) {
                return i;
            }
        }

        return -1;
    }
}
