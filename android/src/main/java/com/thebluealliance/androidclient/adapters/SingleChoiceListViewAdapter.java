package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * File created by phil on 7/28/14.
 */
public class SingleChoiceListViewAdapter extends ArrayAdapter<String> {

    public SingleChoiceListViewAdapter(Context context, List<String> values) {
        super(context, android.R.layout.simple_list_item_single_choice, values);
    }

}
