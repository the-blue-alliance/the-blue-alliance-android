package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.thebluealliance.androidclient.datatypes.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class ListViewAdapter extends ArrayAdapter<ListItem> {
    private LayoutInflater mInflater;
    public ArrayList<ListItem> values;
    public ArrayList<String> keys;

    public enum ItemType {
        LIST_ITEM, HEADER_ITEM
    }

    public ListViewAdapter(Context context, ArrayList<ListItem> values, ArrayList<String> keys) {
        super(context, android.R.layout.simple_list_item_1, values);
        this.values = values;
        this.keys = keys;
        mInflater = LayoutInflater.from(context);
    }

    public void removeAt(int index) {
        if (index >= 0) {
            values.remove(index);
            keys.remove(index);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(getContext(), mInflater, convertView);
    }

    public String getKey(int position) {
        return keys.get(position);
    }

    public void updateListData() {
        notifyDataSetChanged();
    }

}
