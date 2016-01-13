package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SingleChoiceListViewAdapter extends ArrayAdapter<String> {

    public SingleChoiceListViewAdapter(Context context, List<String> values) {
        super(context, android.R.layout.simple_list_item_single_choice, values);
    }

}
