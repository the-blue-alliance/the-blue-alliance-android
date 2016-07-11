package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.listitems.ListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<ListItem> {
    private LayoutInflater mInflater;
    public List<ListItem> values;

    public enum ItemType {
        LIST_ITEM, HEADER_ITEM
    }

    public ListViewAdapter(Context context, List<ListItem> values) {
        super(context, android.R.layout.simple_list_item_1, values);
        this.values = values;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(getContext(), mInflater, convertView);
    }
}
