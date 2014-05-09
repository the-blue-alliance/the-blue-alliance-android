package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.ListItem;

import java.util.ArrayList;

/**
 * Created by Nathan on 5/8/2014.
 */
public class NavigationDrawerAdapter extends ListViewAdapter {

    private int mSelectedItemPosition = -1;

    public NavigationDrawerAdapter(Context context, ArrayList<ListItem> values, ArrayList<String> keys) {
        super(context, values, keys);
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
}
